package org.aimas.consert.engine;

import org.aimas.consert.engine.ConstraintChecker.ConstraintResult;
import org.aimas.consert.engine.ContinuityChecker.ContinuityResult;
import org.aimas.consert.engine.TrackedAssertionStore.TrackedAssertionData;
import org.aimas.consert.engine.constraint.ConstraintResolutionService;
import org.aimas.consert.engine.constraint.DefaultConstraintResolutionService;
import org.aimas.consert.engine.constraint.UniquenessConflictDecision;
import org.aimas.consert.model.constraint.IUniquenessConstraintViolation;
import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public class ConstraintResolutionHandler {
	
	private EventTracker eventTracker;
	private KieSession kSession;
	private TrackedAssertionStore trackedAssertionStore;
	
	private ConstraintResolutionService constraintResolutionService;
	
	public ConstraintResolutionHandler(EventTracker eventTracker) {
		this.eventTracker = eventTracker;
		kSession = eventTracker.getKnowledgeSession();
		trackedAssertionStore = eventTracker.getTrackedAssertionStore();
		
		constraintResolutionService = new DefaultConstraintResolutionService();
	}
	

//    private void resolveConflict(ConstraintResult constraintResult, ContextAssertion assertion,
//                                 FactHandle assertionHandle) {
//
//	    if (constraintResult.hasValueViolations()) {
//            // If we have value violations we currently ignore the extendedAssertion, deleting it
//            // Further ValueConstraintViolation implementation may attempt to alter the value to
//            // an "acceptable" one.
//            kSession.getEntryPoint(assertion.getStreamName()).delete(assertionHandle);
//        }
//        else if (constraintResult.hasUniquenessViolations()) {
//            // If the constraint check result contains a uniqueness violation, it can ONLY have
//            // one of these => run it through the resolution service and interpret the results
//            IUniquenessConstraintViolation ucv = constraintResult.getUniquenessViolation();
//            UniquenessConflictDecision decision = constraintResolutionService.resolveConflict(ucv);
//
//            if (!decision.keepNewAssertion()) {
//                // remove the extended assertion from the entry point
//                kSession.getEntryPoint(assertion.getStreamName()).delete(assertionHandle);
//            }
//            else {
//                // we keep the new, extended assertion under a rectified form
//                ContextAssertion rectifiedNewAssertion = decision.getRectifiedNewAssertion();
//
//                // delete the extended assertion
//                kSession.getEntryPoint(assertion.getStreamName()).delete(assertionHandle);
//                trackedAssertionStore.removeAssertion(assertion);
//
//                // insert the rectified extended assertion
//                FactHandle rectifiedNewAssertionHandle =
//                        kSession.getEntryPoint(assertion.getStreamName()).insert(rectifiedNewAssertion);
//
//                trackedAssertionStore.trackAssertion(rectifiedNewAssertion, rectifiedNewAssertionHandle,
//                    kSession.getEntryPoint(assertion.getStreamName()));
//
//
//                if (decision.getRectifiedExistingAssertion() != null) {
//                    // If the resolution decision involves altering the existing conflicting
//                    // assertion, then replace that as well
//                    ContextAssertion existingViolationAssertion = ucv.getExistingAssertion();
//
//                    // 1) delete the current existing violating assertion
//                    String entryPointName = existingViolationAssertion.getStreamName();
//                    FactHandle fh = kSession.getEntryPoint(entryPointName)
//                            .getFactHandle(existingViolationAssertion);
//
//                    kSession.getEntryPoint(entryPointName).delete(fh);
//
//                    // 2) insert the rectified existing violation assertion
//                    ContextAssertion rectifiedExistingAssertion = decision.getRectifiedExistingAssertion();
//                    DefaultAnnotationData ann = (DefaultAnnotationData) rectifiedExistingAssertion.getAnnotations();
//                    ann.setLastUpdated(kSession.getSessionClock().getCurrentTime());
//
//                    FactHandle rectifiedExistingFh = kSession.getEntryPoint(entryPointName)
//                            .insert(rectifiedExistingAssertion);
//
//                    // 3) if the replaced existing violation assertion was also being tracked, then
//                    // replace the entry in the trackedAssertionStore as well
//                    TrackedAssertionData existingViolationData =
//                            trackedAssertionStore.searchTrackedAssertionByContent(existingViolationAssertion);
//
//                    if (existingViolationData != null) {
//                        trackedAssertionStore.removeAssertion(existingViolationData);
//                        trackedAssertionStore.trackAssertion(rectifiedExistingAssertion,
//                            rectifiedExistingFh, kSession.getEntryPoint(entryPointName));
//                    }
//                }
//            }
//        }
//        else {
//            // TODO: It means we have one or several GENERAL constraints => we need to employ the
//            // "sequential resolution mechanism"
//        }
//    }
    
    public void resolveConflict(ConstraintResult constraintResult, ContinuityResult continuityResult) {
    	
	    if (constraintResult.hasValueViolations()) {
            // If we have value violations we currently ignore the continuityAssertion, deleting it
            // Further ValueConstraintViolation implementation may attempt to alter the value to
            // an "acceptable" one.
            kSession.getEntryPoint(continuityResult.getContinuityEventStream())
                    .delete(continuityResult.getContinuityAssertionHandle());
        }
        else if (constraintResult.hasUniquenessViolations()) {
            // If the constraint check result contains a uniqueness violation, it can ONLY have
            // one of these => run it through the resolution service and interpret the results
            IUniquenessConstraintViolation ucv = constraintResult.getUniquenessViolation();
            UniquenessConflictDecision decision = constraintResolutionService.resolveConflict(ucv);

            if (!decision.keepNewAssertion()) {
                // If we are not keeping the new assertion, we have to delete it from the entrypoint.
            	// It was inserted there such that the ConstraintCheck might be able to run.
            	
            	// Depending on whether there was a continuity result or not, the assertion we have to remove is the extended one,
            	// or the inserted one
                // remove the continuity assertion from the entry point
                kSession.getEntryPoint(continuityResult.getContinuityEventStream())
                	.delete(continuityResult.getContinuityAssertionHandle());
                
            }
            else {
                // We are keeping the new assertion - so update the trackedAssertionStore with the rectified version
                // (which may be the same as the inserted one if we are not keeping the existing one as well
                ContextAssertion rectifiedNewAssertion = decision.getRectifiedNewAssertion();
                if (rectifiedNewAssertion != null) {
                    // we keep the new, continuity assertion under a rectified form
                    
                	// step 1: delete the continuity assertion from the knowledge base
                    kSession.getEntryPoint(continuityResult.getContinuityEventStream())
                            .delete(continuityResult.getContinuityAssertionHandle());

                    // step 2: if there was a temporal extension, delete the existing assertion that is being extended
                    if (continuityResult.hasExtension()) {
                    	TrackedAssertionData existingAssertionData = continuityResult.getExistingAssertionData(kSession);
                        EntryPoint existingAssertionEntry = existingAssertionData.getExistingEventEntryPoint();
                        
                    	existingAssertionEntry.delete(continuityResult.getExistingAssertionHandle());
	                    trackedAssertionStore.removeAssertion(existingAssertionData);
                    }
                    
                    // step 3: insert the rectified extended assertion
                    FactHandle rectifiedNewAssertionHandle =
                            kSession.getEntryPoint(continuityResult.getExtendedEventStream()).insert(rectifiedNewAssertion);

                    trackedAssertionStore.trackAssertion(rectifiedNewAssertion, rectifiedNewAssertionHandle,
                            kSession.getEntryPoint(continuityResult.getExtendedEventStream()));
                }

                // Next check whether the existing assertion, with which there is a conflict, needs to be kept
                if (decision.keepExistingAssertion()) {
                    // If we keep the existing assertion too, there must be a rectification, even if it equals
                    // the current one

                    // If the resolution decision involves altering the existing conflicting
                    // assertion, then replace that as well
                    ContextAssertion existingViolationAssertion = ucv.getExistingAssertion();

                    // 1) delete the current existing violating assertion
                    String entryPointName = existingViolationAssertion.getStreamName();
                    FactHandle fh = kSession.getEntryPoint(entryPointName)
                            .getFactHandle(existingViolationAssertion);

                    kSession.getEntryPoint(entryPointName).delete(fh);

                    // 2) insert the rectified existing violation assertion
                    ContextAssertion rectifiedExistingAssertion = decision.getRectifiedExistingAssertion();
//                    DefaultAnnotationData ann = (DefaultAnnotationData) rectifiedExistingAssertion.getAnnotations();
//                    ann.setLastUpdated(getCurrentTime());

                    FactHandle rectifiedExistingFh = kSession.getEntryPoint(entryPointName)
                            .insert(rectifiedExistingAssertion);

                    // 3) if the replaced existing violation assertion was also being tracked, then
                    // replace the entry in the trackedAssertionStore as well
                    TrackedAssertionData existingViolationData =
                            trackedAssertionStore.searchTrackedAssertionByContent(existingViolationAssertion);

                    if (existingViolationData != null) {
                        trackedAssertionStore.removeAssertion(existingViolationData);
                        trackedAssertionStore.trackAssertion(rectifiedExistingAssertion,
                                rectifiedExistingFh, kSession.getEntryPoint(entryPointName));
                    }
                }
                else {
                    // If we are to delete the existing assertion - then just delete it from the entrypoint and
                    // check if it was also tracked
                    ContextAssertion existingViolationAssertion = ucv.getExistingAssertion();

                    // 1) delete the existing assertion
                    String entryPointName = existingViolationAssertion.getStreamName();
                    FactHandle fh = kSession.getEntryPoint(entryPointName)
                            .getFactHandle(existingViolationAssertion);
                    kSession.getEntryPoint(entryPointName).delete(fh);

                    // 2) if the replaced existing violation assertion was also being tracked, then
                    // replace the entry in the trackedAssertionStore as well
                    TrackedAssertionData existingViolationData =
                            trackedAssertionStore.searchTrackedAssertionByContent(existingViolationAssertion);
                    if (existingViolationData != null) {
                        trackedAssertionStore.removeAssertion(existingViolationData);
                    }
                }
            }
        }
        else {
            // TODO: It means we have one or several GENERAL constraints => we need to employ the
            // "sequential resolution mechanism"
        }
    }
	
}
