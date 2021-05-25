package org.aimas.consert.engine.core;

import java.util.Date;

import org.aimas.consert.engine.core.ConstraintChecker.ConstraintResult;
import org.aimas.consert.engine.core.ContinuityChecker.ContinuityResult;
import org.aimas.consert.engine.core.TrackedAssertionStore.TrackedAssertionData;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.EntityDescription;
import org.drools.core.common.EventFactHandle;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;


public class EventTracker extends BaseEventTracker {

    public static final String CONSTRAINT_STORE = "ConstraintStore";

	private ContinuityChecker continuityChecker;
    private ConstraintChecker constraintChecker;
    
    private ConstraintResolutionHandler constraintResolutionHandler;
    
    private EventWindowManager eventWindowManager;
    
    private ChangePointManager changePointManager;
    
    
	public EventTracker(KieSession kSession, int changePointQueueSize) {
		super(kSession);
		
		// initialize EventWindowManager, continuity and constraint checkers, as well as constraint resolution handlers
		eventWindowManager = new EventWindowManager(this, kSession);
		
		// initialize the changePointManager
		changePointManager = new ChangePointManager(this, changePointQueueSize);
		
		continuityChecker = new ContinuityChecker(this);
		constraintChecker = new ConstraintChecker(this);
		constraintResolutionHandler = new ConstraintResolutionHandler(this);
		
		// set global access for the Event Tracker, the Event Window Manager and the ChangePoint Managerqq
		kSession.setGlobal("eventTracker", this);
		kSession.setGlobal("eventWindowManager", eventWindowManager);
		kSession.setGlobal("changePointManager", changePointManager);
	}
	
	public EventTracker(KieSession kSession) {
		this(kSession, ChangePointManager.DEFAULT_MAX_CP);
	}
	

	/**
	 * Insert an EntityDescription (a fact)
	 * @param fact EntityDescription to be inserted.
	 */
	@Override
	public void insertStaticEvent(EntityDescription fact) {
		kSession.insert(fact);
	}
	
	/**
	 * Remove an EntityDescription. The implementation makes use of the 
	 * <code>hashCode</code> and <code>equals</code> methods of an EntityDescription to identify the 
	 * fact to be deleted in the Working Memory.
	 * @param fact The EntityDescription to be removed.
	 */
	@Override
	public void deleteStaticEvent(EntityDescription fact) {
		FactHandle fh = kSession.getFactHandle(fact);
		
		if (fh != null) {
			kSession.delete(fh);
		}
	}
	
	/**
	 * Insert an event that is NOT required to go through the verifications of TEMPORAL CONTINUITY or CONSTRAINT CHECKING.
	 * @param event The event to be inserted.
	 * @param setTimestamp Boolean value controlling whether to set the timestamp of the event based on the kSession clock.
	 */
	@Override
	public void insertSimpleEvent(ContextAssertion event, boolean setTimestamp) {
		String eventStream = event.getStreamName();
		
		if (event.getAnnotations().allowsAnnotationInsertion()) {
			if (setTimestamp) {
				DefaultAnnotationData ann = (DefaultAnnotationData)event.getAnnotations();
				ann.setTimestamp(getCurrentTime());
				ann.setStartTime(new Date(getCurrentTime()));
				ann.setEndTime(new Date(getCurrentTime()));
				event.setAnnotations(ann);
			}
			
			/* shouldn't be null but it is*/
//			if (kSession.getEntryPoint(eventStream)!=null)
			kSession.getEntryPoint(eventStream).insert(event);
		}
	}
	
	/**
	 * Remove a ContextAssertion. The implementation makes use of the 
	 * <code>hashCode</code> and <code>equals</code> methods of a ContextAssertion to identify the 
	 * event to be deleted in the Working Memory.
	 * @param event The ContextAssertion to be removed.
	 */
	@Override
	public void deleteEvent(ContextAssertion event) {
		String eventStream = event.getStreamName();
		FactHandle handle = kSession.getEntryPoint(eventStream).getFactHandle(event);
		
		kSession.getEntryPoint(eventStream).delete(handle);
	}

