package org.aimas.consert.engine;

import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.constraint.*;
import org.aimas.consert.model.content.ContextAssertion;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;

public class DefaultConstraintResolutionService implements ConstraintResolutionService {

    public static final String OVERLAP_UNIQUENESS_CONDITION = "overlap";
    public static final String IS_INCLUDED_UNIQUENESS_CONDITION = "isIncluded";
    public static final String INCLUDES_UNIQUENESS_CONDITION = "includes";

    static class DeleteNewDecision implements UniquenessConflictDecision {

        @Override
        public boolean keepExistingAssertion() {
            return true;
        }

        @Override
        public boolean keepNewAssertion() {
            return false;
        }


        @Override
        public ContextAssertion getRectifiedExistingAssertion() {
            return null;
        }


        @Override
        public ContextAssertion getRectifiedNewAssertion() {
            return null;
        }
    }


    static class DeleteExistingDecision implements UniquenessConflictDecision {

        @Override
        public boolean keepExistingAssertion() {
            return false;
        }

        @Override
        public boolean keepNewAssertion() {
            return true;
        }


        @Override
        public ContextAssertion getRectifiedExistingAssertion() {
            return null;
        }


        @Override
        public ContextAssertion getRectifiedNewAssertion() {
            return null;
        }
    }


    static class CutBothDecision implements UniquenessConflictDecision {

        private ContextAssertion existingAssertion;
        private ContextAssertion newAssertion;

        private DefaultAnnotationData existingAssertionAnn;
        private DefaultAnnotationData newAssertionAnn;

        public CutBothDecision(ContextAssertion existingAssertion, ContextAssertion newAssertion) {
            this.existingAssertion = existingAssertion;
            this.newAssertion = newAssertion;

            existingAssertionAnn = (DefaultAnnotationData) existingAssertion.getAnnotations();
            newAssertionAnn = (DefaultAnnotationData) newAssertion.getAnnotations();
        }

        @Override
        public boolean keepExistingAssertion() {
            return true;
        }

        @Override
        public boolean keepNewAssertion() {
            return true;
        }

        @Override
        public ContextAssertion getRectifiedExistingAssertion() {
            ContextAssertion assertionClone = existingAssertion.cloneContent();
            DefaultAnnotationData assertionAnnClone = existingAssertionAnn.cloneAnnotations();

            long rectifiedEndTime =
                    (existingAssertionAnn.getEndTime().getTime() + newAssertionAnn.getStartTime().getTime()) / 2;

            assertionAnnClone.setEndTime(new Date(rectifiedEndTime));
            assertionClone.setAnnotations(assertionAnnClone);

            return assertionClone;
        }

        @Override
        public ContextAssertion getRectifiedNewAssertion() {
            ContextAssertion assertionClone = newAssertion.cloneContent();
            DefaultAnnotationData assertionAnnClone = newAssertionAnn.cloneAnnotations();

            long rectifiedStartTime =
                    (existingAssertionAnn.getEndTime().getTime() + newAssertionAnn.getStartTime().getTime()) / 2;

            assertionAnnClone.setStartTime(new Date(rectifiedStartTime));
            assertionClone.setAnnotations(assertionAnnClone);

            return assertionClone;
        }
    }


    public ValueConflictDecision resolveConflict(IValueConstraintViolation violation) {

        // just ignore the violating ContextAssertion
        return new ValueConflictDecision() {
            @Override
            public ContextAssertion getReplacement() {
                return null;
            }
        };
    }


    public UniquenessConflictDecision resolveConflict(IUniquenessConstraintViolation violation) {
        ContextAssertion existingAssertion = violation.getExistingAssertion();
        ContextAssertion newAssertion = violation.getNewAssertion();

        if (violation.getConditionType().equals(IS_INCLUDED_UNIQUENESS_CONDITION)) {
            DefaultAnnotationData existingAssertionAnn = (DefaultAnnotationData)existingAssertion.getAnnotations();
            DefaultAnnotationData newAssertionAnn = (DefaultAnnotationData)newAssertion.getAnnotations();

            if (existingAssertionAnn.getEndTime().after(newAssertionAnn.getEndTime())) {
                // If the new Assertion is completely included in the previous one, judge by the length of the
                // new assertion and the amount of time by which the existing one.
                // In the default case, we just recommend deleting the included assertion altogether

                return new DeleteNewDecision();
            }
            else {
                // If both assertions have the same end time, remove the included assertion if its duration is less
                // than 3000 ms, otherwise, apply the "cut both" logic
                if (newAssertionAnn.getDuration() <= 3000)
                    return new DeleteNewDecision();
                else
                    return new CutBothDecision(existingAssertion, newAssertion);
            }
        }
        else if (violation.getConditionType().equals(INCLUDES_UNIQUENESS_CONDITION)) {
            DefaultAnnotationData existingAssertionAnn = (DefaultAnnotationData)existingAssertion.getAnnotations();
            DefaultAnnotationData newAssertionAnn = (DefaultAnnotationData)newAssertion.getAnnotations();

            if (newAssertionAnn.getEndTime().after(existingAssertionAnn.getEndTime())) {
                // If the existing Assertion is completely included in the new one, judge by the length of the
                // existing assertion.
                // In the default case, we just recommend deleting the included assertion altogether

                return new DeleteExistingDecision();
            }
            else {
                // If both assertions have the same end time, remove the included assertion if its duration is less
                // than 3000 ms, otherwise, apply the "cut both" logic
                if (existingAssertionAnn.getDuration() <= 3000)
                    return new DeleteExistingDecision();
                else
                    return new CutBothDecision(existingAssertion, newAssertion);
            }
        }
        else if (violation.getConditionType().equals(OVERLAP_UNIQUENESS_CONDITION)) {
            // For overlap, the default is the "cut both" logic
            return new CutBothDecision(existingAssertion, newAssertion);
        }

        throw new IllegalArgumentException("[DEFAULT CONSTRAINT RESOLUTION] " + violation.getConditionType()
                + "is not an allowed uniqueness condition type.");
    }

    public GeneralConflictDecision resolveConflict(IGeneralConstraintViolation violation) {
        throw new NotImplementedException();
    }
}
