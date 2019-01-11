package org.aimas.consert.model.constraint;

import org.aimas.consert.model.content.ContextAssertion;

public interface IValueConstraintViolation extends IConstraintViolation {
    ContextAssertion getViolatingAssertion();
}
