package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.content.ContextEntity;

public class NumericLiteral implements ContextEntity {
	
	private Double value;
	
	public NumericLiteral(Double value) {
	    this.value = value;
    }

	@Override
    public boolean isLiteral() {
	    return true;
    }

	@Override
    public Object getValue() {
	     return value;
    }

	@Override
    public String getEntityId() {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	
}
