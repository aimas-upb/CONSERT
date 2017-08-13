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

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((literal == null) ? 0 : literal.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    StringLiteral other = (StringLiteral) obj;
	    if (literal == null) {
		    if (other.literal != null)
			    return false;
	    }
	    else if (!literal.equals(other.literal))
		    return false;
	    return true;
    }
	
	
}
