package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.content.ContextEntity;

public enum MotionStatus implements ContextEntity {
	ON, OFF;

	@Override
    public boolean isLiteral() {
	    return true;
    }

	@Override
    public Object getValue() {
	    return this;
    }

	@Override
    public String getEntityId() {
	    return name();
    }
}
