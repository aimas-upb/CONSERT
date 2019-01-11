package org.aimas.consert.tests.casas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.utils.Interval;
import org.aimas.consert.utils.TestSetup;
import org.apache.commons.lang.SystemUtils;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by mihai on 5/20/17.
 */
public class CASASInterweavedExporter {
	
	private static final String PERSON = "person";
	private static final String ACTIVITIES = "activities";
	private static final String NAME = "name";
	private static final String DETECTED = "detected";
	private static final String START_DIFF = "start_diff";
	private static final String END_DIFF = "end_diff";
	
    private static class ActivityData {
    	public ActivityIntervalData interval;
    	public String activity_type;
    }
	
	private static class ActivityIntervalData {
    	public long start;
    	public long end;
    	
    	public long relative_start;
    	public long relative_end;
    }
	
	private static class ActivityDetectionResult {
		public String person;
		public Map<String, DetectionMetrics> metrics; 
	
		public ActivityDetectionResult(String person) {
			this.person = person;
			metrics = new HashMap<String, DetectionMetrics>();
		}
		
		public void addMetrics(String activityName, 
				double precision, double recall, double accuracy, 
				int nrHits, double hitRate, 
				long maxStartDelay, long maxEndDelay) {
			
			metrics.put(activityName, 
					new DetectionMetrics(precision, recall, accuracy, 
							nrHits, hitRate, maxStartDelay, maxEndDelay));
		}
	}
	
	private static class DetectionMetrics {
		public double precision;
		public double recall;
		public double accuracy;
		
		public int nrHits;
		public double hitRate;
		
		public long maxStartDelay;
		public long maxEndDelay;
		
		public DetectionMetrics(double precision, double recall,
                double accuracy, int nrHits, double hitRate,
                long maxStartDelay, long maxEndDelay) {
	        
			this.precision = precision;
	        this.recall = recall;
	        this.accuracy = accuracy;
	        this.nrHits = nrHits;
	        this.hitRate = hitRate;
	        this.maxStartDelay = maxStartDelay;
	        this.maxEndDelay = maxEndDelay;
        }
		
	}
	
	
    private static final String [] activities = {
    	"PhoneCall", "WatchDVD", "FillDispenser", "ChoosingOutfit", 
    	"Cleaning","WaterPlants","PreparingSoup", "WriteBirthdayCard"
    };
    
    
    
    
    private static final HashMap<String, String> activityIdMap = new HashMap<String, String>();
    static {
    	activityIdMap.put("FillDispenser","1");
    	activityIdMap.put("WatchDVD","2");
    	activityIdMap.put("WaterPlants","3");
    	activityIdMap.put("PhoneCall","4");
    	activityIdMap.put("WriteBirthdayCard","5");
    	activityIdMap.put("PreparingSoup","6");
    	activityIdMap.put("Cleaning","7");
    	activityIdMap.put("ChoosingOutfit","8");
    }
    
	public static String MINICONDA_BIN_FOLDER;
	
