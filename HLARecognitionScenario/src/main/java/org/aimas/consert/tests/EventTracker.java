package org.aimas.consert.tests;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aimas.consert.eventmodel.AnnotationInfo;
import org.aimas.consert.eventmodel.AnnotationUtils;
import org.aimas.consert.eventmodel.BaseEvent;
import org.aimas.consert.eventmodel.LLA;
import org.aimas.consert.eventmodel.Position;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;


public class EventTracker implements RuleRuntimeEventListener {
	
	KieSession kSession;
	Map<Class<? extends BaseEvent>, List<FactHandle>> lastValidEventMap = new HashMap<Class<? extends BaseEvent>, List<FactHandle>>();
	
	
	public EventTracker(KieSession kSession) {
		this.kSession = kSession;
	}

	@Override
    public void objectInserted(ObjectInsertedEvent event) {
	    // TODO Auto-generated method stub
		
    }

	@Override
    public void objectUpdated(ObjectUpdatedEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void objectDeleted(ObjectDeletedEvent event) {
	    // TODO Auto-generated method stub
	    
    }
	
	private EntryPoint searchEntryPoint(FactHandle handle) {
		for (EntryPoint entry : kSession.getEntryPoints()) {
			if (entry.getObject(handle) != null) {
				return entry;
			}
		}
		
		return null;
	}
	
	
	private Map.Entry<FactHandle, BaseEvent> searchHandleByContent(BaseEvent event) {
		List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
		for (FactHandle existingHandle : handleList) {
			EntryPoint existingEventEntry = searchEntryPoint(existingHandle);
			BaseEvent existingEvent = (BaseEvent)existingEventEntry.getObject(existingHandle);
			
			if (existingEvent.getContentHash() == event.getContentHash()) {
				if (existingEvent.allowsContentContinuity(event)) {
					return new AbstractMap.SimpleEntry<FactHandle, BaseEvent>(existingHandle, existingEvent);
				}
			}
		}
		
		return null;
	}
	
	
    public void insertAtomicEvent(BaseEvent event) {
    	//System.out.println(event.getClass());
    	
    	// if this is the first event of its type
    	if (!lastValidEventMap.containsKey(event.getClass())) {
    		
    		if (AnnotationUtils.allowsConfidenceContinuity(
    	    		event.getAnnotations().getConfidence(), 
    	    		event.getConfidenceValueThreshold())) {
    			
    			System.out.println("CREATING LIST FOR EVENT CLASS: " + event.getClass());
    			
    			// go through with insertion in the map and the KieBase
	    		List<FactHandle> handleList = new LinkedList<FactHandle>();
	    		lastValidEventMap.put(event.getClass(), handleList);
    			
    			if (event instanceof Position) {
	    			FactHandle handle = kSession.getEntryPoint("PositionStream").insert(event);
	    			handleList.add(handle);
	    		}
	    		else if (event instanceof LLA) {
	    			FactHandle handle = kSession.getEntryPoint("LLAStream").insert(event);
	    			handleList.add(handle);
	    		}
    		}
    	}
    	else {
    		// check to see if it matches one of the previous stored events by content
    		Map.Entry<FactHandle, BaseEvent> eventEntry = searchHandleByContent(event);
    		if (eventEntry != null) {
    			// if it DOES match any monitored event by content 
    			FactHandle existingEventHandle = eventEntry.getKey();
    			BaseEvent existingEvent = eventEntry.getValue();
    			
    			// if it allows continuity by annotation
    			if (existingEvent.allowsAnnotationContinuity(event.getAnnotations())) {
	    			double maxTimestamp = AnnotationUtils.maxTimestamp(
	    					existingEvent.getAnnotations().getLastUpdated(), 
	    					event.getAnnotations().getLastUpdated());
	    			
	    			double meanConfidence = AnnotationUtils.meanConfidence(
	    					existingEvent.getAnnotations().getConfidence(),
	    					event.getAnnotations().getConfidence());
	    					
	    			AnnotationInfo updatedAnnotations = new AnnotationInfo(
	    					maxTimestamp, meanConfidence, 
	    					existingEvent.getAnnotations().getStartTime(),
	    					event.getAnnotations().getEndTime()
	    			);
	    			existingEvent.setAnnotations(updatedAnnotations);
	    			
	    			// if the event allows continuity, remove the old instance and insert the new one
	    			EntryPoint existingEventEntry = searchEntryPoint(existingEventHandle);
	    			existingEventEntry.delete(existingEventHandle);
	    			
	    			List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
	    			handleList.remove(existingEventHandle);
	    			
	    			
	    			if (event instanceof Position) {
		    			FactHandle handle = kSession.getEntryPoint("ExtendedPositionStream").insert(existingEvent);
		    			handleList.add(handle);
		    			
		    		}
		    		else if (event instanceof LLA) {
		    			FactHandle handle = kSession.getEntryPoint("ExtendedLLAStream").insert(existingEvent);
		    			handleList.add(handle);
		    		}
    			}
    			// if NO annotation continuity (either because of timestamp or confidence)
    			else {
    				if (AnnotationUtils.allowsConfidenceContinuity(
    	    	    		event.getAnnotations().getConfidence(), 
    	    	    		event.getConfidenceValueThreshold())) {
    					// if allowed by confidence allowed, set the new event as the most recently valid one
    					
    					List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
    					handleList.remove(existingEventHandle);
    					
        				if (event instanceof Position) {
    		    			FactHandle handle = kSession.getEntryPoint("PositionStream").insert(event);
    		    			handleList.add(handle);
    		    		}
    		    		else if (event instanceof LLA) {
    		    			FactHandle handle = kSession.getEntryPoint("LLAStream").insert(event);
    		    			handleList.add(handle);
    		    		}
    				}
	    		}
    		}
    		else {
    			// if it DOES NOT match any monitored event by content
    			if (AnnotationUtils.allowsConfidenceContinuity(
        	    		event.getAnnotations().getConfidence(), 
        	    		event.getConfidenceValueThreshold())) {
    				// if the confidence value check allows it to be inserted,
					// add it to the list of monitored events for this type
    				List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
        			
        			if (event instanceof Position) {
    	    			FactHandle handle = kSession.getEntryPoint("PositionStream").insert(event);
    	    			handleList.add(handle);
    	    		}
    	    		else if (event instanceof LLA) {
    	    			FactHandle handle = kSession.getEntryPoint("LLAStream").insert(event);
    	    			handleList.add(handle);
    	    		}
    			}
    		}
    		
    	}
    }
	
}