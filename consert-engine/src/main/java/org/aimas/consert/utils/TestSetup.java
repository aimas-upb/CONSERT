package org.aimas.consert.utils;

import java.io.File;
import java.util.Collection;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class TestSetup {
	public static EventProcessingOption STREAM = EventProcessingOption.STREAM;
	
	public static KieSession getKieSessionFromResources( String... classPathResources ) {
        return getKieSessionFromResources(null, null, classPathResources);
    }
    
	public static KieSession getKieSessionFromResources(KnowledgeBuilderConfiguration kbuilderConf, 
			KieSessionConfiguration kSessionConfig,
			String... classPathResources ) {
		
		KieBase kbase = loadKnowledgeBase( kbuilderConf, null, classPathResources );
		
        return kbase.newKieSession(kSessionConfig, null);
	}
	
	public static KieBase loadKnowledgeBase(KnowledgeBuilderConfiguration kbuilderConf, KieBaseConfiguration kbaseConf, String... classPathResources) {
		Collection<KiePackage> knowledgePackages = loadKnowledgePackages(kbuilderConf, classPathResources);

		if (kbaseConf == null) {
			kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		}
		//kbaseConf.setOption(PHREAK);
		kbaseConf.setOption(STREAM);
		
		InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
		kbase.addPackages(knowledgePackages);
		try {
			kbase = SerializationHelper.serializeObject(kbase);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return kbase;
	}
    
	
	public static Collection<KiePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, String... classPathResources) {
        return loadKnowledgePackages(kbuilderConf, true, classPathResources);
    }
	
	
	public static Collection<KiePackage> loadKnowledgePackages( KnowledgeBuilderConfiguration kbuilderConf, boolean serialize, String... classPathResources) {
		if (kbuilderConf == null) {
			kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		}

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
		for (String classPathResource : classPathResources) {
			kbuilder.add(ResourceFactory.newClassPathResource(classPathResource, TestSetup.class), ResourceType.DRL);
		}

		if (kbuilder.hasErrors()) {
			System.out.println(kbuilder.getErrors().toString());
		}

		Collection<KiePackage> knowledgePackages = null;
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
    
	
	public static File getFileNameFromResources(String fileName) {
        ClassLoader classLoader = TestSetup.class.getClassLoader();
        //System.out.println("[TEST_SETUP] Getting file: " + fileName);
        return new File(classLoader.getResource(fileName).getFile());
    }
}
