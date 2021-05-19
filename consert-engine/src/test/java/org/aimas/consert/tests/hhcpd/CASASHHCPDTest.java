package org.aimas.consert.tests.hhcpd;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.api.ChangePointListener;
import org.aimas.consert.engine.core.EngineRunner;
import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.operators.AnnAfterOperator;
import org.aimas.consert.model.operators.AnnBeforeOperator;
import org.aimas.consert.model.operators.AnnIncludesOperator;
import org.aimas.consert.model.operators.AnnIntersectsOperator;
import org.aimas.consert.model.operators.AnnOverlappedByOperator;
import org.aimas.consert.model.operators.AnnOverlapsOperator;
import org.aimas.consert.model.operators.AnnStartsAfterOperator;
import org.aimas.consert.tests.casas.CASASInterweavedExporter;
import org.aimas.consert.tests.utils.EventReader;
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

public class CASASHHCPDTest extends TestSetup implements ChangePointListener {


	public static final String APPARTMENT = "p20";
	public static final String TEST_FILE = "files/casas_hh/" + APPARTMENT + ".json";
	public static final String VALID_FILE = "files/casas_hh/" + APPARTMENT + "_activity_intervals" + ".json";

	public static void main(String[] args) {
		
		CASASHHCPDTest cpdTest = new CASASHHCPDTest();
		
    	try {
	    	cpdTest.runEvents(TEST_FILE, APPARTMENT);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
	
	private List<ContextAssertion> changePointList;
	private Logger logger;
	
	private CASASHHCPDTest() {
		changePointList = new ArrayList<ContextAssertion>();
	}
	
	@Override
	public void notifyChangePointAdded(ContextAssertion assertion) {
		changePointList.add(assertion);
		logger.info("[CPD] ChangePoint added: " + assertion);
	}
	
	void runEvents(String filepath, String person) throws Exception {
		System.out.println("RUNNING EVENTS FOR file: " + filepath);

		// set up logging
		Properties props = new Properties();
		File logConfigFile = getFileNameFromResources("log4j.properties");
		props.load(new FileInputStream(logConfigFile));
		PropertyConfigurator.configure(props);
		
		logger = Logger.getLogger("assertionLogger");
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
				"casas_hh_cpd_rules/HH_base.drl"
				//,"casas_hh_cpd_rules/HH_leave_home_event.drl"
        );

		kSession.setGlobal("assertionLogger", assertionLogger);

		// set up engine runner thread and event inserter
    	Thread engineRunner = new Thread(new EngineRunner(kSession));
    	File inputFile = getFileNameFromResources(filepath);

    	// set up session clock
    	long testStartTs = System.currentTimeMillis();
    	SessionPseudoClock clock = kSession.getSessionClock();
		clock.advanceTime(testStartTs, TimeUnit.MILLISECONDS);

		EventTracker eventTracker = new EventTracker(kSession);
		EventReader eventReader = new HHEventReader();
		eventTracker.addChangePointListener(this);

    	//CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
		HHSimClockEventInserter eventInserter = new HHSimClockEventInserter(inputFile, eventReader,
				kSession, eventTracker);
    	kSession.addEventListener(eventTracker);
    	
    	// define the list where change points will be collected

    	// start the engine thread and the inserter, wait for the inserter to finish then exit
    	engineRunner.start();
    	eventInserter.start();

    	while (!eventInserter.isFinished()) {
    		Thread.sleep(2000);
    	}

    	eventInserter.stop();
    	engineRunner.join(2000);
    	
    	eventInserter.stop();
    	kSession.halt();
    	kSession.dispose();
    	
    	/** ######## TODO export the changePointList ######## **/
    	
    }
}
