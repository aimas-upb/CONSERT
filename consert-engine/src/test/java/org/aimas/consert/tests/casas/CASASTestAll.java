package org.aimas.consert.tests.casas;


import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.tests.casas.utils.*;
import org.aimas.consert.utils.TestSetup;
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
public class CASASTestAll extends TestSetup {
    
	public static void main(String[] args) {
    	try {
	    	// load up the knowledge base
    		/*
    		KieServices ks = KieServices.Factory.get();
	        KieSessionConfiguration config = ks.newKieSessionConfiguration();
	        config.setOption(ClockTypeOption.get("realtime"));
	        
		    KieContainer kContainer = ks.getKieClasspathContainer();
	    	KieSession kSession = kContainer.newKieSession("ksession-rules", config);
	    	*/
    		
    		// Read all files from the CASAS Dataset
    		//String datasetFolderPath = "files/" + "casas_adlnormal";
    		//String datasetFolderPath = "files" + File.separator + "casas_adlinterwieved";
    		String datasetFolderPath = "files/" + "casas_adlnormal_singles";
    		
    		File casasFolder = getFileNameFromResources(datasetFolderPath);
    		File[] datasetFiles = casasFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getName().startsWith("p") || (pathname.getName().startsWith("data") && !pathname.getName().contains("activity_intervals")));
				}
			});
	    	
	    	for (File f : datasetFiles) {
	    		String filename = f.getName();
	    		
	    		if (filename.startsWith("data-single")) {
	    			String task = "data-single";
	    			String person = filename.split("\\.")[0].split("-")[2];
	    			runEvents(datasetFolderPath, filename, person, task);
	    		}
	    		else {
		    		String person = filename.split("\\.")[0];
		    		String task = filename.split("\\.")[1];
		    		runEvents(datasetFolderPath, filename, person, task);
	    		}
	    	}
    		
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }

	private static void runEvents(String datasetFolderPath, String filename, String person, String task) throws Exception {
		System.out.println("RUNNING EVENTS FOR file: " + datasetFolderPath + File.separator + filename);
		
		// create a new session
		// create a new knowledge builder conf
		KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		
		builderConf.setOption(EvaluatorOption.get("annOverlaps", new AnnOverlapsOperator.AnnOverlapsEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annOverlappedBy", new AnnOverlappedByOperator.AnnOverlappedByEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensBefore", new AnnBeforeOperator.AnnBeforeEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annHappensAfter", new AnnAfterOperator.AnnAfterEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIncludes", new AnnIncludesOperator.AnnIncludesEvaluatorDefinition()));
		builderConf.setOption(EvaluatorOption.get("annIntersects", new AnnIntersectsOperator.AnnIntersectsEvaluatorDefinition()));
		
		// create a new kie session conf
		KieSessionConfiguration kSessionConfig = KieServices.Factory.get().newKieSessionConfiguration();
		kSessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );

		KieSession kSession = getKieSessionFromResources( builderConf, kSessionConfig,
				"casas_rules/CASAS_base.drl", "casas_rules/CASAS_location.drl", 
				"casas_rules/CASAS_cook.drl", "casas_rules/CASAS_cleanup.drl",
				"casas_rules/CASAS_eat.drl","casas_rules/CASAS_phone_call.drl",
				"casas_rules/CASAS_wash_hands.drl");
		
		//System.out.println("[TESTAL] KSESSION CLOCK at init is: " + kSession.getSessionClock().getCurrentTime());
		
    	// set up engine runner thread and event inserter
    	Thread engineRunner = new Thread(new EngineRunner(kSession));
    	
    	String filePath = datasetFolderPath + "/"+ filename;
    	File inputFile = getFileNameFromResources(filePath);
    	
    	// set up session clock
    	long testStartTs = System.currentTimeMillis();
    	SessionPseudoClock clock = kSession.getSessionClock();
		clock.advanceTime(testStartTs, TimeUnit.MILLISECONDS);
    	
		EventTracker eventTracker = new EventTracker(kSession);
    	//CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
		CASASSimClockEventInserter eventInserter = new CASASSimClockEventInserter(inputFile, kSession, eventTracker);
    	kSession.addEventListener(eventTracker);
    	
    	// start the engine thread and the inserter, wait for the inserter to finish then exit
    	engineRunner.start();
    	eventInserter.start();
    	
    	while (!eventInserter.isFinished()) {
    		Thread.sleep(2000);
    	}
    	
    	eventInserter.stop();
    	engineRunner.join(5000);
    	
    	
		// Plot the results. Plots are stored per task (i.e. the actions by all people are grouped per task).
    	CASASPlotlyExporter.exportToHTML(person, task, kSession, testStartTs);
    	
    	kSession.halt();
    	kSession.dispose();
    	kSession.destroy();
    }
}
