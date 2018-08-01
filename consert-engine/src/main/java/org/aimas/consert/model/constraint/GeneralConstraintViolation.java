package org.aimas.consert.model.constraint;

import org.aimas.consert.model.content.ContextAssertion;

public class GeneralConstraintViolation implements IGeneralConstraintViolation {

    private String constraintName;

    private ContextAssertion existingAssertion;
    private ContextAssertion newAssertion;

    public GeneralConstraintViolation() { }

    public GeneralConstraintViolation(String constraintName, ContextAssertion firstAssertion,
                                      ContextAssertion secondAssertion) {
        this.constraintName = constraintName;
        this.existingAssertion = firstAssertion;
        this.newAssertion = secondAssertion;
    }

    @Override
    public String getConstraintName() {
        return null;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    @Override
    public ContextAssertion getExistingAssertion() {
        return existingAssertion;
    }

    public void setExistingAssertion(ContextAssertion existingAssertion) {
        this.existingAssertion = existingAssertion;
    }

    @Override
    public ContextAssertion getNewAssertion() {
        return newAssertion;
    }

    public void setNewAssertion(ContextAssertion newAssertion) {
        this.newAssertion = newAssertion;
    }

    @Override
    public String toString() {
        String res = "";

        res += "[GENERAL CONSTRAINT] ------------ constraintName: " + constraintName;
        res += " " + "existingAssertion: " + existingAssertion;
        res += " " + "newAssertion: " + newAssertion;

        return res;
    }
}
