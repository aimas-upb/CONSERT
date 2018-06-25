package org.aimas.consert.tests.casas;


import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.casas.utils.AnnAfterOperator;
import org.aimas.consert.tests.casas.utils.AnnBeforeOperator;
import org.aimas.consert.tests.casas.utils.AnnIncludesOperator;
import org.aimas.consert.tests.casas.utils.AnnIntersectsOperator;
import org.aimas.consert.tests.casas.utils.AnnOverlappedByOperator;
import org.aimas.consert.tests.casas.utils.AnnOverlapsOperator;
import org.aimas.consert.tests.casas.utils.AnnStartsAfterOperator;
import org.aimas.consert.utils.AssertionLogger;
import org.aimas.consert.utils.TestSetup;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CASASTestInterweavedAll extends TestSetup {

	public static ObjectMapper mapper;
	public static ObjectNode jsonActivities[];
	public static String PERSON = "p17";
	public static final String[] Persons = {"p17"};
	public static final String TASK = "interweaved";
	public static String TEST_FILE = "files/casas_adlinterweaved/" + PERSON + "_interweaved" + ".json";
	public static String VALID_FILE = "files/casas_adlinterweaved/" + PERSON + "_activity_intervals" + ".json";
	public static final String [] activities = {"PhoneCall", "WatchDVD", "FillDispenser", "ChoosingOutfit", "Cleaning","WaterPlants","PreparingSoup", "WriteBirthdayCard"};


	public static void main(String[] args)
	{
			mapper = new ObjectMapper();

			ObjectNode activitiesJSON =  mapper.createObjectNode();

			jsonActivities = new ObjectNode[8];
			for (int i = 0; i<8; i++)
				jsonActivities[i] = mapper.createObjectNode();
		String datasetFolderPath = "files/" + "casas_adlinterweaved";

		File casasFolder = getFileNameFromResources(datasetFolderPath);
		File[] datasetFiles = casasFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (pathname.getName().startsWith("p") && !pathname.getName().contains("activity_intervals"));
			}
		});

		for (File f : datasetFiles)
		{
			String filename = f.getName();
			PERSON = filename.split("\\.")[0].split("_")[0];
			TEST_FILE = "files/casas_adlinterweaved/" +  PERSON + "_interweaved" + ".json";
			VALID_FILE = "files/casas_adlinterweaved/" + PERSON + "_activity_intervals" + ".json";
		//	if (PERSON.startsWith("p20"))
			try {
				runEvents(TEST_FILE, PERSON, TASK);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		for (int i=1; i<=8; i++)
		{
			activitiesJSON.put(Integer.toString(i), jsonActivities[i-1]);
		}
		System.out.println(activitiesJSON);
	}
		
		
		private static void runEvents(String filepath, String person, String task) throws Exception {
			System.out.println("RUNNING EVENTS FOR file: " + "files/casas_adlinterweaved/" + person + "_interweaved" + ".json");
			
			// set up logging
			Properties props = new Properties();
			File logConfigFile = getFileNameFromResources("log4j.properties");
			props.load(new FileInputStream(logConfigFile));
			PropertyConfigurator.configure(props);
			
			Logger logger = Logger.getLogger("assertionLogger");
			AssertionLogger assertionLogger = new AssertionLogger(logger);
			
			// create a new knowledge builder conf
			KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
			builderConf.setOption(EvaluatorOption.get("annOverlaps", new AnnOverlapsOperator.AnnOverlapsEvaluatorDefinition()));
			builderConf.setOption(EvaluatorOption.get("annOverlappedBy", new AnnOverlappedByOperator.AnnOverlappedByEvaluatorDefinition()));
			builderConf.setOption(EvaluatorOption.get("annHappensBefore", new AnnBeforeOperator.AnnBeforeEvaluatorDefinition()));
			builderConf.setOption(EvaluatorOption.get("annHappensAfter", new AnnAfterOperator.AnnAfterEvaluatorDefinition()));
			builderConf.setOption(EvaluatorOption.get("annIncludes", new AnnIncludesOperator.AnnIncludesEvaluatorDefinition()));
			builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsOperator.AnnIntersectsEvaluatorDefinition()));
			builderConf.setOption(EvaluatorOption.get("annStartsAfter", new AnnStartsAfterOperator.AnnStartsAfterEvaluatorDefinition()));
			
			// create a new kie session conf
			KieSessionConfiguration kSessionConfig = KieServices.Factory.get().newKieSessionConfiguration();
			kSessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );

			KieSession kSession = getKieSessionFromResources( builderConf, kSessionConfig,
					"casas_interwoven_rules/CASAS_base.drl", "casas_interwoven_rules/CASAS_location.drl",

					"casas_interwoven_rules/CASAS_cleaning.drl",
					"casas_interwoven_rules/CASAS_watch_DVD.drl",
					"casas_interwoven_rules/CASAS_phone_call.drl",
					"casas_interwoven_rules/CASAS_fill_pills.drl",
					"casas_interwoven_rules/CASAS_soup.drl",
					"casas_interwoven_rules/CASAS_outfit.drl",
					"casas_interwoven_rules/CASAS_write_birthdaycard.drl",
					"casas_interwoven_rules/CASAS_water_plants.drl");
			
			//kSession.setGlobal("assertionLogger", assertionLogger);
			
					
			// set up engine runner thread and event inserter
	    	Thread engineRunner = new Thread(new EngineRunner(kSession));
	    	File inputFile = getFileNameFromResources(filepath);
	    	
	    	// set up session clock
	    	long testStartTs = System.currentTimeMillis();
	    	SessionPseudoClock clock = kSession.getSessionClock();
			clock.advanceTime(testStartTs, TimeUnit.MILLISECONDS);
	    	
			EventTracker eventTracker = new EventTracker(kSession);
			EventReader eventReader = new CASASEventReader();
			
	    	//CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
			CASASSimClockEventInserter eventInserter = new CASASSimClockEventInserter(inputFile, eventReader,
					kSession, eventTracker);
	    	kSession.addEventListener(eventTracker);
	    	
	    	//List<ContextAssertion> cleanunpPrepopList = getCleaningPrepopulateList(kSession);
	    	//prepopulateSession(eventTracker, cleanunpPrepopList);
	    	
	    	//List<ContextAssertion> eatingPrepopList = getEatingPrepopulateList(kSession);
	    	//prepopulateSession(eventTracker, eatingPrepopList);
	    	
	    	// start the engine thread and the inserter, wait for the inserter to finish then exit
	    	engineRunner.start();
	    	eventInserter.start();
	    	
	    	while (!eventInserter.isFinished()) {
	    		Thread.sleep(2000);
	    	}
	    	
	    	eventInserter.stop();
	    	engineRunner.join(2000);
	    	
			// Plot the results. Plots are stored per task (i.e. the actions by all people are grouped per task).
	    	CASASInterweavedExporter.exportResults(person, task, kSession, testStartTs);
	    	eventInserter.stop();

			try {
				File f =  getFileNameFromResources(VALID_FILE);
				final InputStream in =new FileInputStream(f);
				ObjectMapper mapper =  new ObjectMapper();

				final JsonNode eventListNode = mapper.readTree(in);


				HashMap<String,String> H = new HashMap<String,String>();
				H.put("FillDispenser","1");
				H.put("WatchDVD","2");
				H.put("WaterPlants","3");
				H.put("PhoneCall","4");
				H.put("WriteBirthdayCard","5");
				H.put("PreparingSoup","6");
				H.put("Cleaning","7");
				H.put("ChoosingOutfit","8");

				for (String act : activities)
				{

					ObjectNode actObject = mapper.createObjectNode();
					ArrayNode actArray = mapper.createArrayNode();

					String entryPointName = "Extended" + act + "Stream";
					EntryPoint entryPoint = kSession.getEntryPoint(entryPointName);
					// there should be only one instance of the final activity,
					// so we only retrieve the first element in the list
					long fstime = 100000000;
					long  lstime = -1;
					long fn=0,tn=0,tp=0,fp=0,tduration;
					int totalHitForActivity = 0;

					long starts[] = new long[100];
					long ends[] = new long[100];
					int nrst = 0;
					if (entryPoint.getObjects() != null && !entryPoint.getObjects().isEmpty())
					{
						Iterator<?> it = entryPoint.getObjects().iterator();
						for (int i = 0; i < entryPoint.getObjects().size(); i++) {
							ObjectNode aux = mapper.createObjectNode();
							ContextAssertion assertion = (ContextAssertion) it.next();

							DefaultAnnotationData ann = (DefaultAnnotationData) assertion.getAnnotations();
							long relativeAssertionStart = ann.getStartTime().getTime() - testStartTs;
							long relativeAssertionEnd = relativeAssertionStart + assertion.getEventDuration();
							starts[nrst] = relativeAssertionStart;
							ends[nrst] = relativeAssertionEnd;
							nrst++;
						}
					}
					int ok = 1;
					while (ok==1)
					{
						ok =0;
						for (int i=0; i<nrst-1; i++)
							if (starts[i]>starts[i+1])
							{
								ok = 1;
								long aux = starts[i]; starts[i]=starts[i+1]; starts[i+1]=aux;
								aux = ends[i]; ends[i] = ends[i+1]; ends[i+1]=aux;
							}
					}
					long finstarts[] = new long[100];
					long finends[] = new long[100];
					int nrfin = 0;
					long curst = starts[0], curend = ends[0];
					finstarts[0] = curst;
					finends[0] = curend;
					for (int i =1; i<nrst; i++)
						if (starts[i]<curend)
						{
							finends[nrfin] = ends[i];
							curend = ends[i];
						}
						else
						{
							nrfin++;
							curst= starts[i]; curend=ends[i];

							finstarts[nrfin] = starts[i];
							finends[nrfin] = ends[i];
						}
					if (nrst>0)
						nrfin++;
				/*	System.out.println(act);
					System.out.println("nrstart "+ nrst + " nrfin " + nrfin);
					for (int i=0; i<nrst; i++)
						System.out.println(starts[i]+ " " + ends[i]);
					for (int i=0; i<nrfin; i++)
						System.out.println(finstarts[i]+ " " + finends[i]);*/

					if (entryPoint.getObjects() != null && !entryPoint.getObjects().isEmpty())
					{
						actObject.put("detected intervals",  entryPoint.getObjects().size());

						if (eventListNode.get(H.get(act)) != null){
							actObject.put("real number of intervals",  eventListNode.get(H.get(act)).size());
						}
						else
							actObject.put("real number of intervals",  0);

						Iterator<?> it = entryPoint.getObjects().iterator();
						long laststart = -1, lastend = -1;
						for (int i = 0; i <nrfin; i++)
						{
							ObjectNode aux = mapper.createObjectNode();

							long relativeAssertionStart = finstarts[i];
							long relativeAssertionEnd = finends[i];
							if (relativeAssertionStart < fstime)
								fstime = relativeAssertionStart;
							if (relativeAssertionEnd > lstime)
								lstime = relativeAssertionEnd;
							if (relativeAssertionStart == laststart)
								relativeAssertionStart = lastend;
							long hitStart = -1;
							long hitEnd = - 1;
							int noHit = 0;
							long gap;
							ArrayNode actArrayaux = mapper.createArrayNode();
							if (eventListNode.get(H.get(act)) == null)
								break;
							long tpint = 0;
							for (int j = 0; j< eventListNode.get(H.get(act)).size(); j++)
							{
								long relativeAssertionStart2 =  eventListNode.get(H.get(act)).get(j).get("interval").get("relative_start").asLong();
								long relativeAssertionEnd2 =  eventListNode.get(H.get(act)).get(j).get("interval").get("relative_end").asLong();

								if (relativeAssertionStart2 < fstime)
									fstime = relativeAssertionStart2;
								if (relativeAssertionEnd2 > lstime)
									lstime = relativeAssertionEnd2;

								if ( (relativeAssertionStart2 >= relativeAssertionStart && relativeAssertionStart2 <= relativeAssertionEnd)||
										(relativeAssertionEnd2 <= relativeAssertionEnd && relativeAssertionEnd2 >= relativeAssertionStart)
										|| (relativeAssertionStart2 <= relativeAssertionStart && relativeAssertionEnd2 >= relativeAssertionStart)) // the 2 intervals overlaps
								{
									if (hitStart ==-1)
										hitStart = relativeAssertionStart2;
									hitEnd = relativeAssertionEnd2;
									noHit++;
									if (noHit>1)
									{
										gap =  (relativeAssertionStart2 - ( eventListNode.get(H.get(act)).get(j-1).get("interval").get("relative_end").asLong()));
										actArrayaux.add(gap);
									}

									long maxst = Long.max(relativeAssertionStart, relativeAssertionStart2);
									long minend = Long.min(relativeAssertionEnd, relativeAssertionEnd2);
									tp += (minend-maxst);
									tpint +=  (minend-maxst);
									//System.out.println(maxst + " " + minend + " " + tp + " " + tpint);
								}
								long EvDuration2 = relativeAssertionEnd2 - relativeAssertionStart2;

							}

							fp += (relativeAssertionEnd-relativeAssertionStart-tpint);
							long hitDuration = hitEnd - hitStart;
							long deltaDurationFromDetectedActivity = Math.abs(hitDuration- (relativeAssertionEnd-relativeAssertionStart));
							long deltaStart = Math.abs(hitStart - relativeAssertionStart);
							long deltaEnd = Math.abs(hitEnd - relativeAssertionEnd);


							totalHitForActivity += noHit;

							if (hitStart !=-1) {
								aux.put("delta start", deltaStart);
								aux.put("delta end", deltaEnd);
								aux.put("delta duration", deltaDurationFromDetectedActivity);
								aux.put("gaps", actArrayaux);
							}
							else
							{
								aux.put("delta start", "N/A");
								aux.put("delta end", "N/A");
								aux.put("delta duration", "N/A");
								aux.put("gaps", "N/A");
							}

							actArray.add(aux);

							lastend = relativeAssertionEnd;
							laststart = relativeAssertionStart;
						}
					}

					if (eventListNode.get(H.get(act)) != null)
					{
							if (entryPoint.getObjects() == null || entryPoint.getObjects().isEmpty())
								actObject.put("detected intervals",  0);
							else
								actObject.put("detected intervals",  entryPoint.getObjects().size());
							actObject.put("real number of intervals",  eventListNode.get(H.get(act)).size());
							Long intervall[] = new Long[100];
							Long intervalr[] = new Long[100];
							for (int j = 0; j < eventListNode.get(H.get(act)).size(); j++) {
								long tpint2 = 0;
								long relativeAssertionStart2 = eventListNode.get(H.get(act)).get(j).get("interval").get("relative_start").asLong();
								long relativeAssertionEnd2 = eventListNode.get(H.get(act)).get(j).get("interval").get("relative_end").asLong();

								if (relativeAssertionStart2 < fstime)
									fstime = relativeAssertionStart2;
								if (relativeAssertionEnd2 > lstime)
									lstime = relativeAssertionEnd2;
								if (entryPoint.getObjects() != null && !entryPoint.getObjects().isEmpty()) {
									Iterator<?> it = entryPoint.getObjects().iterator();
									long laststart = -1, lastend = -1;
									for (int i = 0; i < nrfin; i++)
									{

										long relativeAssertionStart = finstarts[i];
										long relativeAssertionEnd = finends[i];

										if (i>0)
											if (relativeAssertionStart == laststart )
												relativeAssertionStart = lastend;
										if (relativeAssertionEnd > lstime)
											lstime = relativeAssertionEnd;
										if (relativeAssertionEnd2 > lstime)
											lstime = relativeAssertionEnd2;
										if ((relativeAssertionStart2 >= relativeAssertionStart && relativeAssertionStart2 <= relativeAssertionEnd) ||
												(relativeAssertionEnd2 <= relativeAssertionEnd && relativeAssertionEnd2 >= relativeAssertionStart)
												|| (relativeAssertionStart2 <= relativeAssertionStart && relativeAssertionEnd2 >= relativeAssertionStart)) // the 2 intervals overlaps
										{
											long maxst = Long.max(relativeAssertionStart, relativeAssertionStart2);
											long minend = Long.min(relativeAssertionEnd, relativeAssertionEnd2);
											tpint2 += minend - maxst;
											//System.out.println(maxst + " " + minend + "  " + tpint2);
										}
										laststart = relativeAssertionStart;
										lastend  = relativeAssertionEnd;
									}
								}

								fn += (relativeAssertionEnd2 - relativeAssertionStart2 - tpint2);
							}
					}

					if (eventListNode.get(H.get(act)) == null && ((entryPoint.getObjects() == null || entryPoint.getObjects().isEmpty())))
					{
						actObject.put("real number of intervals",  0);
						actObject.put("detected intervals",  0);
					}

						tduration = lstime - fstime;
						tn = tduration - tp -fp - fn;

						actObject.put("hit intervals",  totalHitForActivity);
						actObject.put("intervals", actArray);
						actObject.put("total time", tduration);
						actObject.put("true positive time",  tp);
						actObject.put("true negative time",  tn);
						actObject.put("false positive time",  fp);
						actObject.put("false negative time",  fn);
						double prec, acc, recall;
						prec = (double) tp/ ((double)(tp)+(double)(fp));
						recall = (double) tp/ ((double)(tp)+(double)(fn));
						acc = ((double)(tp)+(double)(tn))/ ((double)(tp)+(double)(fp) + (double)(tn)+(double)(fn));
						actObject.put("precision",  prec);
						actObject.put("accuracy",  recall);
						actObject.put("recall",  acc);
						jsonActivities[Integer.parseInt(H.get(act))-1].put(PERSON,actObject);

				}
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			
	    	kSession.halt();
	    	kSession.dispose();
	    }
}