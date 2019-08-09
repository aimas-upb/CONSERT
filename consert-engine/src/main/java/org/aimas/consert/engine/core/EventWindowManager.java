package org.aimas.consert.engine.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.engine.api.EventWindowListener;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextAssertionContent;
import org.aimas.consert.model.eventwindow.EventWindow;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class EventWindowManager {
	
	/**
	 * The maximum time difference allowed between the initial/create timestamp of an EventWindow and the first
	 * ContextAssertion which constitutes a support for the window, IF that assertion is OLDER than the init timestamp.
	 */
	public static final int MAX_INIT_TIMEDIFF = 10000;
	
	static class EventWindowInfo {
		EventWindow eventWindow;
		FactHandle eventWindowHandle;
		
		public EventWindowInfo(EventWindow eventWindow, FactHandle fh) {
			this.eventWindow = eventWindow;
			this.eventWindowHandle = fh;
		}

		public EventWindow getEventWindow() {
			return eventWindow;
		}

		public void setEventWindow(EventWindow eventWindow) {
			this.eventWindow = eventWindow;
		}

		public FactHandle getEventWindowHandle() {
			return eventWindowHandle;
		}

		public void setEventWindowHandle(FactHandle fh) {
			this.eventWindowHandle = fh;
		}
	}
	
	private Map<ContextAssertionContent, EventWindowInfo> activeWindows;
	private EventWindowListener eventTracker;
	private KieSession kSession;
	
	public EventWindowManager(EventWindowListener eventWindowListener, KieSession kSession) {
		this.eventTracker = eventWindowListener;
		this.kSession = kSession;
		
		activeWindows = new HashMap<>();
	}
	
	
	private boolean isFreshWindow(EventWindow eventWindow, long timestamp) {
		ContextAssertion firstSupportAssertion = eventWindow.getSupportingAssertions().get(0);
		if (timestamp - firstSupportAssertion.getAnnotations().getTimestamp() > MAX_INIT_TIMEDIFF) {
			return false;
		}
		
		return true;
	}
	
	
	public void newWindow(ContextAssertionContent possibleContextAssertionContent, int maxEvents, double maxDuration, ContextAssertion ... supportingAssertions) {
		EventWindow newEventWindow = null;
		long timestamp = kSession.getSessionClock().getCurrentTime();
		
		if (supportingAssertions == null) 
			newEventWindow = new EventWindow(possibleContextAssertionContent, timestamp);
		else 
			newEventWindow = new EventWindow(possibleContextAssertionContent, timestamp,
					Arrays.asList(supportingAssertions), maxEvents, maxDuration);
		
		if (isFreshWindow(newEventWindow, timestamp)) {
			// ONLY consider the new window if the support is NOT older than the max allowed time difference 
		
			// add new window to kSession
			FactHandle eventWindowHandle = kSession.insert(newEventWindow);
			
			if (!activeWindows.containsKey(possibleContextAssertionContent)) {
				// if created for the first time, add to active window list
				activeWindows.put(possibleContextAssertionContent, new EventWindowInfo(newEventWindow, eventWindowHandle));
			}
			else {
				// if overwriting an existing window, then also make sure to delete the existing one from the kSession
				FactHandle existingEventWindowHandle = activeWindows.get(possibleContextAssertionContent).getEventWindowHandle();
				activeWindows.put(possibleContextAssertionContent, new EventWindowInfo(newEventWindow, eventWindowHandle));
				
				// delete previously existing event window
				kSession.delete(existingEventWindowHandle);
			}
		}
		
	}
	
	
	public void newWindow(ContextAssertion possibleContextAssertion, int maxEvents, double maxDuration, ContextAssertion ... supportingAssertions) {
		newWindow(possibleContextAssertion.getAssertionContent(), maxEvents, maxDuration, supportingAssertions);
	}
	
	
	
	public void updateWindow(ContextAssertionContent possibleContextAssertionContent, ContextAssertion ... supportingAssertions) {
		if (supportingAssertions != null) {
			long timestamp = kSession.getSessionClock().getCurrentTime();
			
			if (activeWindows.containsKey(possibleContextAssertionContent)) {
				// if the key exists, extend the existing window
				EventWindow eventWindow = activeWindows.get(possibleContextAssertionContent).getEventWindow();
				eventWindow.addSupportingAssertions(Arrays.asList(supportingAssertions));
				
				
				if (!isFreshWindow(eventWindow, timestamp)) {
					// This would be the case where an initially empty EventWindow is updated with an old event,
					// in which case it should be discarded
					kSession.delete(activeWindows.get(possibleContextAssertionContent).getEventWindowHandle());
				}
				else {
					// update the corresponding fact in the kSession as well
					kSession.update(activeWindows.get(possibleContextAssertionContent).getEventWindowHandle(), eventWindow);
				}
			}
			else {
				// else create a new window
				EventWindow newEventWindow = new EventWindow(possibleContextAssertionContent, timestamp, Arrays.asList(supportingAssertions));
				
				if (isFreshWindow(newEventWindow, timestamp)) {
					// add to kSession
					FactHandle eventWindowHandle = kSession.insert(newEventWindow);
					
					// add to active windows map
					activeWindows.put(possibleContextAssertionContent, new EventWindowInfo(newEventWindow, eventWindowHandle));
				}
			}
		}
	}
	
	public void updateWindow(ContextAssertion possibleContextAssertion, ContextAssertion ... supportingAssertions) {
		updateWindow(possibleContextAssertion.getAssertionContent(), supportingAssertions);
	}
	
	
	
	public void cancelWindow(ContextAssertionContent possibleContextAssertionContent) {
		if (activeWindows.containsKey(possibleContextAssertionContent)) {
			
			// remove from active windows map
			FactHandle eventWindowHandle = activeWindows.remove(possibleContextAssertionContent).getEventWindowHandle();
			
			// remove fact from kSession
			kSession.delete(eventWindowHandle);
		}
	}
	
	public void cancelWindow(ContextAssertion possibleContextAssertion) {
		cancelWindow(possibleContextAssertion.getAssertionContent());
	}
	
	
	
	public boolean existsWindow(ContextAssertionContent possibleContextAssertionContent) {
		return activeWindows.containsKey(possibleContextAssertionContent);
	}
	
	public boolean existsWindow(ContextAssertion possibleContextAssertion) {
		return existsWindow(possibleContextAssertion.getAssertionContent());
	}
	
	
	
	
	public EventWindow getWindow(ContextAssertionContent possiblContextAssertionContent) {
		EventWindowInfo eventWindowInfo = activeWindows.get(possiblContextAssertionContent);
		
		if (eventWindowInfo != null)
			return eventWindowInfo.getEventWindow();
		
		return null;
	}
	
	
	public EventWindow getWindow(ContextAssertion possiblContextAssertion) {
		return getWindow(possiblContextAssertion.getAssertionContent());
	}
	
	
	
	
	public void submitWindow(ContextAssertionContent possibleContextAssertionContent) {
		if(existsWindow(possibleContextAssertionContent)) {
			eventTracker.notifyEventWindowSubmitted(
				activeWindows.get(possibleContextAssertionContent).getEventWindow());
		}
	}
	
	public void submitWindow(ContextAssertion possibleContextAssertion) {
		submitWindow(possibleContextAssertion.getAssertionContent());
	}
	
}
