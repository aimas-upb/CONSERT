package org.aimas.consert.model.constraint;

import org.aimas.consert.model.content.ContextAssertion;
import org.drools.core.common.EventFactHandle;

public class ValueConstraintViolation implements IValueConstraintViolation {

    private String constraintName;

    //private EventFactHandle eventHandle;
    private ContextAssertion violatingAssertion;

    public ValueConstraintViolation() { }

    public ValueConstraintViolation(String constraintName, ContextAssertion violatingAssertion) {
        this.constraintName = constraintName;
        this.violatingAssertion = violatingAssertion;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    @Override
    public ContextAssertion getViolatingAssertion() {
        return violatingAssertion;
    }

    public void setViolatingAssertion(ContextAssertion violatingAssertion) {
        this.violatingAssertion = violatingAssertion;
    }

//    public EventFactHandle getEventHandle() {
//        return eventHandle;
//    }
//
//    public void setEventHandle(EventFactHandle eventHandle) {
//        this.eventHandle = eventHandle;
//    }

    @Override
    public String toString() {
        String res = "";
        res += "[VALUE CONSTRAINT] ------------ name: " + constraintName;

        return res;
    }
}
