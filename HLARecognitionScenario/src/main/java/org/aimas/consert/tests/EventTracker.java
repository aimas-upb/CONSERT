package org.aimas.consert.tests;

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
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;


public class EventTracker extends BaseEventTracker {
	
	public static class TrackedEventData {
		private FactHandle existingHandle;
		private EntryPoint existingEventEntryPoint;
		private BaseEvent existingEvent;
		
		public TrackedEventData(FactHandle existingHandle, EntryPoint existingEventEntryPoint, BaseEvent existingEvent) {
	        this.existingHandle = existingHandle;
	        this.existingEventEntryPoint = existingEventEntryPoint;
	        this.existingEvent = existingEvent;
        }

		public FactHandle getHandle() {
			return existingHandle;
		}

		public EntryPoint getEntryPoint() {
			return existingEventEntryPoint;
		}

		public BaseEvent getEventObject() {
			return existingEvent;
		}
	}
	
	
	Map<Class<? extends BaseEvent>, List<FactHandle>> lastValidEventMap = new HashMap<Class<? extends BaseEvent>, List<FactHandle>>();
	
	public EventTracker(KieSession kSession) {
		super(kSession);
	}
	
	
	
	private TrackedEventData searchHandleByContent(BaseEvent event, KieSession kSession) {
		List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
		for (FactHandle existingHandle : handleList) {
			EntryPoint existingEventEntry = searchEntryPoint(existingHandle, kSession);
			BaseEvent existingEvent = (BaseEvent)existingEventEntry.getObject(existingHandle);
			
			if (existingEvent.getContentHash() == event.getContentHash()) {
				if (existingEvent.allowsContentContinuity(event)) {
					return new TrackedEventData(existingHandle, existingEventEntry, existingEvent);
				}
			}
		}
		
		return null;
	}
	
	
    public void insertAtomicEvent(final BaseEvent event) {
    	//System.out.println(event.getClass());
    	
    	// if this is the first event of its type
    	if (!lastValidEventMap.containsKey(event.getClass())) {
    		
    		if (AnnotationUtils.allowsConfidenceContinuity(
    	    		event.getAnnotations().getConfidence(), 
    	    		event.getConfidenceValueThreshold())) {
    			
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
    		else {
    			// Go through with event insertion in its appropriate stream anyway, just don't hold the handle in the lastValidEventMap
    			if (event instanceof Position) {
	    			kSession.getEntryPoint("PositionStream").insert(event);
	    		}
	    		else if (event instanceof LLA) {
	    			kSession.getEntryPoint("LLAStream").insert(event);
	    		}
    		}
    	}
    	else {
    		// execute insertion in regular stream
    		// save handle in case we need to insert it in the lastValidEventMap
    		FactHandle newEventHandle = null;
    		if (event instanceof Position) {
    			newEventHandle = kSession.getEntryPoint("PositionStream").insert(event);
    		}
    		else if (event instanceof LLA) {
    			newEventHandle = kSession.getEntryPoint("LLAStream").insert(event);
    		}
    		
    		final FactHandle finalNewEventHandle = newEventHandle; 
    		
    		// afterwards, do the all continuity verification steps as an atomic action
    		kSession.submit(new KieSession.AtomicAction() {
				
				@Override
				public void execute(KieSession kSession) {
					// check to see if it matches one of the previous stored events by content
		    		TrackedEventData existingEventData = searchHandleByContent(event, kSession);
		    		if (existingEventData != null) {
		    			// if it DOES match any monitored event by content 
		    			FactHandle existingEventHandle = existingEventData.getHandle();
		    			BaseEvent updatedEvent = existingEventData.getEventObject();
		    			
		    			// if it allows continuity by annotation
		    			if (updatedEvent.allowsAnnotationContinuity(event.getAnnotations())) { // time s
			    			// create event clone
							System.out.println( updatedEvent.getProcessingTimeStamp());
		    				double maxTimestamp = AnnotationUtils.maxTimestamp(
			    					updatedEvent.getAnnotations().getLastUpdated(), 
			    					event.getAnnotations().getLastUpdated());
			    			
			    			double meanConfidence = AnnotationUtils.meanConfidence(
			    					updatedEvent.getAnnotations().getConfidence(),
			    					event.getAnnotations().getConfidence());
			    					
			    			AnnotationInfo updatedAnnotations = new AnnotationInfo(
			    					maxTimestamp, meanConfidence, 
			    					updatedEvent.getAnnotations().getStartTime(),
			    					event.getAnnotations().getEndTime()
			    			);
			    			updatedEvent.setAnnotations(updatedAnnotations);
			    			
			    			// if the event allows continuity, remove the old instance and insert the new one
			    			// DO THIS AS ATOMIC ACTION
			    			EntryPoint existingEventEntry = searchEntryPoint(existingEventHandle, kSession);
			    			existingEventEntry.delete(existingEventHandle);
			    			
			    			List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
			    			handleList.remove(existingEventHandle);
			    			
			    			if (event instanceof Position) {
			        			FactHandle handle = kSession.getEntryPoint("ExtendedPositionStream").insert(updatedEvent);
			        			handleList.add(handle);
			        			
			        		}
			        		else if (event instanceof LLA) {
			        			FactHandle handle = kSession.getEntryPoint("ExtendedLLAStream").insert(updatedEvent);
			        			handleList.add(handle);
			        		}
			    			
			    			
			    			// TEST SIZE OF KnowledgeBase
			    			//System.out.println("COUNT OF EVENTS FOR " + existingEventEntry.getEntryPointId() + " IS: " + existingEventEntry.getObjects().size());
		    			}
		    			// if NO annotation continuity (either because of timestamp or confidence)
		    			else {
		    				if (AnnotationUtils.allowsConfidenceContinuity(
		    	    	    		event.getAnnotations().getConfidence(), 
		    	    	    		event.getConfidenceValueThreshold())) {
		    					// if allowed by confidence allowed, set the new event as the most recently valid one
		    					List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
		    					handleList.remove(existingEventHandle);
		    					handleList.add(finalNewEventHandle);
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
		        			handleList.add(finalNewEventHandle);
		    			}
		    		}
					
				}
			});
    		
    		
    	}
    }
    
    
    public void objectDeleted(ObjectDeletedEvent event) {
	    // TODO Auto-generated method stub
	    
    }



	public void objectInserted(ObjectInsertedEvent event) {
	    // TODO Auto-generated method stub
	    
    }


	public void objectUpdated(ObjectUpdatedEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
