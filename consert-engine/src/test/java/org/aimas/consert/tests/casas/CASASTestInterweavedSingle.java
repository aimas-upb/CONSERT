package org.aimas.consert.tests.casas;


import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.model.annotations.ContextAnnotation;
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

public class CASASTestInterweavedSingle extends TestSetup {
    
	public static final String PERSON = "p13";
	public static final String TASK = "interweaved";
	public static final String TEST_FILE = "files/casas_adlinterweaved/" + PERSON + "_interweaved" + ".json";
	public static final String VALID_FILE = "files/casas_adlinterweaved/" + PERSON + "_activity_intervals" + ".json";

	
	public static void main(String[] args) {
    	
    	try {
	    	runEvents(TEST_FILE, PERSON, TASK);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
	
	
	private static void runEvents(String filepath, String person, String task) throws Exception {
		System.out.println("RUNNING EVENTS FOR file: " + filepath);
		
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
				"casas_interwoven_rules/CASAS_watch_DVD.drl",
				"casas_interwoven_rules/CASAS_phone_call.drl",
				"casas_interwoven_rules/CASAS_fill_pills.drl",
				"casas_interwoven_rules/CASAS_soup.drl",
				"casas_interwoven_rules/CASAS_outfit.drl");
				//"casas_interwoven_rules/CASAS_write_birthdaycard.drl");
		
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
    	CASASInterweavedExporter.exportToHTML(person, task, kSession, testStartTs);
    	eventInserter.stop();


		try {
			File f =  getFileNameFromResources(VALID_FILE);
			final InputStream in =new FileInputStream(f);
			ObjectMapper mapper =  new ObjectMapper();

			final JsonNode eventListNode = mapper.readTree(in);

			String [] activities = {"PhoneCall", "WatchDVD", "PreparingSoup", "FillDispenser", "ChoosingOutfit"};
			HashMap<String,String> H = new HashMap<String,String>();
			H.put("FillDispenser","1");
			H.put("WatchDVD","2");
			H.put("PhoneCall","4");
			H.put("PreparingSoup","6");
			H.put("ChoosingOutfit","8");

			for (String act : activities)
			{

				String entryPointName = "Extended" + act + "Stream";
				EntryPoint entryPoint = kSession.getEntryPoint(entryPointName);
				// there should be only one instance of the final activity,
				// so we only retrieve the first element in the list
				if (entryPoint.getObjects() != null && !entryPoint.getObjects().isEmpty())
				{

					System.out.println("detected intervals " + entryPoint.getObjects().size());
					System.out.println("real number of intervals " + eventListNode.get(H.get(act)).size());
					Iterator<?> it = entryPoint.getObjects().iterator();
					int totalHitForActivity = 0;
					for (int i = 0; i < entryPoint.getObjects().size(); i++) {
						ContextAssertion assertion = (ContextAssertion) it.next();

						DefaultAnnotationData ann = (DefaultAnnotationData) assertion.getAnnotations();
						long relativeAssertionStart = ann.getStartTime().getTime() - testStartTs;
						long relativeAssertionEnd = relativeAssertionStart + assertion.getEventDuration();
						System.out.println("detected start " + relativeAssertionStart + " detected end " + relativeAssertionEnd);
						long hitStart = -1;
						long hitEnd = - 1;
						int noHit = 0;
						for (int j = 0; j< eventListNode.get(H.get(act)).size(); j++)
						{
							long relativeAssertionStart2 =  eventListNode.get(H.get(act)).get(j).get("interval").get("relative_start").asLong();
							long relativeAssertionEnd2 =  eventListNode.get(H.get(act)).get(j).get("interval").get("relative_end").asLong();
							System.out.println("interval "  + j + " real start " + relativeAssertionStart2 + " real end " + relativeAssertionEnd2);
							if ( (relativeAssertionStart2 >= relativeAssertionStart && relativeAssertionStart2 <= relativeAssertionEnd)||
									(relativeAssertionEnd2 <= relativeAssertionEnd && relativeAssertionEnd2 >= relativeAssertionStart)
									|| (relativeAssertionStart2 <= relativeAssertionStart && relativeAssertionEnd2 >= relativeAssertionStart)) // the 2 intervals overlaps
							{
								if (hitStart ==-1)
									hitStart = relativeAssertionStart2;
								hitEnd = relativeAssertionEnd2;
								noHit++;
							}
							long EvDuration2 = relativeAssertionEnd2 - relativeAssertionStart2;
					//		System.out.println(eventListNo	de.get(H.get(act)).get(j));
					//		System.out.println( eventListNode.get(H.get(act)).get("interval").get("relative_start"));
					//		System.out.println( eventListNode.get(H.get(act)).get("interval").get("relative_end"));

						}
						long hitDuration = hitEnd - hitStart;
						long deltaDurationFromDetectedActivity = Math.abs(hitDuration- assertion.getEventDuration());
						long deltaStart = Math.abs(hitStart - relativeAssertionStart);
						long deltaEnd = Math.abs(hitEnd - relativeAssertionEnd);
						totalHitForActivity += noHit;

						if (hitStart !=-1) {
							System.out.println("delta duration " + deltaDurationFromDetectedActivity);
							System.out.println("delta start " + deltaStart);
							System.out.println("delta end " + deltaEnd);
						}
						else
							System.out.println("no overlapp :( ");
					}
					System.out.println("hit intervals for activity " + act + " -> "  + totalHitForActivity + " from " +  eventListNode.get(H.get(act)).size());
				}
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
