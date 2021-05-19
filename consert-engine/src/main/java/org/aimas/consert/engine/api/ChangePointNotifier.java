package org.aimas.consert.engine.api;

import java.util.LinkedList;
import java.util.List;

import org.aimas.consert.model.content.ContextAssertion;

public class ChangePointNotifier {
	
	private List<ChangePointListener> registeredListeners;
	
	private ChangePointNotifier() {
		registeredListeners = new LinkedList<ChangePointListener>();
	}
	
	public void addChangePointListener(ChangePointListener changePointListener) {
		synchronized(registeredListeners) {
			registeredListeners.add(changePointListener);
		}
	}
	
	public void removeChangePointListener(ChangePointListener changePointListener) {
		synchronized(registeredListeners) {
			registeredListeners.remove(changePointListener);
		}
	}
	
	public void notifyChangePointAdded(ContextAssertion assertion) {
		synchronized(registeredListeners) {
			for (ChangePointListener changePointListener : registeredListeners) {
				changePointListener.notifyChangePointAdded(assertion);
			}
		}
	}
	
	
	public static ChangePointNotifier getNewInstance() {
		//if (instance == null) {
		//	instance = new ContextAssertionNotifier();
		//}
		
		//return instance;
		return new ChangePointNotifier();
	}
}
