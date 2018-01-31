package org.aimas.consert.tests.casas;


import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.casas.assertions.Cooking;
import org.aimas.consert.tests.casas.assertions.Eating;
import org.aimas.consert.tests.casas.assertions.Item;
import org.aimas.consert.tests.casas.assertions.PersonLocation;
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
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;


/**
 * Created by alex on 06.04.2017.
 */
public class CASASTestSingle extends TestSetup {
	// TASK 2 - washing hands in sink
    
//	public static final String PERSON = "p04";
//	public static final String TASK = "t4";
//	public static final String TEST_FILE = "files/casas_adlnormal/" + PERSON + "." + TASK + ".json";
	
	public static final String PERSON = "23";
	public static final String TASK = "data-single";
	public static final String TEST_FILE = "files/casas_adlnormal_singles/" + TASK + "-" + PERSON + ".json";

	
	public static void main(String[] args) {
    	
    	try {
	    	runEvents(TEST_FILE, PERSON, TASK);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
	
	private static void prepopulateSession(EventTracker tracker, List<ContextAssertion> assertions) {
		for (ContextAssertion assertion : assertions) {
			System.out.println("[INIT] Prepopulating session with assertion: " + assertion);
			tracker.insertSimpleEvent(assertion, false);
		}
	}
	
	private static List<ContextAssertion> getEatingPrepopulateList(KieSession kSession) {
		// add a single context assertion relating to Cooking
		long endTs = kSession.getSessionClock().getCurrentTime();
		long startTs = endTs - 600000;
		
		DefaultAnnotationData ann = new DefaultAnnotationData(endTs, 1, new Date(startTs), new Date(endTs));
		
		Cooking cooking = new Cooking(ann);
		Item bowl = new Item("I04", "ABSENT", new DefaultAnnotationData(endTs));
		
		List<ContextAssertion> l = new LinkedList<ContextAssertion>();
		l.add(cooking);
		l.add(bowl);
		
		return l;
	}
	
	private static List<ContextAssertion> getCleaningPrepopulateList(KieSession kSession) {
		// add a single context assertion relating to Cooking
		long now = kSession.getSessionClock().getCurrentTime();
		
		long endTs = now;
		long startTs = endTs - 120000;
		
		long locEndTs = now;
		long locStartTs = now - 120000;
		
		DefaultAnnotationData ann = new DefaultAnnotationData(endTs, 1, new Date(startTs), new Date(endTs));
		DefaultAnnotationData locAnn = new DefaultAnnotationData(locEndTs, 1, new Date(locStartTs), new Date(locEndTs));
		
		Eating eating  = new Eating(ann);
		PersonLocation loc = new PersonLocation("DiningRoom", locAnn);
		
		List<ContextAssertion> l = new LinkedList<ContextAssertion>();
		l.add(eating);
		l.add(loc);
		
		return l;
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
				"casas_rules/CASAS_base.drl", "casas_rules/CASAS_location.drl", 
				"casas_rules/CASAS_cook.drl", "casas_rules/CASAS_cleanup.drl",
				"casas_rules/CASAS_eat.drl","casas_rules/CASAS_phone_call.drl",
				"casas_rules/CASAS_wash_hands.drl");
		//kSession.setGlobal("assertionLogger", assertionLogger);
		
		
		
//		// Wash Hands
//		KieSession kSession = getKieSessionFromResources( builderConf,"casas_rules/CASAS_base.drl", "casas_rules/CASAS_location.drl", 
//				"casas_rules/CASAS_wash_hands.drl");
		
		// Cook
//		KieSession kSession = getKieSessionFromResources( builderConf,"casas_rules/CASAS_base.drl", "casas_rules/CASAS_location.drl", 
//				"casas_rules/CASAS_cook.drl");
		
		// Eat
//		KieSession kSession = getKieSessionFromResources( builderConf, kSessionConfig, 
//				"casas_rules/CASAS_base.drl", "casas_rules/CASAS_location.drl", 
//				"casas_rules/CASAS_eat.drl");
    	
    	
		// Cleanup
//		KieSession kSession = getKieSessionFromResources( builderConf,"casas_rules/CASAS_base.drl", "casas_rules/CASAS_location.drl", 
//				"casas_rules/CASAS_cleanup.drl");
		
		// Phone Call
		//KieSession kSession = getKieSessionFromResources( builderConf,"casas_rules/CASAS_base.drl", "casas_rules/CASAS_location.drl", 
		//		"casas_rules/CASAS_phone_call.drl");
		
		// set up engine runner thread and event inserter
    	Thread engineRunner = new Thread(new EngineRunner(kSession));
    	File inputFile = getFileNameFromResources(filepath);
    	
    	// set up session clock
    	long testStartTs = System.currentTimeMillis();
    	SessionPseudoClock clock = kSession.getSessionClock();
		clock.advanceTime(testStartTs, TimeUnit.MILLISECONDS);
    	
		EventTracker eventTracker = new EventTracker(kSession);
    	//CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
		CASASSimClockEventInserter eventInserter = new CASASSimClockEventInserter(inputFile, kSession, eventTracker);
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
    	CASASPlotlyExporter.exportToHTML(person, task, kSession, testStartTs);
    	eventInserter.stop();

    	kSession.halt();
    	kSession.dispose();
    }
}
