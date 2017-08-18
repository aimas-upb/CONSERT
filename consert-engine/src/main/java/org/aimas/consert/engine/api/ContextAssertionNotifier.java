package org.aimas.consert.engine.api;

import java.util.LinkedList;
import java.util.List;

import org.aimas.consert.model.content.ContextAssertion;

public class ContextAssertionNotifier {
	private static ContextAssertionNotifier instance = null;
	
	private List<ContextAssertionListener> registeredListeners;
	
	private ContextAssertionNotifier() {
		registeredListeners = new LinkedList<ContextAssertionListener>();
	}
	
	public void addEventListener(ContextAssertionListener eventListener) {
		synchronized(registeredListeners) {
			registeredListeners.add(eventListener);
		}
	}
	
	public void removeEventListener(ContextAssertionListener eventListener) {
		synchronized(registeredListeners) {
			registeredListeners.remove(eventListener);
		}
	}
	
	public void notifyEventInserted(ContextAssertion contextAssertion) {
		synchronized(registeredListeners) {
			for (ContextAssertionListener eventListener : registeredListeners) {
				eventListener.notifyAssertionInserted(contextAssertion);
			}
		}
	}
	
	
	public void notifyEventDeleted(ContextAssertion contextAssertion) {
		synchronized(registeredListeners) {
			for (ContextAssertionListener eventListener : registeredListeners) {
				eventListener.notifyAssertionDeleted(contextAssertion);
			}
		}
	}
	
	public static ContextAssertionNotifier getInstance() {
		if (instance == null) {
			instance = new ContextAssertionNotifier();
		}
		
		return instance;
	}
}
