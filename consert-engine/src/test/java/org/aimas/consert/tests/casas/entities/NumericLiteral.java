package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

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
		return SimpleValueFactory.getInstance().createLiteral(value).getLabel();
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
	    NumericLiteral other = (NumericLiteral) obj;
	    if (value == null) {
		    if (other.value != null)
			    return false;
	    }
	    else if (!value.equals(other.value))
		    return false;
	    return true;
    }
}
