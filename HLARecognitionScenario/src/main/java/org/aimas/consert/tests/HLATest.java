package org.aimas.consert.tests;


import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aimas.consert.utils.EventInserter;
import org.aimas.consert.utils.PlotlyExporter;
import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

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
	    	
	    	kSession.addEventListener(new RuleRuntimeEventListener() {
				
				@Override
				public void objectUpdated(ObjectUpdatedEvent event) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void objectInserted(ObjectInsertedEvent event) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void objectDeleted(ObjectDeletedEvent event) {
					// TODO Auto-generated method stub
					
				}
			});

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

			PlotlyExporter.exportToHTML(null, kSession);

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
