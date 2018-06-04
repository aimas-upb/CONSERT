package org.aimas.consert.tests.casas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.utils.TestSetup;
import org.apache.commons.lang.SystemUtils;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
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
	
    private static class ActivityIntervalData {
    	public long start;
    	public long end;
    	
    	public long relative_start;
    	public long relative_end;
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
	
    
	public static boolean exportToHTML(String person, String task, KieSession session, long testStartTs) {
    	
    	//exportActivityDetectionDelays(person, task, session, testStartTs);
    	
        if (SystemUtils.IS_OS_LINUX)
            MINICONDA_BIN_FOLDER = "/home/alex/miniconda2/bin";
        else
            MINICONDA_BIN_FOLDER = "C:\\Users\\David\\Miniconda2";

        // Check outputHTMLFolder and set default
        File cwd = new File(Paths.get("").toAbsolutePath().toString());
        File parentFolder = cwd.getParentFile();
        
        //String outputHTMLFolder = parentFolder.getAbsolutePath() + File.separator  + "casas-event-visualizer" + File.separator + "outputs" + File.separator + task;
        String outputHTMLFolder = parentFolder.getAbsolutePath() + File.separator + "casas-event-visualizer" + File.separator + "experiment" + File.separator + task;
        
        // Create temporary folder
        Path tmp;
        try {
            tmp = Files.createTempDirectory("consert_");
            System.out.println("[Plotly] Generating JSON outputs @: " + tmp);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Write each JSON file for its respective entrypoint
        ObjectMapper mapper = new ObjectMapper();
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
                        tmp + " --o " + outputHTMLFolder + " --p " + person;
            }
            else
                {
                    command[0]="cmd";
                    command[1]="/C";
                    command[2]=  "activate consert & " +
                            "python ..\\casas-event-visualizer\\casas_plotly_generator.py --f " +
                            tmp + " --o " + outputHTMLFolder + " --p " + person;
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

	
}
