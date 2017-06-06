package org.aimas.consert.tests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aimas.consert.eventmodel.AnnotationInfo;
import org.aimas.consert.eventmodel.AnnotationUtils;
import org.aimas.consert.eventmodel.BaseEvent;
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
	Map<Class<? extends BaseEvent>, List<FactHandle>> lastValidDeducedMap = new HashMap<Class<? extends BaseEvent>, List<FactHandle>>();
	
	public EventTracker(KieSession kSession) {
		super(kSession);
		kSession.setGlobal( "eventTracker", this);
	}
	
	
	
	private TrackedEventData searchHandleByContent(Map<Class<? extends BaseEvent>, List<FactHandle>> recencyMap, BaseEvent event, KieSession kSession) {
		List<FactHandle> handleList = recencyMap.get(event.getClass());
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
	
	/**
	 * Insert an atomic event. The event will go through the verifications of temporal continuity.
	 * @param event The atomic event to insert
	 */
    public void insertAtomicEvent(BaseEvent event) {
    	//System.out.println(event.getClass());
    	String eventStream = event.getStreamName();
    	
    	// if this is the first event of its type
    	if (!lastValidEventMap.containsKey(event.getClass())) {
    		
    		if (AnnotationUtils.allowsConfidenceContinuity(
    	    		event.getAnnotations().getConfidence(), 
    	    		event.getConfidenceValueThreshold())) {
    			
    			// go through with insertion in the map and the KieBase
	    		List<FactHandle> handleList = new LinkedList<FactHandle>();
	    		lastValidEventMap.put(event.getClass(), handleList);
    			
	    		FactHandle handle = kSession.getEntryPoint(eventStream).insert(event);
	    		handleList.add(handle);
	    		
	    		/*
	    		if (event instanceof Position) {
	    			FactHandle handle = kSession.getEntryPoint("PositionStream").insert(event);
	    			handleList.add(handle);
	    		}
	    		else if (event instanceof LLA) {
	    			FactHandle handle = kSession.getEntryPoint("LLAStream").insert(event);
	    			handleList.add(handle);
	    		}
	    		*/
    		}
    		else {
    			// Go through with event insertion in its appropriate stream anyway, just don't hold the handle in the lastValidEventMap
    			kSession.getEntryPoint(eventStream).insert(event);
    		}
    	}
    	else {
    		// execute insertion in regular stream
    		// save handle in case we need to insert it in the lastValidEventMap
    		final FactHandle newEventHandle = kSession.getEntryPoint(eventStream).insert(event);
    		
    		// afterwards, do the all continuity verification steps as an atomic action
    		kSession.submit(new KieSession.AtomicAction() {
				
				@Override
				public void execute(KieSession kSession) {
					// check to see if it matches one of the previous stored events by content
		    		TrackedEventData existingEventData = searchHandleByContent(lastValidEventMap, event, kSession);
		    		if (existingEventData != null) {
		    			// if it DOES match any monitored event by content 
		    			FactHandle existingEventHandle = existingEventData.getHandle();
		    			BaseEvent updatedEvent = existingEventData.getEventObject();
		    			EntryPoint existingEventEntry = existingEventData.getEntryPoint();
		    			
		    			// if it allows continuity by annotation
		    			if (updatedEvent.allowsAnnotationContinuity(event.getAnnotations())) {
			    			// create event clone
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
			    			//EntryPoint existingEventEntry = searchEntryPoint(existingEventHandle, kSession);
			    			existingEventEntry.delete(existingEventHandle);
			    			
			    			List<FactHandle> handleList = lastValidEventMap.get(event.getClass());
			    			handleList.remove(existingEventHandle);
			    			
			    			String extendedEventStream = updatedEvent.getExtendedStreamName();
			    			FactHandle handle = kSession.getEntryPoint(extendedEventStream).insert(updatedEvent);
		        			handleList.add(handle);
			    			
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
		    					handleList.add(newEventHandle);
		    					//handleList.add(finalNewEventHandle);
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
		    				handleList.add(newEventHandle);
		    				//handleList.add(finalNewEventHandle);
		    			}
		    		}
					
				}
			});
    		
    		
    	}
    }
    
    /**
     * Insert a derived event. This method will usually be called in the right-hand-side of a 
     * ContextDerivationRule. The method will check for duplicates of events that have already been
     * derived, but which have still not been garbage collected.
     * @param event The event to be inserted
     */
    public void insertDerivedEvent(BaseEvent event) {
    	//kSession.getQueryResults(query, arguments);
    	
    	// Check if event to be inserted exists already
    	if (!checkPreviouslyDerived(event)) {
    		doDerivedInsertion(event);
    	}
    	
    }
    
    
	private boolean checkPreviouslyDerived(BaseEvent derivedEvent) {
		String derivedStreamName = derivedEvent.getExtendedStreamName();
		EntryPoint derivedEventStream = kSession.getEntryPoint(derivedStreamName);
		
		//System.out.println("Derived Event Stream name: " + derivedStreamName);
		
		
		for (Object eventObj : derivedEventStream.getObjects()) {
			BaseEvent event = (BaseEvent) eventObj;
			
			// if the objects have the same content
			if (event.allowsContentContinuity(derivedEvent)) {
				// and they have the same validity interval and confidence annotations
				if (event.getAnnotations().getStartTime().equals(derivedEvent.getAnnotations().getStartTime()) &&
					event.getAnnotations().getEndTime().equals(derivedEvent.getAnnotations().getEndTime()) && 
					event.getAnnotations().getConfidence() == derivedEvent.getAnnotations().getConfidence()) {
					
					System.out.println("[INFO] ::::::::::::::::::::: We have an insertion of an already existing event!!!");
					return true;
				}
			}
		}
	    
		return false;
    }


    
    private void doDerivedInsertion(BaseEvent eventObject) {
		//BaseEvent insertedEventObject = (BaseEvent)insertEvent.getObject();
		
    	String derivedEventStream = eventObject.getExtendedStreamName();
    	
		// perform same type of checks as in the case of temporal validity extension
		// if this is the first event of its type
    	if (!lastValidDeducedMap.containsKey(eventObject.getClass())) {
    		// go through with insertion in the map
    		List<FactHandle> handleList = new LinkedList<FactHandle>();
    		FactHandle handle = kSession.getEntryPoint(derivedEventStream).insert(eventObject);
    		handleList.add(handle);
    		
    		lastValidDeducedMap.put(eventObject.getClass(), handleList);
    	}
    	else {
    		// The newly derived event has to make it to the KnowledgeBase in any case, so we perform the insert here
    		final FactHandle derivedEventHandle = kSession.getEntryPoint(derivedEventStream).insert(eventObject);
    		
    		// do the overlap verification steps as an atomic action
    		kSession.submit(new KieSession.AtomicAction() {
				@Override
				public void execute(KieSession kSession) {
					// check to see if it matches one of the previous stored events by content
		    		TrackedEventData existingEventData = searchHandleByContent(lastValidDeducedMap, eventObject, kSession);
		    		if (existingEventData != null) {
		    			// if it DOES match any monitored event by content 
		    			FactHandle existingEventHandle = existingEventData.getHandle();
		    			BaseEvent updatedEvent = existingEventData.getEventObject();
		    			EntryPoint existingEventEntry = existingEventData.getEntryPoint();
		    			
		    			// if the validity interval of the previous deduced event is included in the interval of the extended one 
		    			if (updatedEvent.isOverlappedBy(eventObject)) {
			    			System.out.println("!!!!!!!!!![EventTracker] Analyzing garbage collection for DEDUCED event " + updatedEvent);
		    				// if the event allows continuity, remove the old instance and insert the new one
			    			// DO THIS AS ATOMIC ACTION
			    			//EntryPoint existingEventEntry = searchEntryPoint(existingEventHandle, kSession);
			    			existingEventEntry.delete(existingEventHandle);
			    			
			    			List<FactHandle> handleList = lastValidDeducedMap.get(eventObject.getClass());
			    			handleList.remove(existingEventHandle);
		        			handleList.add(derivedEventHandle);
		    			}
		    			else {
		    				// if it is not overlapped, then it means it just has to replace the existing event in the lastValidDeducedMap
		    				List<FactHandle> handleList = lastValidDeducedMap.get(eventObject.getClass());
			    			handleList.remove(existingEventHandle);
		        			handleList.add(derivedEventHandle);
		    			}
		    		}
		    		else {
		    			// If it DOES NOT match any monitored event by content,
						// add it to the list of monitored events for this type
	    				List<FactHandle> handleList = lastValidDeducedMap.get(eventObject.getClass());
	    				handleList.add(derivedEventHandle);
		    		}
				}
			});
    	}
		
    }
	

	public void objectDeleted(ObjectDeletedEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void objectInserted(ObjectInsertedEvent insertEvent) {
		
    }


	public void objectUpdated(ObjectUpdatedEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
