package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

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

	@Override
    public String getEntityId() {
	    return SimpleValueFactory.getInstance().createLiteral(literal).getLabel();
    }
	
	@Override
	public String toString() {
		return literal;
	}
}
