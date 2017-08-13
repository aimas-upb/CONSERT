package org.aimas.consert.engine;

import java.util.Date;

import org.aimas.consert.engine.TrackedAssertionStore.TrackedEventData;
import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.annotations.AnnotationDataFactory;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.annotations.DefaultAnnotationDataFactory;
import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;


public class EventTracker extends BaseEventTracker {
	
	private TrackedAssertionStore trackedAssertionStore = TrackedAssertionStore.getInstance();
	
	private AnnotationDataFactory annotationFactory = new DefaultAnnotationDataFactory();
	
	public AnnotationDataFactory getAnnotationFactory() {
		return annotationFactory;
	}
	
	public void setAnnotationFactory(AnnotationDataFactory annotationFactory) {
		this.annotationFactory = annotationFactory;
	}
	
	public EventTracker(KieSession kSession) {
		super(kSession);
		kSession.setGlobal("eventTracker", this);
	}
	
	
	/**
	 * Insert an event that is not required to go through the verifications OF temporal continuity.
	 * @param event The event to be inserted.
	 * @param setTimestamp Boolean value controlling whether to set the timestamp of the event based on the kSession clock.
	 */
	public void insertSimpleEvent(ContextAssertion event, boolean setTimestamp) {
		String eventStream = event.getStreamName();
		
		if (event.getAnnotations().allowsAnnotationInsertion()) {
			if (setTimestamp) {
				DefaultAnnotationData ann = (DefaultAnnotationData)event.getAnnotations();
				ann.setTimestamp(kSession.getSessionClock().getCurrentTime());
				ann.setStartTime(new Date(kSession.getSessionClock().getCurrentTime()));
				ann.setEndTime(new Date(kSession.getSessionClock().getCurrentTime()));
			}
			
			kSession.getEntryPoint(eventStream).insert(event);
		}
	}
	

	/**
	 * Insert an atomic event. The event will go through the verifications of temporal continuity.
	 * @param event The atomic event to insert
	 */
    public void insertAtomicEvent(final ContextAssertion event) {
    	String eventStream = event.getStreamName();
    	
    	// if this is the first event of its type
    	if (!trackedAssertionStore.tracksSensed(event.getClass())) {
    		if (event.getAnnotations().allowsAnnotationInsertion()) {
    			// go through with insertion in the map and the KieBase
    			FactHandle handle = kSession.getEntryPoint(eventStream).insert(event);
    			trackedAssertionStore.trackSensed(event, handle, kSession.getEntryPoint(eventStream));
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
					TrackedEventData existingEventData = trackedAssertionStore.searchSensedAssertionByContent(event); 
		    		if (existingEventData != null) {
		    			// if it DOES match any monitored event by content 
		    			FactHandle existingEventHandle = existingEventData.getExistingHandle();
		    			ContextAssertion updatedEvent = existingEventData.getExistingEvent();
		    			EntryPoint existingEventEntry = existingEventData.getExistingEventEntryPoint();
		    			
		    			//System.out.println("TRACKED DATA exists: " + updatedEvent);
		    			
		    			// if it allows continuity by annotation
		    			if (updatedEvent.getAnnotations().allowsAnnotationContinuity(event.getAnnotations())) { // time s
			    			// create event clone
							//System.out.println( updatedEvent.getProcessingTimeStamp());
		    				//System.out.println("FOUND EXTENSION for existing event: " + updatedEvent + ", new event: " + event);
			    			AnnotationData updatedAnnotations = updatedEvent.getAnnotations()
			    					.applyExtensionOperator(event.getAnnotations());
			    			
			    			updatedEvent.setAnnotations(updatedAnnotations);
			    			
			    			// if the event allows continuity, remove the old instance and insert the new one
			    			if (existingEventHandle != null)
			    				existingEventEntry.delete(existingEventHandle);
			    			
			    			trackedAssertionStore.removeSensed(existingEventData);
			    			
			    			String extendedEventStream = updatedEvent.getExtendedStreamName();
			    			FactHandle handle = kSession.getEntryPoint(extendedEventStream).insert(updatedEvent);
			    			trackedAssertionStore.trackSensed(updatedEvent, handle, kSession.getEntryPoint(extendedEventStream));
			    			
			    			// TEST SIZE OF KnowledgeBase
			    			//System.out.println("COUNT OF EVENTS FOR " + existingEventEntry.getEntryPointId() + " IS: " + existingEventEntry.getObjects().size());
		    			}
		    			// if NO annotation continuity (either because of timestamp or confidence)
		    			else {
		    				if (event.getAnnotations().allowsAnnotationInsertion()) {
		    					// if allowed by confidence allowed, set the new event as the most recently valid one
		    					trackedAssertionStore.removeSensed(existingEventData);
		    					trackedAssertionStore.trackSensed(event, newEventHandle, kSession.getEntryPoint(eventStream));
		    				}
			    		}
		    		}
		    		else {
		    			// if it DOES NOT match any monitored event by content
		    			if (event.getAnnotations().allowsAnnotationInsertion()) {
		    				// if the confidence value check allows it to be inserted,
							// add it to the list of monitored events for this type
		    				trackedAssertionStore.trackSensed(event, newEventHandle, kSession.getEntryPoint(eventStream));
		    			}
		    		}
					
				}
			});
    		
    		
    	}
    }
    
    
