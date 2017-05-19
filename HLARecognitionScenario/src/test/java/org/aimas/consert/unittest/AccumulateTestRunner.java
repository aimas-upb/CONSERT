package org.aimas.consert.unittest;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aimas.consert.tests.EngineRunner;
import org.aimas.consert.tests.EventTracker;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;

/**
 * Created by alex on 06.04.2017.
 */
public class AccumulateTestRunner {
	public static RuleEngineOption phreak = RuleEngineOption.PHREAK;
	
    public static void main(String[] args) {
    	
    	try {
	    	// load up the knowledge base
//	        KieServices ks = KieServices.Factory.get();
//	        KieSessionConfiguration config = ks.newKieSessionConfiguration();
//	        config.setOption(ClockTypeOption.get("realtime"));
//	        
//		    KieContainer kContainer = ks.getKieClasspathContainer();
//	    	KieSession kSession = kContainer.newKieSession("ksession-rules", config);

    		KieSession kSession = getKieSessionFromResources( "rules/accumulateTest.drl" );
            final List<Long> results = new ArrayList<Long>();

            kSession.setGlobal( "results", results );
    		
	    	// set up engine runner thread and event inserter
	    	Thread engineRunner = new Thread(new EngineRunner(kSession));
	    	
	    	EventTracker eventTracker = new EventTracker(kSession);
	    	TestInserter eventInserter = new TestInserter(eventTracker);
	    	
	    	// start the engine thread and the inserter, wait for the inserter to finish then exit
	    	engineRunner.start();
	    	eventInserter.start();
	    	
	    	while (!eventInserter.isFinished()) {
	    		Thread.sleep(2000);
	    	}
	    	
	    	eventInserter.stop();
	    	
	    	// verify if results only contain values <= 1 
	    	System.out.println(results);
	    	for (Long l : results) {
	    		if (l > 1) {
	    			System.out.println("IT DOES NOT WORK! GOT A COUNT GREATER THAN 1.");
	    			break;
	    		}
	    	}
	    	System.out.println("WE HAVE A WINNER. ALL COUNTS WITHIN WINDOW ARE <=1.");
	    	
	    	
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
    
    private static KieSession getKieSessionFromResources( String... classPathResources ) {
        KieBase kbase = loadKnowledgeBase( null, null, classPathResources );
        return kbase.newKieSession();
    }
    
    private static KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf, KieBaseConfiguration kbaseConf, String... classPathResources) {
		Collection<KnowledgePackage> knowledgePackages = loadKnowledgePackages(kbuilderConf, classPathResources);

		if (kbaseConf == null) {
			kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		}
		kbaseConf.setOption(phreak);
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
		kbase.addKnowledgePackages(knowledgePackages);
		try {
			kbase = SerializationHelper.serializeObject(kbase);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return kbase;
	}
    
    private static Collection<KnowledgePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, String... classPathResources) {
        return loadKnowledgePackages(kbuilderConf, true, classPathResources);
    }

	private static Collection<KnowledgePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, boolean serialize, String... classPathResources) {
		if (kbuilderConf == null) {
			kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		}

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
		for (String classPathResource : classPathResources) {
			kbuilder.add(ResourceFactory.newClassPathResource(classPathResource, AccumulateTestRunner.class), ResourceType.DRL);
		}

		if (kbuilder.hasErrors()) {
			System.out.println(kbuilder.getErrors().toString());
		}

		Collection<KnowledgePackage> knowledgePackages = null;
        if ( serialize ) {
            try {
                knowledgePackages = SerializationHelper.serializeObject(kbuilder.getKnowledgePackages(),  ((KnowledgeBuilderConfigurationImpl)kbuilderConf).getClassLoader() );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            knowledgePackages = kbuilder.getKnowledgePackages();
        }
		return knowledgePackages;
	}
    
    private static File getFileNameFromResources(String fileName) {
        ClassLoader classLoader = AccumulateTestRunner.class.getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }
}
