package org.aimas.consert.engine.core;

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
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;

public abstract class BaseEventTracker implements RuleRuntimeEventListener,
		ContextAssertionListenerRegistrer, EntityDescriptionListenerRegistrer, 
		EventWindowListenerRegistrer, EventWindowListener {
	
	protected KieSession kSession;
    protected TrackedAssertionStore trackedAssertionStore;
	
    
    protected AnnotationDataFactory annotationFactory = new DefaultAnnotationDataFactory();
	
	protected ContextAssertionNotifier eventNotifier = ContextAssertionNotifier.getNewInstance();
	protected EntityDescriptionNotifier factNotifier = EntityDescriptionNotifier.getNewInstance();
	protected EventWindowNotifier eventWindowNotifier = EventWindowNotifier.getNewInstance();
	
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
	
	
	public abstract void insertStaticEvent(EntityDescription entityDescription);
	
	public abstract void deleteStaticEvent(EntityDescription entityDescription);
	
	public abstract void insertSimpleEvent(ContextAssertion event, boolean setTimestamp);
	
	public abstract void deleteEvent(ContextAssertion event);
	
	public abstract void insertDerivedEvent(ContextAssertion event);
	
	public abstract void insertEvent(ContextAssertion event);
}
