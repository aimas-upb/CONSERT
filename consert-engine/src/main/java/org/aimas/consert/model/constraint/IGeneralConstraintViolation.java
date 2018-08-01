package org.aimas.consert.model.constraint;

import org.aimas.consert.model.content.ContextAssertion;

public interface IGeneralConstraintViolation extends IConstraintViolation {
    ContextAssertion getExistingAssertion();

    ContextAssertion getNewAssertion();
}
