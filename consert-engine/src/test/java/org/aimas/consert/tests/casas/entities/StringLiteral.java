package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.ContextEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StringLiteral implements ContextEntity {
	
	private String literal;
	
	public StringLiteral(String literal) {
		this.literal = literal;
	}

	@Override
    public boolean isLiteral() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
	@JsonIgnore
    public Object getValue() {
	    return literal;
    }
	
}
