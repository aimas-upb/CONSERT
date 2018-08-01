package org.aimas.consert.engine;

import org.aimas.consert.model.content.ContextAssertion;

public interface GeneralConflictDecision {

    boolean keepExistingAssertion();

    ContextAssertion getRectifiedExistingAssertion();

    ContextAssertion getRectifiedNewAssertion();

}
