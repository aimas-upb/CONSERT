package org.aimas.consert.tests;


import java.io.File;

import org.aimas.consert.utils.EventInserter;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

/**
 * Created by alex on 06.04.2017.
 */
public class HLATest {
	
    public static void main(String[] args) {
    	
    	try {
	    	// load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
	        KieSessionConfiguration config = ks.newKieSessionConfiguration();
	        config.setOption(ClockTypeOption.get("realtime"));
	        
		    KieContainer kContainer = ks.getKieClasspathContainer();
	    	KieSession kSession = kContainer.newKieSession("ksession-rules", config);
	    	
	    	// set up engine runner thread and event inserter
	    	Thread engineRunner = new Thread(new EngineRunner(kSession));
	    	
	    	File inputFile = getFileNameFromResources("files/single_hla_120s_01er_015fd.json");
	    	EventInserter eventInserter = new EventInserter(kSession, inputFile);
	    	
	    	// start the engine thread and the inserter, wait for the inserter to finish then exit
	    	engineRunner.start();
	    	eventInserter.start();
	    	
	    	while (!eventInserter.isFinished()) {
	    		Thread.sleep(2000);
	    	}
	    	
	    	eventInserter.stop();
	    	kSession.halt();
	    	kSession.dispose();
	    	
	    	engineRunner.join(1000);
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

    private static File getFileNameFromResources(String fileName) {
        ClassLoader classLoader = HLATest.class.getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }
}
