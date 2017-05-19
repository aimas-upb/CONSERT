package org.aimas.consert.tests;

import java.util.Map;

import org.aimas.consert.eventmodel.BaseEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public abstract class BaseEventTracker implements RuleRuntimeEventListener {
	
	protected KieSession kSession;
	
	protected BaseEventTracker(KieSession kSession) {
		this.kSession = kSession;
	}
	
	protected EntryPoint searchEntryPoint(FactHandle handle, KieSession kSession) {
		for (EntryPoint entry : kSession.getEntryPoints()) {
			if (entry.getObject(handle) != null) {
				return entry;
			}
		}
		
		return null;
	}
	
	public abstract void insertAtomicEvent(BaseEvent event);	
}
