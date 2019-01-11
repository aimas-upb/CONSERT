package org.aimas.consert.model.constraint;

import org.aimas.consert.model.content.ContextAssertion;

public class UniquenessConstraintViolation implements IUniquenessConstraintViolation {

    private String constraintName;
    private String conditionType;

    private ContextAssertion existingAssertion;
    private ContextAssertion newAssertion;

    public UniquenessConstraintViolation() { }

    public UniquenessConstraintViolation(String constraintName, String conditionType,
                                         ContextAssertion existingAssertion,
                                         ContextAssertion newAssertion) {
        this.constraintName = constraintName;
        this.conditionType = conditionType;

        this.existingAssertion = existingAssertion;
        this.newAssertion = newAssertion;
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
    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    @Override
    public String toString() {
        String res = "";

        res += "[UNIQUENESS CONSTRAINT] ------------ name: " + constraintName;
        res += " " + "conditionType: " + conditionType;
        res += " " + "existingAssertion: " + existingAssertion;
        res += " " + "newAssertion: " + newAssertion;

        return res;
    }
}
