package org.aimas.consert.engine;

import org.aimas.consert.model.content.ContextAssertion;

public abstract class ValueConflictDecision implements ConstraintResolutionDecision {

    @Override
    public boolean keepNewAssertion() {
        return false;
    }

    public abstract ContextAssertion getReplacement();
}
