package org.aimas.consert.tests;


import java.io.File;

import org.aimas.consert.utils.EventInserter;
import org.kie.api.runtime.KieSession;

/**
 * Created by alex on 06.04.2017.
 */
public class HLATest extends TestSetup {
	
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
    		
    		KieSession kSession = getKieSessionFromResources( "rules/HLA.drl" );

	    	// set up engine runner thread and event inserter
	    	Thread engineRunner = new Thread(new EngineRunner(kSession));
	    	
	    	File inputFile = getFileNameFromResources("files/single_hla_120s_01er_015fd.json");
	    	EventTracker eventTracker = new EventTracker(kSession);
	    	EventInserter eventInserter = new EventInserter(inputFile, eventTracker);
	    	
	    	// start the engine thread and the inserter, wait for the inserter to finish then exit
	    	engineRunner.start();
	    	eventInserter.start();
	    	
	    	while (!eventInserter.isFinished()) {
	    		Thread.sleep(2000);
	    	}
	    	
	    	eventInserter.stop();
	    	
	    	engineRunner.join(10000);
	    	
	    	kSession.halt();
	    	kSession.dispose();
	    	
	    	
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	
    	/*
    	HLATest test = new HLATest();

        File inputFile = getFileNameFromResources("files/single_hla_120s_01er_015fd.json");
        Queue<Object> events = JSONEventReader.parseEvents(inputFile);

        System.out.println(events);
        */
    }
}
