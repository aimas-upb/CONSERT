package org.aimas.consert.tests.hla;


import org.aimas.consert.engine.EngineRunner;
import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.utils.TestSetup;
import org.kie.api.runtime.KieSession;

import java.io.File;


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
	    	HLAEventInserter eventInserter = new HLAEventInserter(inputFile, eventTracker);
	    	
	    	// start the engine thread and the inserter, wait for the inserter to finish then exit
	    	engineRunner.start();
	    	eventInserter.start();
	    	
	    	while (!eventInserter.isFinished()) {
	    		Thread.sleep(2000);
	    	}
	    	
	    	eventInserter.stop();
	    	
	    	engineRunner.join(10000);
	    	
			
	    	HLAPlotlyExporter.exportToHTML(null, kSession);
            
	    	eventInserter.stop();


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
