package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.ContextEntity;

public enum ItemStatus implements ContextEntity {
	PRESENT, ABSENT;

	@Override
    public boolean isLiteral() {
	    return true;
    }

	@Override
    public Object getValue() {
	    return this;
    }
}
