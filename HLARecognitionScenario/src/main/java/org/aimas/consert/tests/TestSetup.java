package org.aimas.consert.tests;

import java.io.File;
import java.util.Collection;

import org.aimas.consert.unittest.AccumulateTestRunner;
import org.aimas.consert.unittest.SerializationHelper;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
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

public class TestSetup {
	public static RuleEngineOption 		PHREAK = RuleEngineOption.PHREAK;
	public static EventProcessingOption STREAM = EventProcessingOption.STREAM;
	
	protected static KieSession getKieSessionFromResources( String... classPathResources ) {
        KieBase kbase = loadKnowledgeBase( null, null, classPathResources );
        return kbase.newKieSession();
    }
    
	protected static KnowledgeBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf, KieBaseConfiguration kbaseConf, String... classPathResources) {
		Collection<KnowledgePackage> knowledgePackages = loadKnowledgePackages(kbuilderConf, classPathResources);

		if (kbaseConf == null) {
			kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		}
		kbaseConf.setOption(PHREAK);
		kbaseConf.setOption(STREAM);
		
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
		kbase.addKnowledgePackages(knowledgePackages);
		try {
			kbase = SerializationHelper.serializeObject(kbase);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return kbase;
	}
    
	
	protected static Collection<KnowledgePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, String... classPathResources) {
        return loadKnowledgePackages(kbuilderConf, true, classPathResources);
    }
	
	
	protected static Collection<KnowledgePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, boolean serialize, String... classPathResources) {
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
    
	
	protected static File getFileNameFromResources(String fileName) {
        ClassLoader classLoader = TestSetup.class.getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }
}
