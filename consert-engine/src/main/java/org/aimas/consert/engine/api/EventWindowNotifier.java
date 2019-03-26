package org.aimas.consert.engine.api;

import java.util.LinkedList;
import java.util.List;

import org.aimas.consert.model.eventwindow.EventWindow;

public class EventWindowNotifier {
	//private static EventWindowNotifier instance = null;
	
	private List<EventWindowListener> registeredListeners;
	
	private EventWindowNotifier() {
		registeredListeners = new LinkedList<EventWindowListener>();
	}
	
	public void addEventWindowListener(EventWindowListener eventWindowListener) {
		synchronized(registeredListeners) {
			registeredListeners.add(eventWindowListener);
		}
	}
	
	public void removeEventWindowListener(EventWindowListener eventWindowListener) {
		synchronized(registeredListeners) {
			registeredListeners.remove(eventWindowListener);
		}
	}
	
	public void notifyEventWindowSubmitted(EventWindow eventWindow) {
		synchronized(registeredListeners) {
			for (EventWindowListener eventListener : registeredListeners) {
				eventListener.notifyEventWindowSubmitted(eventWindow);
			}
		}
	}
	
	
	public static EventWindowNotifier getNewInstance() {
		//if (instance == null) {
		//	instance = new ContextAssertionNotifier();
		//}
		
		//return instance;
		return new EventWindowNotifier();
	}
}
