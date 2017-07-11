package org.aimas.consert.tests.casas.utils;


import java.io.File;

import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.utils.TestSetup;
import org.kie.api.runtime.KieSession;


/**
 * Created by alex on 06.04.2017.
 */
public class CASASTest extends TestSetup {
    
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
    		
    		KieSession kSession = getKieSessionFromResources( "casas_rules/CASAS.drl" );

	    	// set up engine runner thread and event inserter
	    	Thread engineRunner = new Thread(new EngineRunner(kSession));
	    	
	    	File inputFile = getFileNameFromResources("files/casas_parsed/p01.t1.json");
	    	EventTracker eventTracker = new EventTracker(kSession);
	    	CASASEventInserter eventInserter = new CASASEventInserter(inputFile, eventTracker);
	    	
	    	// start the engine thread and the inserter, wait for the inserter to finish then exit
	    	engineRunner.start();
	    	eventInserter.start();
	    	
	    	while (!eventInserter.isFinished()) {
	    		Thread.sleep(2000);
	    	}
	    	
	    	eventInserter.stop();
	    	
	    	engineRunner.join(5000);
	    	
			
	    	CASASPlotlyExporter.exportToHTML(null, kSession);
            
	    	eventInserter.stop();


	    	kSession.halt();
	    	kSession.dispose();
	    	
	    	
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
