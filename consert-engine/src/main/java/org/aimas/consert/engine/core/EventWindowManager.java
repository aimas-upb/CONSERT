package org.aimas.consert.engine.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.engine.api.EventWindowListener;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextAssertionContent;
import org.aimas.consert.model.eventwindow.EventWindow;

public class EventWindowManager {
	
	private Map<ContextAssertionContent, EventWindow> activeWindows;
	private EventWindowListener eventWindowListener;
	
	public EventWindowManager(EventWindowListener eventWindowListener) {
		this.eventWindowListener= eventWindowListener;
		activeWindows = new HashMap<>();
	}
	
	
	public void newWindow(ContextAssertion possibleContextAssertion, ContextAssertion ... supportingAssertions) {
		if (supportingAssertions == null) 
			activeWindows.put(possibleContextAssertion.getAssertionContent(), 
					new EventWindow(possibleContextAssertion.getAssertionContent()));
		else 
			activeWindows.put(possibleContextAssertion.getAssertionContent(), 
					new EventWindow(possibleContextAssertion.getAssertionContent(), 
							Arrays.asList(supportingAssertions)));
	}
	
	
	public void updateWindow(ContextAssertion possibleContextAssertion, ContextAssertion ... supportingAssertions) {
		if (supportingAssertions != null) {
			if (activeWindows.containsKey(possibleContextAssertion.getAssertionContent())) {
				// if the key exists, extend the existing window
				activeWindows.get(possibleContextAssertion.getAssertionContent()).addSupportingAssertions(Arrays.asList(supportingAssertions));
			}
			else {
				// else create a new window
				activeWindows.put(possibleContextAssertion.getAssertionContent(), 
					new EventWindow(possibleContextAssertion.getAssertionContent(), 
							Arrays.asList(supportingAssertions)));
			}
		}
	}
	
	
	public void cancelWindow(ContextAssertion possibleContextAssertion) {
		if (activeWindows.containsKey(possibleContextAssertion.getAssertionContent())) {
			activeWindows.remove(possibleContextAssertion.getAssertionContent());
		}
	}
	
	
	public boolean existsWindow(ContextAssertion possibleContextAssertion) {
		return activeWindows.containsKey(possibleContextAssertion.getAssertionContent());
	}
	
	
	public EventWindow getWindow(ContextAssertion possiblContextAssertion) {
		return activeWindows.get(possiblContextAssertion.getAssertionContent());
	}
	
	
	public void submitWindow(ContextAssertion possibleContextAssertion) {
		if(existsWindow(possibleContextAssertion)) {
			eventWindowListener.notifyEventWindowSubmitted(activeWindows.get(possibleContextAssertion.getAssertionContent()));
		}
	}
	
}
