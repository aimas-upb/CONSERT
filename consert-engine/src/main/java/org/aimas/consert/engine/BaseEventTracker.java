package org.aimas.consert.engine;

import org.aimas.consert.engine.api.ContextAssertionListenerRegistrer;
import org.aimas.consert.engine.api.EntityDescriptionListenerRegistrer;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.EntityDescription;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;

public abstract class BaseEventTracker implements RuleRuntimeEventListener,
		ContextAssertionListenerRegistrer, EntityDescriptionListenerRegistrer {
	
	protected KieSession kSession;
    protected TrackedAssertionStore trackedAssertionStore;
	
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



	public abstract void insertStaticEvent(EntityDescription entityDescription);
	
	public abstract void deleteStaticEvent(EntityDescription entityDescription);
	
	public abstract void insertSimpleEvent(ContextAssertion event, boolean setTimestamp);
	
	public abstract void deleteEvent(ContextAssertion event);
	
	public abstract void insertDerivedEvent(ContextAssertion event);
	
	public abstract void insertEvent(ContextAssertion event);
}
