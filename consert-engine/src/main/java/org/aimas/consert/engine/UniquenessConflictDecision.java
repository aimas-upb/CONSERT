package org.aimas.consert.engine;

import org.aimas.consert.model.content.ContextAssertion;

public interface UniquenessConflictDecision extends ConstraintResolutionDecision {

    boolean keepExistingAssertion();

    ContextAssertion getRectifiedExistingAssertion();

    ContextAssertion getRectifiedNewAssertion();

}
