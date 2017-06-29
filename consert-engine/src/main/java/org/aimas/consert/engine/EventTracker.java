package org.aimas.consert.engine;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.AnnotationDataFactory;
import org.aimas.consert.model.ContextAssertion;
import org.aimas.consert.model.DefaultAnnotationDataFactory;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;


public class EventTracker extends BaseEventTracker {
	static long ID = 0;
	ArrayList<Long> LLADelays;
	ArrayList<Long> HLADelays;

	public static class TrackedEventData {
		private FactHandle existingHandle;
		private EntryPoint existingEventEntryPoint;
		private ContextAssertion existingEvent;
		
		public TrackedEventData(FactHandle existingHandle, EntryPoint existingEventEntryPoint, ContextAssertion existingEvent) {
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

		public ContextAssertion getEventObject() {
			return existingEvent;
		}
	}
	
	
	Map<Class<? extends ContextAssertion>, List<FactHandle>> lastValidEventMap = new HashMap<Class<? extends ContextAssertion>, List<FactHandle>>();
	Map<Class<? extends ContextAssertion>, List<FactHandle>> lastValidDeducedMap = new HashMap<Class<? extends ContextAssertion>, List<FactHandle>>();
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

	@Override
	public void onStart() {
		LLADelays = new ArrayList<Long>();
		HLADelays = new ArrayList<Long>();
	}

	private void printToFile (String fileName, ArrayList<Long> arr)
	{
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i =0; i< arr.size(); i++)
			writer.println(arr.get(i));
		writer.close();
	}

	@Override
	public void onStop() {
		printToFile("lla_delays.txt",LLADelays);
		printToFile("hla_delays.txt",HLADelays);
	}

	private TrackedEventData searchHandleByContent(Map<Class<? extends ContextAssertion>, List<FactHandle>> recencyMap, ContextAssertion event, KieSession kSession) {
		List<FactHandle> handleList = recencyMap.get(event.getClass());
		for (FactHandle existingHandle : handleList) {
			EntryPoint existingEventEntry = searchEntryPoint(existingHandle, kSession);
			ContextAssertion existingEvent = (ContextAssertion)existingEventEntry.getObject(existingHandle);
			
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
    public void insertAtomicEvent(final ContextAssertion event) {
    	//System.out.println(event.getClass());
		event.setProcessingTimeStamp(kSession.getSessionClock().getCurrentTime());
		event.setID(ID);
		ID++;
    	String eventStream = event.getStreamName();
    	
    	// if this is the first event of its type
    	if (!lastValidEventMap.containsKey(event.getClass())) {
    		
    		if (event.getAnnotations().allowsAnnotationInsertion()) {
    			// go through with insertion in the map and the KieBase
	    		List<FactHandle> handleList = new LinkedList<FactHandle>();
	    		lastValidEventMap.put(event.getClass(), handleList);
    			
	    		FactHandle handle = kSession.getEntryPoint(eventStream).insert(event);
	    		handleList.add(handle);
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
		    			ContextAssertion updatedEvent = existingEventData.getEventObject();
		    			EntryPoint existingEventEntry = existingEventData.getEntryPoint();
		    			
		    			// if it allows continuity by annotation
		    			if (updatedEvent.getAnnotations().allowsAnnotationContinuity(event.getAnnotations())) { // time s
			    			// create event clone
							long delay = (kSession.getSessionClock().getCurrentTime() - event.getProcessingTimeStamp());
							System.out.println("delay for " + event.toString() + "is: "  + delay);
							LLADelays.add(delay);
			    			AnnotationData updatedAnnotations = updatedEvent.getAnnotations().applyExtensionOperator(event.getAnnotations());
			    			updatedEvent.setAnnotations(updatedAnnotations);
							updatedEvent.setID(event.getID());
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
		    				if (event.getAnnotations().allowsAnnotationInsertion()) {
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
		    			if (event.getAnnotations().allowsAnnotationInsertion()) {
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
    public void insertDerivedEvent(ContextAssertion event) {
    	//kSession.getQueryResults(query, arguments);
    	
    	// Check if event to be inserted exists already
    	if (!checkPreviouslyDerived(event)) {
    		doDerivedInsertion(event);
    	}
    	
    }
    
	public void insertDerivedEvent(ContextAssertion event, ArrayList<ContextAssertion> List) {
		//kSession.getQueryResults(query, arguments);

		// Check if event to be inserted exists already
		if (!checkPreviouslyDerived(event)) {
			doDerivedInsertion(event, List);
		}

	}
    
	private boolean checkPreviouslyDerived(ContextAssertion derivedEvent) {
		String derivedStreamName = derivedEvent.getExtendedStreamName();
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
		
    	final String derivedEventStream = eventObject.getExtendedStreamName();
    	
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
    		// do the overlap verification steps as an atomic action
    		kSession.submit(new KieSession.AtomicAction() {
				@Override
				public void execute(KieSession kSession) {
					// The newly derived event has to make it to the KnowledgeBase in any case, so we perform the insert here
		    		FactHandle derivedEventHandle = kSession.getEntryPoint(derivedEventStream).insert(eventObject);
					
					// check to see if it matches one of the previous stored events by content
		    		TrackedEventData existingEventData = searchHandleByContent(lastValidDeducedMap, eventObject, kSession);
		    		if (existingEventData != null) {
		    			// if it DOES match any monitored event by content 
		    			FactHandle existingEventHandle = existingEventData.getHandle();
		    			ContextAssertion updatedEvent = existingEventData.getEventObject();
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
    
    
	private void doDerivedInsertion(final ContextAssertion eventObject,final ArrayList<ContextAssertion> List) {
		//BaseEvent insertedEventObject = (BaseEvent)insertEvent.getObject();
		eventObject.setProcessingTimeStamp(kSession.getSessionClock().getCurrentTime());
		final String derivedEventStream = eventObject.getExtendedStreamName();

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
			// do the overlap verification steps as an atomic action
			kSession.submit(new KieSession.AtomicAction() {
				@Override
				public void execute(KieSession kSession) {
					// The newly derived event has to make it to the KnowledgeBase in any case, so we perform the insert here
					FactHandle derivedEventHandle = kSession.getEntryPoint(derivedEventStream).insert(eventObject);
					int max_id = -1 ;
					for (int i =0; i<List.size(); i++)
					{
						if (List.get(i).getID()>max_id)
							max_id = i;
					}
					String extendedEventStream = List.get(max_id).getStreamName();
					Collection<FactHandle> handles = kSession.getEntryPoint(extendedEventStream).getFactHandles();
					
					for (Object event : kSession.getEntryPoint(extendedEventStream).getObjects())
					{
						if(((ContextAssertion)event).getID()==List.get(max_id).getID())
						{
							long delay = (kSession.getSessionClock().getCurrentTime()- ((ContextAssertion)event).getProcessingTimeStamp());
							System.out.println("delay for " + eventObject + "is :" + delay);
							HLADelays.add(delay);
							
							break;
						}
					}
					
					// check to see if it matches one of the previous stored events by content
					TrackedEventData existingEventData = searchHandleByContent(lastValidDeducedMap, eventObject, kSession);
					if (existingEventData != null) {
						// if it DOES match any monitored event by content
						FactHandle existingEventHandle = existingEventData.getHandle();
						ContextAssertion updatedEvent = existingEventData.getEventObject();
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