	/**
	 * Insert an event. The event will go through the verifications of TEMPORAL CONTINUITY and possibly ensuing CONSTRAINT CHECK.
	 * @param newAssertion The event to insert
	 */
	@Override
    public void insertEvent(final ContextAssertion newAssertion) {
    	String eventStream = newAssertion.getStreamName();
    	
    	// if this is the first event of its type
    	if (!trackedAssertionStore.tracksAssertion(newAssertion.getClass())) {
    		if (newAssertion.getAnnotations().allowsAnnotationInsertion()) {
    			// Go through with insertion in the tracking map and the KieBase
    			FactHandle handle = kSession.getEntryPoint(eventStream).insert(newAssertion);
                
                if (!newAssertion.isAtomic()) {
                	// Go through with constraint check 
                	ContinuityResult continuityResult = new ContinuityResult(false, null, null, newAssertion, handle, null, null);
                	ConstraintResult constraintResult =
							constraintChecker.check(newAssertion);
                	
                	if (!constraintResult.isClear()) {
                        //generalRuleLogger.debug("[CONSTRAINT CHECKER] DETECTED CONSTRAINT VIOLATIONS FOR: "
                        //        + continuityResult.getExtendedAssertion() + ". Violations:\n" + constraintResult);

                        constraintResolutionHandler.resolveConflict(constraintResult, continuityResult);
                    }
                    else {
                        // No constraints found, so start tracking the newly inserted assertion type 
                    	trackedAssertionStore.trackAssertion(newAssertion, handle, kSession.getEntryPoint(eventStream));
                    }
                }
                else {
                	// No constraints are applied for atomic event, so start tracking the newly inserted assertion type 
                	trackedAssertionStore.trackAssertion(newAssertion, handle, kSession.getEntryPoint(eventStream));
                }
    		}
    	}
    	else {
    		// afterwards, do the all continuity verification steps as an atomic action
    		kSession.submit(new KieSession.AtomicAction() {
				@Override
				public void execute(KieSession kSession) {
					// check to see if it matches one of the previous stored events by content
                    ContinuityResult continuityResult = continuityChecker.check(newAssertion);

                    if (continuityResult.hasExtension()) {
                        TrackedAssertionData existingAssertionData = continuityResult.getExistingAssertionData(kSession);
                        EntryPoint existingAssertionEntry = existingAssertionData.getExistingEventEntryPoint();

                        // insert the extended one
                        FactHandle extendedAssertionHandle = kSession.getEntryPoint(continuityResult.getExtendedEventStream())
                                .insert(continuityResult.getExtendedAssertion());
                        continuityResult.setExtendedAssertionHandle(extendedAssertionHandle);

                        ConstraintResult constraintResult =
								constraintChecker.check(continuityResult.getExtendedAssertion());

                        if (!constraintResult.isClear()) {
                            //generalRuleLogger.debug("[CONSTRAINT CHECKER] DETECTED CONSTRAINT VIOLATIONS FOR: "
                            //        + continuityResult.getExtendedAssertion() + ". Violations:\n" + constraintResult);

                            constraintResolutionHandler.resolveConflict(constraintResult, continuityResult);
                        }
                        else {
                            // No constraints found, so update update kSession and trackedAssertionStore,
                            // remove existing ContextAssertion
                            existingAssertionEntry.delete(continuityResult.getExistingAssertionHandle());
                            trackedAssertionStore.updateTrackedAssertion(existingAssertionData,
                                    continuityResult.getExtendedAssertion(), extendedAssertionHandle);
                        }
                    }
                    else {
                        if (newAssertion.getAnnotations().allowsAnnotationInsertion()) {
                            if (continuityResult.getExistingAssertion() != null) {
                                // If NO annotation continuity, but a previously tracked assertion of the
                                // same content exists, then the newly derived event is allowed for insertion
                                // (from an annotation perspective).
                                // Therefore, it has to make it to the KnowledgeBase => we perform the insert here
                                FactHandle newAssertionHandle = kSession.getEntryPoint(eventStream).insert(newAssertion);
                                continuityResult.setInsertedAssertionHandle(newAssertionHandle);
                                
                                // next, if the assertion is not an atomic event, run it through the constraint check
                                if (!newAssertion.isAtomic()) {
	                                ConstraintResult constraintResult =
	                                        constraintChecker.check(newAssertion);
	
	                                if (!constraintResult.isClear()) {
	                                    //generalRuleLogger.debug("[CONSTRAINT CHECKER] DETECTED CONSTRAINT VIOLATIONS FOR: "
	                                    //        + newAssertion + ". Violations:\n" + constraintResult);
	
	                                    constraintResolutionHandler.resolveConflict(constraintResult, continuityResult);
	                                } 
	                                else {
	                                    // if there are no violated constraints then it means it
	                                    // can also replace the existing event in the lastValidMap
	                                    trackedAssertionStore.updateTrackedAssertion(continuityResult.getExistingAssertionData(kSession),
	                                            newAssertion, newAssertionHandle);
	                                }
                                }
                                else {
                                	// if it is atomic it means we can also replace the existing event in the lastValidMap
                                    trackedAssertionStore.updateTrackedAssertion(continuityResult.getExistingAssertionData(kSession),
                                            newAssertion, newAssertionHandle);
                                }
                            }
                            else {
                                //generalRuleLogger.debug("CONTENT MISMATCH - NO TRACKED DATA FOR non-extended new Assertion: " + newAssertion);
                                // If allowed for insertion from an annotation perspective, but it DOES NOT match any
                                // monitored event by content, add it to the list of monitored events for this type

                                FactHandle newEventHandle = kSession.getEntryPoint(eventStream).insert(newAssertion);
                                continuityResult.setInsertedAssertionHandle(newEventHandle);
                                
                                if (!newAssertion.isAtomic()) {
                                	ConstraintResult constraintResult = constraintChecker.check(newAssertion);
                                	if (!constraintResult.isClear()) {
	                                    //generalRuleLogger.debug("[CONSTRAINT CHECKER] DETECTED CONSTRAINT VIOLATIONS FOR: "
	                                    //        + newAssertion + ". Violations:\n" + constraintResult);
	
	                                    constraintResolutionHandler.resolveConflict(constraintResult, continuityResult);
	                                } 
	                                else {
	                                    // if there are no violated constraints then it means it
	                                    // the new assertion content can also be tracked
	                                	trackedAssertionStore.trackAssertion(newAssertion, newEventHandle, kSession.getEntryPoint(eventStream));
	                                }
                                }
                                else {
                                	// No constraint checking is performed for atomic events, so the new assertion content is tracked
                                	trackedAssertionStore.trackAssertion(newAssertion, newEventHandle, kSession.getEntryPoint(eventStream));
                                }
                            }
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
	@Override
    public void insertDerivedEvent(ContextAssertion event) {
    	// Check if event to be inserted exists already
    	if (!checkPreviouslyDerived(event)) {
    		//doDerivedInsertion(event);
            insertEvent(event);
    	}
    	
    }
    
    
	private boolean checkPreviouslyDerived(ContextAssertion derivedEvent) {
		String derivedStreamName = derivedEvent.getStreamName();
		EntryPoint derivedEventStream = kSession.getEntryPoint(derivedStreamName);

		for (Object eventObj : derivedEventStream.getObjects()) {
			ContextAssertion event = (ContextAssertion) eventObj;
			
			// if the objects have the same content
			if (event.allowsContentContinuity(derivedEvent)) {
				if (event.getAnnotations() != null) {
					// and they it includes the validity interval of the derived object
					if (event.getAnnotations().hasIncludedValidity(derivedEvent.getAnnotations())) {
						//generalRuleLogger.debug("[INFO] ::::::::::::::::::::: We have an insertion of an already existing event: " + derivedEvent);
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// CONFLICT MANAGEMENT //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    private void applyUniquenessConflictDecision(UniquenessConflictDecision decision, boolean existingAssertion) {
//
//    }

//    @Override
//    public void conflictDetected(ValueConstraintViolation vcv) {
//
//    }
//
//    @Override
//    public void conflictDetected(UniquenessConstraintViolation ucv) {
//    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////// API MANAGEMENT ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
	public void objectDeleted(ObjectDeletedEvent event) {
		//generalRuleLogger.debug("TRACKER DELETED EVENT object: " + event.getOldObject());
		//generalRuleLogger.debug("	HANDLE: " + event.getFactHandle());

        if (event.getOldObject() instanceof ContextAssertion) {
            ContextAssertion assertion = (ContextAssertion)event.getOldObject();
            
            //if (!assertion.isAtomic())
            //    generalRuleLogger.debug("[CALLBACK DELETE] ************ DELETED assertion: " + assertion + "; FACT HANDLE: " + event.getFactHandle());
        }

		FactHandle deletedHandle = event.getFactHandle();
		if (deletedHandle instanceof EventFactHandle) {
		    // if we are deleting an event - ContextAssertion
            EventFactHandle deletedEventHandle = (EventFactHandle)deletedHandle;

            // only call the eventNotifier if we are deleting from
            // the default EntryPoint or a ContextAssertion specific one
            if (event.getOldObject() instanceof ContextAssertion
                    && !deletedEventHandle.getEntryPoint().getEntryPointId().equals(CONSTRAINT_STORE)) {

                ContextAssertion deletedAssertion = (ContextAssertion)event.getOldObject();

                if (trackedAssertionStore.untrack(deletedHandle, deletedAssertion)) {
                    trackedAssertionStore.markExpired(deletedHandle, deletedAssertion);
                }

                eventNotifier.notifyEventDeleted(deletedAssertion);
            }
		}
		else {
		    // if we are deleting a fact - EntityDescription
		    if (event.getOldObject() instanceof EntityDescription) {
                factNotifier.notifyFactDeleted((EntityDescription)event.getOldObject());
            }
        }
    }
	
	
    @Override
	public void objectInserted(ObjectInsertedEvent insertEvent) {
//		if (insertEvent.getObject() instanceof ContextAssertion) {
//
//		    ContextAssertion assertion = (ContextAssertion)insertEvent.getObject();
//		    if (!assertion.isAtomic())
//                generalRuleLogger.debug("[CALLBACK INSERTION] Inserted assertion: " + assertion + "; FACT HANDLE: " + insertEvent.getFactHandle());
//		}

        FactHandle insertHandle = insertEvent.getFactHandle();
        if (insertHandle instanceof EventFactHandle) {
            // if we are inserting an event - ContextAssertion
            EventFactHandle insertEventHandle = (EventFactHandle)insertHandle;

            if (insertEvent.getObject() instanceof ContextAssertion
                    && !insertEventHandle.getEntryPoint().getEntryPointId().equals(CONSTRAINT_STORE)) {
                generalRuleLogger.debug("TRACKER INSERTED EVENT object: " + insertEvent.getObject());
                eventNotifier.notifyEventInserted((ContextAssertion)insertEvent.getObject());
            }
        }
    	else {
            // if we are inserting a fact - EntityDescription
            if (insertEvent.getObject() instanceof EntityDescription)
                factNotifier.notifyFactInserted((EntityDescription) insertEvent.getObject());
        }
    }

    
    @Override
	public void objectUpdated(ObjectUpdatedEvent event) {
    	
    }

}