//	/**
//	 * Insert a derived event. This method will usually be called in the right-hand-side of a 
//     * ContextDerivationRule. The method will check for duplicates of events that have already been
//     * derived, but which have still not been garbage collected.
//     * However, no continuity check will be performed
//	 * @param derivedEvent The derived event to be inserted.
//	 */
//	public void insertSimpleDerivedEvent(ContextAssertion derivedEvent) {
//		if (!checkPreviouslyDerived(derivedEvent)) {
//			String derivedEventStream = derivedEvent.getExtendedStreamName();
//			kSession.getEntryPoint(derivedEventStream).insert(derivedEvent);
//		}
//	}
	
    
    /**
     * Insert a derived event. This method will usually be called in the right-hand-side of a 
     * ContextDerivationRule. The method will check for duplicates of events that have already been
     * derived, but which have still not been garbage collected.
     * @param event The event to be inserted
     */
    public void insertDerivedEvent(ContextAssertion event) {
    	//kSession.getQueryResults(query, arguments);
    	
    	// Check if event to be inserted exists already
    	if (!checkPreviouslyDerived(event)) {
    		doDerivedInsertion(event);
    	}
    	
    }
    
    
	private boolean checkPreviouslyDerived(ContextAssertion derivedEvent) {
		String derivedStreamName = derivedEvent.getStreamName();
		EntryPoint derivedEventStream = kSession.getEntryPoint(derivedStreamName);
		
		//System.out.println("Derived Event Stream name: " + derivedStreamName);
		
		
		for (Object eventObj : derivedEventStream.getObjects()) {
			ContextAssertion event = (ContextAssertion) eventObj;
			
			// if the objects have the same content
			if (event.allowsContentContinuity(derivedEvent)) {
				if (event.getAnnotations() != null) {
					// and they have the same validity interval
					if (event.getAnnotations().hasSameValidity(derivedEvent.getAnnotations())) {
						System.out.println("[INFO] ::::::::::::::::::::: We have an insertion of an already existing event: " + derivedEvent);
						return true;
					}
				}
				else {
					return true;
				}
			}
		}
	    
		return false;
    }


    
    private void doDerivedInsertion(final ContextAssertion eventObject) {
		//BaseEvent insertedEventObject = (BaseEvent)insertEvent.getObject();
		
    	final String derivedEventStream = eventObject.getStreamName();
    	
		// perform same type of checks as in the case of temporal validity extension
		// if this is the first event of its type
    	if (!trackedAssertionStore.tracksDerived(eventObject.getClass())) {
    		// go through with insertion in the map
    		FactHandle handle = kSession.getEntryPoint(derivedEventStream).insert(eventObject);
    		trackedAssertionStore.trackDerived(eventObject, handle, kSession.getEntryPoint(derivedEventStream));
    	}
    	else {
    		// do the overlap verification steps as an atomic action
    		kSession.submit(new KieSession.AtomicAction() {
				@Override
				public void execute(KieSession kSession) {
					// The newly derived event has to make it to the KnowledgeBase in any case, so we perform the insert here
		    		FactHandle derivedEventHandle = kSession.getEntryPoint(derivedEventStream).insert(eventObject);
					
					// check to see if it matches one of the previous stored events by content
		    		TrackedEventData existingEventData = trackedAssertionStore.searchDerivedAssertionByContent(eventObject);
		    		if (existingEventData != null) {
		    			// if it DOES match any monitored event by content 
		    			FactHandle existingEventHandle = existingEventData.getExistingHandle();
		    			ContextAssertion updatedEvent = existingEventData.getExistingEvent();
		    			EntryPoint existingEventEntry = existingEventData.getExistingEventEntryPoint();
		    			
		    			// if the validity interval of the previous deduced event is included in the interval of the extended one 
//		    			System.out.println("0000000000000000000000000");
//		    			System.out.println("Updated Event: " + updatedEvent);
//		    			System.out.println("New Event (that extends): " + eventObject);
//		    			System.out.println("0000000000000000000000000");
		    			
		    			
		    			if (updatedEvent.isOverlappedBy(eventObject)) {
			    			System.out.println("!!!!!!!!!![EventTracker] Analyzing garbage collection for DEDUCED event " + updatedEvent);
		    				
			    			if (existingEventHandle != null) 
			    				existingEventEntry.delete(existingEventHandle);
			    			
			    			trackedAssertionStore.removeDerived(existingEventData);
		        			trackedAssertionStore.trackDerived(eventObject, derivedEventHandle, kSession.getEntryPoint(derivedEventStream));
		    			}
		    			else {
		    				// if it is not overlapped, then it means it just has to replace the existing event in the lastValidDeducedMap
		    				trackedAssertionStore.removeDerived(existingEventData);
		        			trackedAssertionStore.trackDerived(eventObject, derivedEventHandle, kSession.getEntryPoint(derivedEventStream));
		    			}
		    		}
		    		else {
		    			// If it DOES NOT match any monitored event by content,
						// add it to the list of monitored events for this type
		    			trackedAssertionStore.trackDerived(eventObject, derivedEventHandle, kSession.getEntryPoint(derivedEventStream));
		    		}
				}
			});
    	}
		
    }
	

	public void objectDeleted(ObjectDeletedEvent event) {
		System.out.println("DELETED EVENT object: " + event.getOldObject());
		System.out.println("	HANDLE: " + event.getFactHandle());
		
		if (event.getRule() != null) {
			System.out.println("	RULE: " + event.getRule().getName());
		}
		System.out.println();
		
		FactHandle deletedHandle = event.getFactHandle();
	    ContextAssertion deletedAssertion = (ContextAssertion)event.getOldObject();
	    
	    if (trackedAssertionStore.wasUntracked(deletedHandle, deletedAssertion)) {
	    	trackedAssertionStore.markExpired(deletedHandle, deletedAssertion);
	    }
    }
	
	
	public void objectInserted(ObjectInsertedEvent insertEvent) {
		System.out.println("INSERTED EVENT object: " + insertEvent.getObject());
		System.out.println("	HANDLE: " + insertEvent.getFactHandle());
		if (insertEvent.getRule() != null) {
			System.out.println("	RULE: " + insertEvent.getRule().getName());
		}
		System.out.println();
    }


	public void objectUpdated(ObjectUpdatedEvent event) {
//		System.out.println("UPDATED EVENT with handle: " + event.getFactHandle());
//		System.out.println("UPDATED EVENT object: " + event.getObject());
//		System.out.println();
    }

}
