package org.aimas.consert.engine.constraint;

import org.aimas.consert.model.content.ContextAssertion;

public interface GeneralConflictDecision {

    boolean keepExistingAssertion();

    ContextAssertion getRectifiedExistingAssertion();

    ContextAssertion getRectifiedNewAssertion();

}
