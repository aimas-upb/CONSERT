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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CASASTestInterweavedSingle extends TestSetup {

	public static ObjectMapper mapper;
	public static ObjectNode jsonActivities[];
	public static String PERSON = "p13";
	public static final String[] Persons = {"p04","p13","p14","p15","p17","p18","p19", "p20", "p22","p23","p24","p25","p26","p27","p28","p29","p30","p31","p32","p33","p34"};
	public static final String TASK = "interweaved";
	public static final String TEST_FILE = "files/casas_adlinterweaved/" + PERSON + "_interweaved" + ".json";
	public static final String VALID_FILE = "files/casas_adlinterweaved/" + PERSON + "_activity_intervals" + ".json";
	public static final String [] activities = {"PhoneCall", "WatchDVD", "PreparingSoup", "WriteBirthdayCard", "FillDispenser", "ChoosingOutfit"};


	public static void main(String[] args) {

		mapper = new ObjectMapper();

		ObjectNode activitiesJSON =  mapper.createObjectNode();

		jsonActivities = new ObjectNode[8];
		for (int i = 0; i<8; i++)
			jsonActivities[i] = mapper.createObjectNode();
		for (int i =0; i<Persons.length; i++) {
			PERSON = Persons[i];
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

				"casas_interwoven_rules/CASAS_watch_DVD.drl",
				"casas_interwoven_rules/CASAS_phone_call.drl",
				"casas_interwoven_rules/CASAS_fill_pills.drl",
				"casas_interwoven_rules/CASAS_soup.drl",
				"casas_interwoven_rules/CASAS_outfit.drl",
				"casas_interwoven_rules/CASAS_write_birthdaycard.drl");

		
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


			HashMap<String,String> H = new HashMap<String,String>();
			H.put("FillDispenser","1");
			H.put("WatchDVD","2");
			H.put("PhoneCall","4");
			H.put("WriteBirthdayCard","5");
			H.put("PreparingSoup","6");
			H.put("ChoosingOutfit","8");

			for (String act : activities)
			{

				ObjectNode actObject = mapper.createObjectNode();
				ArrayNode actArray = mapper.createArrayNode();

				String entryPointName = "Extended" + act + "Stream";
				EntryPoint entryPoint = kSession.getEntryPoint(entryPointName);
				// there should be only one instance of the final activity,
				// so we only retrieve the first element in the list
				if (entryPoint.getObjects() != null && !entryPoint.getObjects().isEmpty())
				{



					actObject.put("detected intervals",  entryPoint.getObjects().size());
					actObject.put("real number of intervals",  eventListNode.get(H.get(act)).size());

					Iterator<?> it = entryPoint.getObjects().iterator();
					int totalHitForActivity = 0;
					for (int i = 0; i < entryPoint.getObjects().size(); i++)
					{
						ObjectNode aux = mapper.createObjectNode();
						ContextAssertion assertion = (ContextAssertion) it.next();

						DefaultAnnotationData ann = (DefaultAnnotationData) assertion.getAnnotations();
						long relativeAssertionStart = ann.getStartTime().getTime() - testStartTs;
						long relativeAssertionEnd = relativeAssertionStart + assertion.getEventDuration();


						long hitStart = -1;
						long hitEnd = - 1;
						int noHit = 0;
						long gap;
						ArrayNode actArrayaux = mapper.createArrayNode();
						for (int j = 0; j< eventListNode.get(H.get(act)).size(); j++)
						{
							long relativeAssertionStart2 =  eventListNode.get(H.get(act)).get(j).get("interval").get("relative_start").asLong();
							long relativeAssertionEnd2 =  eventListNode.get(H.get(act)).get(j).get("interval").get("relative_end").asLong();

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
							}
							long EvDuration2 = relativeAssertionEnd2 - relativeAssertionStart2;

						}
						long hitDuration = hitEnd - hitStart;
						long deltaDurationFromDetectedActivity = Math.abs(hitDuration- assertion.getEventDuration());
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
					}
					actObject.put("hit intervals",  totalHitForActivity);
					actObject.put("intervals", actArray);
					jsonActivities[Integer.parseInt(H.get(act))-1].put(PERSON,actObject);

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
