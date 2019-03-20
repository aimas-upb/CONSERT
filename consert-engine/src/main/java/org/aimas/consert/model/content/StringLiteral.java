package org.aimas.consert.model.content;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class StringLiteral extends BaseContextEntity {
	
	public StringLiteral(String literal) {
		this.isLiteral = true;
		this.value = literal;
	}

	@Override
    public boolean isLiteral() {
	    return true;
    }

	public String getValue() {
		return (String) value;
	}
	
	@Override
    public String getEntityId() {
	    return SimpleValueFactory.getInstance().createLiteral((String)value).getLabel();
    }
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
    public Object parseValueFromString(String serializedValue) {
	    return serializedValue;
    }

	@Override
    public String serializeValue() {
	    return getValue();
    }
}
