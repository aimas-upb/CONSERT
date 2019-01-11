package org.aimas.consert.engine.constraint;

import org.aimas.consert.model.content.ContextAssertion;

public interface UniquenessConflictDecision extends ConstraintResolutionDecision {

    boolean keepExistingAssertion();

    ContextAssertion getRectifiedExistingAssertion();

    ContextAssertion getRectifiedNewAssertion();

}