	public static final Map<String, String> assertionMapping = new HashMap<String, String>() {
		{
			put("WriteBirthdayCard", "WriteBirthdayCard");
			put("FillDispenser", "FillDispenser");
		}
	};
	
	
	private static void exportActivityDetectionDelays(String person, String task, KieSession session, long testStartTs) {
		String inputFileName = person + "_activity_intervals" + ".json";
		String casasFolder = "files" + File.separator + "casas_adlinterweaved";
		
		File inputFile = TestSetup.getFileNameFromResources(casasFolder + File.separator + inputFileName); 
		ObjectMapper mapper =  new ObjectMapper();
		
		Map<String, Object> activityResultsMap = new HashMap<String, Object>();
		List<Object> activityDelayList = new LinkedList<Object>();
		
		activityResultsMap.put(PERSON, person);
		activityResultsMap.put(ACTIVITIES, activityDelayList);
		
		try {
	        final InputStream in = new FileInputStream(inputFile);

            Map<String, ActivityIntervalData> statsMap = mapper.readValue(in, new TypeReference<HashMap<String, ActivityIntervalData>>() {});
            String [] activities = {"Phone_Call", "Wash_hands" , "Cook", "Eat", "Clean"};
            
            for (String act : activities) {
            	Map<String, Object> activityData = new HashMap<String, Object>();
            	activityData.put(NAME, act);
            	
            	String assertionName = assertionMapping.get(act);
            	String entryPointName = "Extended" + assertionName + "Stream";
            	EntryPoint entryPoint = session.getEntryPoint(entryPointName);
            	
            	// there should be only one instance of the final activity, 
            	// so we only retrieve the first element in the list
            	if (entryPoint.getObjects() != null && !entryPoint.getObjects().isEmpty()) {
            		activityData.put(DETECTED, true);
            		
            		ContextAssertion assertion = (ContextAssertion)entryPoint.getObjects().iterator().next();
	            	
            		DefaultAnnotationData ann = (DefaultAnnotationData)assertion.getAnnotations();
            		long relativeAssertionStart = ann.getStartTime().getTime() - testStartTs;
	            	long relativeAssertionEnd = relativeAssertionStart + assertion.getEventDuration();
	            	
	            	long startDiff = relativeAssertionStart - statsMap.get(act).relative_start;
	            	long endDiff = relativeAssertionEnd - statsMap.get(act).relative_end;
	            	
	            	activityData.put(START_DIFF, startDiff);
	            	activityData.put(END_DIFF, endDiff);
            	}
            	else {
            		activityData.put(DETECTED, false);
            	}
            	
            	activityDelayList.add(activityData);
            }
            
            File cwd = new File(Paths.get("").toAbsolutePath().toString());
            File parentFolder = cwd.getParentFile();
            
            String outputFolder = parentFolder.getAbsolutePath() + File.separator  
            		+ "casas-event-visualizer" + File.separator + "experiment" + File.separator + task;
            
            File file = new File(outputFolder + File.separator + person + "-delay-analysis" + ".json");
            mapper.writeValue(file, activityResultsMap);
        }
		catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    
	public static boolean exportResults(String person, String task, KieSession session, long testStartTs) {
    	
    	//exportActivityDetectionDelays(person, task, session, testStartTs);
    	
        if (SystemUtils.IS_OS_LINUX)
            MINICONDA_BIN_FOLDER = "/home/alex/miniconda2/bin";
        else
            MINICONDA_BIN_FOLDER = "C:\\Users\\David\\Miniconda2";

        // Check outputHTMLFolder and set default
        File cwd = new File(Paths.get("").toAbsolutePath().toString());
        File parentFolder = cwd.getParentFile();
        
        //String outputHTMLFolder = parentFolder.getAbsolutePath() + File.separator  + "casas-event-visualizer" + File.separator + "outputs" + File.separator + task;
        String outputFolder = parentFolder.getAbsolutePath() + File.separator + "casas-event-visualizer" + File.separator + "experiment" + File.separator + task;
        
        // Create temporary folder
        Path tmp;
        try {
            tmp = Files.createTempDirectory("consert_");
            System.out.println("[Plotly] Generating JSON outputs @: " + tmp);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        
        ObjectMapper mapper = new ObjectMapper();
        
        // first perform the analysis by computing precision, recall, accuracy and other metrics (e.g. number of hits, detection delay)
        try {
        	// create directory structure if it does not exist
            if (!Files.isDirectory(Paths.get(outputFolder))) {
            	Files.createDirectories(Paths.get(outputFolder));
            }
            
        	// read in the real intervals
	 		String VALID_FILE = "files/casas_adlinterweaved/" + person + "_activity_intervals" + ".json";
	 		File f =  TestSetup.getFileNameFromResources(VALID_FILE);
	 		final InputStream in =new FileInputStream(f);
	 		JsonNode realActivityIntervalsNode = mapper.readTree(in);
	        
	 		ActivityDetectionResult detectionResult = new ActivityDetectionResult(person);
	 		
	        for (String activity : activities) {
	        	String entryPointId = "Extended" + activity + "Stream";
	        	EntryPoint ep = session.getEntryPoint(entryPointId);
	        	
	            analyzeActivityResults(activity, person, 
	            		new ArrayList<Object>(ep.getObjects()), testStartTs, 
	            		realActivityIntervalsNode, mapper, detectionResult);
	        } 
	        
	        File file = new File(outputFolder + File.separator + person + "-detection-metrics" + ".json");
	        mapper.writeValue(file, detectionResult);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // then plot all detected activities
        Collection<EntryPoint> entryPoints = (Collection<EntryPoint>) session.getEntryPoints();
        List<String> entryPointIDs = entryPoints.stream().map(e -> e.getEntryPointId()).collect(Collectors.toList());
        
        for(String entryPointId : entryPointIDs) {
            // if we have an atomic stream
        	if (!entryPointId.startsWith("Extended")) {
            	// if the entrypoint list contains the extended stream  as well, skip the atomic one 
	        	String extendedEntryPointID = "Extended" + entryPointId;
        		//System.out.println("Extended entry point id: " + extendedEntryPointID);
	        	
        		if (entryPointIDs.contains(extendedEntryPointID))
	        		continue;
            }
            
            try {
                EntryPoint ep = session.getEntryPoint(entryPointId);
            	File entryPointTempFile = new File(tmp +  File.separator + "output_" + entryPointId + ".json");
            	
            	mapper.writeValue(entryPointTempFile, ep.getObjects());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        
        // Call the python script which generates a plotly gantt chart
        try {
            // build command (this works only under Linux)
            String[] command = new String[3];
            if (SystemUtils.IS_OS_LINUX) {
                command[0]="bash";
                command[1]="-c";
                command[2]=  "source activate consert; " +
                        "python ../casas-event-visualizer/casas_plotly_generator.py --f " +
                        tmp + " --o " + outputFolder + " --p " + person;
            }
            else
                {
                    command[0]="cmd";
                    command[1]="/C";
                    command[2]=  "activate consert & " +
                            "python ..\\casas-event-visualizer\\casas_plotly_generator.py --f " +
                            tmp + " --o " + outputFolder + " --p " + person;
                }

            // build PATH environment variable value
            StringBuilder sbPath = new StringBuilder(System.getenv("PATH"));
            sbPath.append(":").append(MINICONDA_BIN_FOLDER);

            // create a process builder for the command
            ProcessBuilder pb = new ProcessBuilder(command);

            // set the PATH environment variable
            Map<String,String> env = pb.environment();
            if(env != null)
                env.put("PATH",sbPath.toString());

            // redirect outputs from python back to java
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            // run the process and wait for it
                Process p = pb.start();

            p.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


	private static void analyzeActivityResults(String activity, String person,
            List<? extends Object> events, long testStartTs,
            JsonNode realActivityIntervalsNode, ObjectMapper mapper, 
            ActivityDetectionResult detectionResult) throws JsonProcessingException, IOException {
	    
		List<Interval<Long>> predictedEventIntervals = events.stream()
				.map(e -> getIntervalFromAssertion((ContextAssertion)e, testStartTs))
				.collect(Collectors.toList());
		
		// sort the predictedEventIntervals
		Collections.sort(predictedEventIntervals);
		
		//System.out.println("######## Predicted Intervals for activity " + activity + " ########");
		//System.out.println(predictedEventIntervals);
		
		// first, reduce the events that might overlap others
		for (int i = 0; i < predictedEventIntervals.size() - 1; i++) {
			Interval<Long> interval1 = predictedEventIntervals.get(i);
			Interval<Long> interval2 = predictedEventIntervals.get(i + 1);
			
			if (interval2.covers(interval1)) {
				//System.out.println("Found one");
				predictedEventIntervals.remove(i);
				i--;
				
			}
		}
		
		// collect the real intervals that belong to this activity
		String activityId = activityIdMap.get(activity);
		JsonNode intervalListNode = realActivityIntervalsNode.get(activityId);
		
		if (intervalListNode == null) {
			detectionResult.addMetrics(activity, 1, 1, 1, 
					1, 1, 0, 0);
			return;
		}
		
		List<ActivityData> realIntervalList = 
				mapper.convertValue(intervalListNode, new TypeReference<List<ActivityData>>() {});
		
		List<Interval<Long>> realEventIntervals = realIntervalList.stream()
				.map(i -> getIntervalFromRecordedData(i))
				.collect(Collectors.toList());
		
		// sort real event list
		Collections.sort(realEventIntervals);
		
		//System.out.println("######## Predicted Intervals for activity " + activity + " ########");
		System.out.println(predictedEventIntervals);
		
		//System.out.println("######## Real Intervals for activity " + activity + " ########");
		//System.out.println(realEventIntervals);
		//System.out.println();
		
		int predSize = predictedEventIntervals.size();
		int realSize = realEventIntervals.size();
		
		
		long totalDuration = 0;
		long tp = 0, fp = 0, tn = 0, fn = 0;
		
		int[] hitList = new int[realSize];
		
		long maxStartDelay = 0;
		long maxEndDelay = 0;
		
		int realIdx = 0;
		int predIdx = 0;
		
		if (predSize == 0) {
			// if we predicted nothing
			totalDuration = realEventIntervals.get(realSize - 1).upperLimit() - realEventIntervals.get(0).lowerLimit();
			for (Interval<Long> i : realEventIntervals) 
				fn += intervalLen(i);
		}
		else {
			// compute total activity time span length
			totalDuration = 
				Math.max(predictedEventIntervals.get(predSize - 1).upperLimit(), realEventIntervals.get(realSize - 1).upperLimit()) -
				Math.min(predictedEventIntervals.get(0).lowerLimit(), realEventIntervals.get(0).lowerLimit());
			
			
			while (realIdx < realSize && predIdx < predSize) {
				Interval<Long> rInterval = realEventIntervals.get(realIdx);
				
				// go through predictedIntervals while they are before the
				while (predIdx < predSize) {
					Interval<Long> pInterval = predictedEventIntervals.get(predIdx);
					if (pInterval.isBefore(rInterval)) {
						//System.out.println("pInterval is before rInterval case for activity " + activity 
						//		+ " pInterval: " + pInterval + " and rInterval " + rInterval );
						
						// count this interval as a FP
						fp += intervalLen(pInterval);
						
						predIdx++;
					}
					else {
						// once we have an interval that is not before the current real one, stop
						break;
					}
				}
				
				if (predIdx == predSize) {
					// if we have exhausted all the predicted intervals, break
					break;
				}
				
				// check if the current predictedInterval is after the current real one
				Interval<Long> pInterval = predictedEventIntervals.get(predIdx);
				
				if (pInterval.isAfter(rInterval)) {
					//System.out.println("pInterval is after rInterval case for activity " + activity 
					//		+ " pInterval: " + pInterval + " and rInterval " + rInterval );
					// mark the rInterval as a false negative
					fn += intervalLen(rInterval);
					realIdx++;
				}
				else {
					// if it is not below and not above, it means it must intersect somewhere
					if (rInterval.covers(pInterval)) {
						//System.out.println("rInterval covers pInterval case for activity " + activity 
						//		+ " rInterval: " + rInterval + " and pInterval " + pInterval );
						
						// if the real interval completely covers the predicted one
						// mark the predicted interval as a TP
						tp += intervalLen(pInterval);
						hitList[realIdx] = 1;
						
						// left and right residual intervals from the whole real interval
						Interval<Long> leftRes = pInterval.leftComplementRelativeTo(rInterval);
						Interval<Long> rightRes = pInterval.rightComplementRelativeTo(rInterval);
						
						// mark left as FN, but don't mark right the same, because it may be covered by another predicted interval 
						long leftResLen = intervalLen(leftRes);
						fn += leftResLen;
						
						// update max start and max end delay
						maxStartDelay = updateMaxDelay(maxStartDelay, leftRes);
						maxEndDelay = updateMaxDelay(maxEndDelay, rightRes);
						
						// swap rightRes interval for current real interval before continuing
						realEventIntervals.remove(realIdx);
						realEventIntervals.add(realIdx, rightRes);
						
						// increase predicted index
						predIdx++;
					}
					else if (pInterval.covers(rInterval)) {
						//System.out.println("pInterval covers rInterval case for activity " + activity 
						//		+ " rInterval: " + rInterval + " and pInterval " + pInterval );
						
						// if the predicted interval covers the real one
						// mark the real interval as TP
						tp += intervalLen(rInterval);
						hitList[realIdx] = 1;
						
						// left and right residual intervals from the whole predicted interval
						Interval<Long> leftRes = rInterval.leftComplementRelativeTo(pInterval);
						Interval<Long> rightRes = rInterval.rightComplementRelativeTo(pInterval);
						
						// mark left as FP, but don't mark right the same, because it may be covered by another real interval 
						long leftResLen = intervalLen(leftRes);
						fp += leftResLen;
						
						// update max start and max end delay
						maxStartDelay = updateMaxDelay(maxStartDelay, leftRes);
						maxEndDelay = updateMaxDelay(maxEndDelay, rightRes);
						
						// swap rightRes interval for current predicted interval before continuing
						predictedEventIntervals.remove(predIdx);
						predictedEventIntervals.add(predIdx, rightRes);
						
						// increase real index
						realIdx++;
					}
					else {
						// it means it's an overlap
						// get the intersection and count it as a TP
						tp += intervalLen(pInterval.intersect(rInterval));
						hitList[realIdx] = 1;
						
						Interval<Long> leftRes = rInterval.leftComplementRelativeTo(pInterval);
						if (leftRes != null) {
							//System.out.println("pInterval start before rInterval for activity " + activity 
							//		+ " pInterval: " + pInterval + " and rInterval " + rInterval );
							
							// if pInterval starts before rInterval
							fp += intervalLen(leftRes);
							
							Interval<Long> rightRes = pInterval.rightComplementRelativeTo(rInterval);
							
							maxStartDelay = updateMaxDelay(maxStartDelay, leftRes);
							maxEndDelay = updateMaxDelay(maxEndDelay, rightRes);
							
							// swap rightRes interval for current real interval before continuing
							realEventIntervals.remove(realIdx);
							realEventIntervals.add(realIdx, rightRes);
							
							// increase predicted index
							predIdx++;
						}
						else {
							//System.out.println("rInterval start before pInterval for activity " + activity 
							//		+ " rInterval: " + rInterval + " and pInterval " + pInterval );
							
							// if rInterval starts before pInterval
							leftRes = pInterval.leftComplementRelativeTo(rInterval);
							fn += intervalLen(leftRes);
							
							Interval<Long> rightRes = rInterval.rightComplementRelativeTo(pInterval);
							
							maxStartDelay = updateMaxDelay(maxStartDelay, leftRes);
							maxEndDelay = updateMaxDelay(maxEndDelay, rightRes);
							
							// swap rightRes interval for current predicted interval before continuing
							predictedEventIntervals.remove(predIdx);
							predictedEventIntervals.add(predIdx, rightRes);
							
							// increase real index
							realIdx++;
						}
					}
				}
			}
			
			// consume what is left of each list
			while (predIdx < predSize) {
				Interval<Long> i = predictedEventIntervals.get(predIdx);
				fp += intervalLen(i);
				
				predIdx++;
			}
			
			while (realIdx < realSize) {
				Interval<Long> i = realEventIntervals.get(realIdx);
				fn += intervalLen(i);
				
				realIdx++;
			}
		}
		
		
		tn = totalDuration - tp - fp - fn;
		
		int nrHits = 0;
		for (int i = 0; i < hitList.length; i++)
			if (hitList[i] == 1)
				nrHits++;
		
		double precision = (tp + fp) > 0 ? (double)tp / (tp + fp) : 0;
		double recall = (tp + fn) > 0 ? (double)tp / (tp + fn) : 0;
		
		double accuracy = (double)(tp + tn) / totalDuration;
		double hitRate = (double)nrHits / realSize;
		
		System.out.println("Stats for activity " + activity);
		System.out.println("\t" + "precision: " + precision);
		System.out.println("\t" + "recall: " + recall);
		System.out.println("\t" + "accuracy: " + accuracy);
		System.out.println("\t" + "nrHits: " + nrHits);
		System.out.println("\t" + "hitRate: " + hitRate);
		System.out.println("\t" + "maxStartDelay: " + maxStartDelay);
		System.out.println("\t" + "maxEndDelay: " + maxEndDelay);
		System.out.println();
		
		detectionResult.addMetrics(activity, precision, recall, accuracy, 
				nrHits, hitRate, maxStartDelay, maxEndDelay);
    }

	private static long updateMaxDelay(long currentMaxDelay, Interval<Long> gapInterval) {
		long len = intervalLen(gapInterval);
		
		if (currentMaxDelay < len) 
			return len;
		return currentMaxDelay;
	}
	
	private static Interval<Long> getIntervalFromAssertion(ContextAssertion assertion, long testStartTs) {
		DefaultAnnotationData ann = (DefaultAnnotationData)assertion.getAnnotations();
		
		return new Interval<Long>(ann.getStartTime().getTime() - testStartTs, true, 
								  ann.getEndTime().getTime() - testStartTs, true);
	}
	
	private static Interval<Long> getIntervalFromRecordedData(ActivityData activityData) {
		
		return new Interval<Long>(activityData.interval.relative_start, true, 
								  activityData.interval.relative_end, true);
	}
	
	private static long intervalLen(Interval<Long> interval) {
		if (interval == null)
			return 0;
		
		return interval.upperLimit() - interval.lowerLimit();
	}
	
	
	
}
