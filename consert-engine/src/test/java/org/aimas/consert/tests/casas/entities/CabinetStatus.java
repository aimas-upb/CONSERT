package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.ContextEntity;

public enum CabinetStatus implements ContextEntity {
	OPEN, CLOSED;

	@Override
    public boolean isLiteral() {
	    return true;
    }

	@Override
    public Object getValue() {
	    return this;
    }
	
}
