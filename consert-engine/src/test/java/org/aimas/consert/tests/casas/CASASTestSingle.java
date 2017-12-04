package org.aimas.consert.tests.casas;


import java.io.File;

import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.tests.casas.utils.*;
import org.aimas.consert.unittest.AnnOverlapsOperatorTest;
import org.aimas.consert.utils.*;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;


/**
 * Created by alex on 06.04.2017.
 */
public class CASASTestSingle extends TestSetup {
	// TASK 2 - washing hands in sink
    
	public static final String PERSON = "p02";
	public static final String TASK = "t2";
	public static final String TEST_FILE = "files/casas_adlnormal/" + PERSON + "." + TASK + ".json";
	
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
		
		// create a new session
		KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		builderConf.setOption(EvaluatorOption.get(	"annOverlaps", new AnnOverlapsOperator.AnnOverlapsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annOverlappedBy", new AnnOverlappedByOperator.AnnOverlappedByEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensBefore", new AnnBeforeOperator.AnnBeforeEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensAfter", new AnnAfterOperator.AnnAfterEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIncludes", new AnnIncludesOperator.AnnIncludesEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsOperator.AnnIntersectsEvaluatorDefinition()));

		KieSession kSession = getKieSessionFromResources( builderConf,"casas_rules/CASAS_base.drl",  "casas_rules/CASAS_cook.drl", "casas_rules/CASAS_cleanup.drl",
				"casas_rules/CASAS_eat.drl","casas_rules/CASAS_location.drl","casas_rules/CASAS_phone_call.drl","casas_rules/CASAS_wash_hands.drl");
    	// set up engine runner thread and event inserter
    	Thread engineRunner = new Thread(new EngineRunner(kSession));
    	
    	File inputFile = getFileNameFromResources(filepath);
    	
    	EventTracker eventTracker = new EventTracker(kSession);
    	CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
    	
    	kSession.addEventListener(eventTracker);
    	
    	// start the engine thread and the inserter, wait for the inserter to finish then exit
    	engineRunner.start();
    	eventInserter.start();
    	
    	while (!eventInserter.isFinished()) {
    		Thread.sleep(2000);
    	}
    	
    	eventInserter.stop();
    	engineRunner.join(2000);
    	
		// Plot the results. Plots are stored per task (i.e. the actions by all people are grouped per task).
    	CASASPlotlyExporter.exportToHTML(person, task, kSession);
        
    	eventInserter.stop();

    	kSession.halt();
    	kSession.dispose();
    }
}
