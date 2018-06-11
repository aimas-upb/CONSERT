package org.aimas.consert.tests.casas;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
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

public class CASASTestInterweavedSingle extends TestSetup {
    
	public static final String PERSON = "p13";
	public static final String TASK = "interweaved";
	public static final String TEST_FILE = "files/casas_adlinterweaved/" + PERSON + "_interweaved" + ".json";

	
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

				//"casas_interwoven_rules/CASAS_watch_DVD.drl",
				//"casas_interwoven_rules/CASAS_phone_call.drl",
				//"casas_interwoven_rules/CASAS_fill_pills.drl");
				//"casas_interwoven_rules/CASAS_write_birthdaycard.drl");
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
    	CASASInterweavedExporter.exportToHTML(person, task, kSession, testStartTs);
    	eventInserter.stop();

    	kSession.halt();
    	kSession.dispose();
    }
}
