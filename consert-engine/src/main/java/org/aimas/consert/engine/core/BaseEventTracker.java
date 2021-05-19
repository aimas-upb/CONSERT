package org.aimas.consert.engine.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.aimas.consert.engine.api.ChangePointListener;
import org.aimas.consert.engine.api.ChangePointListenerRegistrer;
import org.aimas.consert.engine.api.ChangePointNotifier;
import org.aimas.consert.engine.api.ContextAssertionListener;
import org.aimas.consert.engine.api.ContextAssertionListenerRegistrer;
import org.aimas.consert.engine.api.ContextAssertionNotifier;
import org.aimas.consert.engine.api.EntityDescriptionListener;
import org.aimas.consert.engine.api.EntityDescriptionListenerRegistrer;
import org.aimas.consert.engine.api.EntityDescriptionNotifier;
import org.aimas.consert.engine.api.EventWindowListener;
import org.aimas.consert.engine.api.EventWindowListenerRegistrer;
import org.aimas.consert.engine.api.EventWindowNotifier;
import org.aimas.consert.model.annotations.AnnotationDataFactory;
import org.aimas.consert.model.annotations.DefaultAnnotationDataFactory;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.EntityDescription;
import org.aimas.consert.model.eventwindow.EventWindow;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;

public abstract class BaseEventTracker implements RuleRuntimeEventListener,
		ContextAssertionListenerRegistrer, EntityDescriptionListenerRegistrer, 
		EventWindowListenerRegistrer, EventWindowListener,
		ChangePointListenerRegistrer, ChangePointListener {
	
	protected KieSession kSession;
    protected TrackedAssertionStore trackedAssertionStore;
	
    
    protected AnnotationDataFactory annotationFactory = new DefaultAnnotationDataFactory();
	
	protected ContextAssertionNotifier eventNotifier = ContextAssertionNotifier.getNewInstance();
	protected EntityDescriptionNotifier factNotifier = EntityDescriptionNotifier.getNewInstance();
	protected EventWindowNotifier eventWindowNotifier = EventWindowNotifier.getNewInstance();
	protected ChangePointNotifier changePointNotifier = ChangePointNotifier.getNewInstance();
	
	// general rule execution logger
	protected Logger generalRuleLogger;
	
	// event window management logger
	protected Logger eventWindowLogger;
	
	
	public AnnotationDataFactory getAnnotationFactory() {
		return annotationFactory;
	}
	
	public void setAnnotationFactory(AnnotationDataFactory annotationFactory) {
		this.annotationFactory = annotationFactory;
	}
    
	protected BaseEventTracker(KieSession kSession) {
		this.kSession = kSession;
		kSession.addEventListener(this);

        trackedAssertionStore = TrackedAssertionStore.getNewInstance(kSession);
        
        configureLogging();
 	}
	
	/*
	protected EntryPoint searchEntryPoint(FactHandle handle, KieSession kSession) {
		for (EntryPoint entry : kSession.getEntryPoints()) {
			if (entry.getObject(handle) != null) {
				return entry;
			}
		}
		
		return null;
	}
	*/

	private void configureLogging() {
		FileAppender generalRuleFileAppender = new FileAppender();
	    generalRuleFileAppender.setName("generalRuleLogger");
	    generalRuleFileAppender.setFile("general-rules.log");
	    generalRuleFileAppender.setLayout(new PatternLayout("%-5p [%t, %c, %d{ABSOLUTE}]: %m%n"));
	    generalRuleFileAppender.setThreshold(Level.DEBUG);
	    generalRuleFileAppender.setAppend(false);
	    generalRuleFileAppender.activateOptions();
		
	    FileAppender eventWindowFileAppender = new FileAppender();
	    eventWindowFileAppender.setName("eventWindowLogger");
	    eventWindowFileAppender.setFile("event-window-rules.log");
	    eventWindowFileAppender.setLayout(new PatternLayout("%-5p [%t, %c, %d{ABSOLUTE}]: %m%n"));
	    eventWindowFileAppender.setThreshold(Level.DEBUG);
	    eventWindowFileAppender.setAppend(false);
	    eventWindowFileAppender.activateOptions();
	    
	    generalRuleLogger = Logger.getLogger("generalRuleLogger");
 		eventWindowLogger = Logger.getLogger("eventWindowLogger");
		
		try {
        	// set up logging
     		Properties props = new Properties();
     		File logConfigFile = new File(getClass().getClassLoader().getResource("log4j.properties").getFile());
        	props.load(new FileInputStream(logConfigFile));
        	
        	PropertyConfigurator.configure(props);
		} 
        catch (Exception e) {
			e.printStackTrace();
			
			generalRuleLogger.addAppender(generalRuleFileAppender);
			eventWindowLogger.addAppender(eventWindowFileAppender);
        }
		
		// set loggers as globals in KieSession
		kSession.setGlobal("generalRuleLogger", generalRuleLogger);
		kSession.setGlobal("eventWindowLogger", eventWindowLogger);
	}
	
	
	public KieSession getKnowledgeSession() {
		return kSession;
	}

	public TrackedAssertionStore getTrackedAssertionStore() {
	    return trackedAssertionStore;
    }

	public long getCurrentTime() {
		return kSession.getSessionClock().getCurrentTime();
	}

	
	@Override
    public void addContextAssertionListener(ContextAssertionListener eventListener) {
	    eventNotifier.addContextAssertionListener(eventListener);
    }

	@Override
    public void removeContextAssertionListener(ContextAssertionListener eventListener) {
	    eventNotifier.removeContextAssertionListener(eventListener);
    }

	@Override
    public void addFactListener(EntityDescriptionListener factListener) {
	    factNotifier.addfactListener(factListener);
    }

	@Override
    public void removeFactListener(EntityDescriptionListener factListener) {
	    factNotifier.removefactListener(factListener);
    }
	
	@Override
	public void addEventWindowListener(EventWindowListener eventWindowListener) {
		eventWindowNotifier.addEventWindowListener(eventWindowListener);
	}
	
	@Override
    public void removeEventWindowListener(EventWindowListener eventWindowListener) {
		eventWindowNotifier.removeEventWindowListener(eventWindowListener);
    }
	
	@Override
	public void notifyEventWindowSubmitted(EventWindow eventWindow) {
		eventWindowNotifier.notifyEventWindowSubmitted(eventWindow);
	}
	
	@Override
	public void addChangePointListener(ChangePointListener changePointListener) {
		changePointNotifier.addChangePointListener(changePointListener);
	}

	@Override
	public void removeChangePointListener(ChangePointListener changePointListener) {
		changePointNotifier.removeChangePointListener(changePointListener);
	}
	
	@Override
	public void notifyChangePointAdded(ContextAssertion assertion) {
		changePointNotifier.notifyChangePointAdded(assertion);
	}
	
	
	public abstract void insertStaticEvent(EntityDescription entityDescription);
	
	public abstract void deleteStaticEvent(EntityDescription entityDescription);
	
	public abstract void insertSimpleEvent(ContextAssertion event, boolean setTimestamp);
	
	public abstract void deleteEvent(ContextAssertion event);
	
	public abstract void insertDerivedEvent(ContextAssertion event);
	
	public abstract void insertEvent(ContextAssertion event);

}
