package org.aimas.consert.model.constraint;

import org.aimas.consert.model.content.ContextAssertion;

public interface IUniquenessConstraintViolation extends IConstraintViolation {

    ContextAssertion getExistingAssertion();

    ContextAssertion getNewAssertion();

    String getConditionType();
}
