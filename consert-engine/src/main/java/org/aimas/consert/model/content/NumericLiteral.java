package org.aimas.consert.model.content;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class NumericLiteral extends BaseContextEntity {
	
	public NumericLiteral(Double value) {
	    this.isLiteral = true;
		this.value = value;
    }

	@Override
    public boolean isLiteral() {
	    return true;
    }
	
	public Double getValue() {
		return (Double)value;
	}

	@Override
    public String getEntityId() {
		return SimpleValueFactory.getInstance().createLiteral((Double)value).getLabel();
    }
	
	@Override
	public String toString() {
		return "" + value;
	}

	@Override
    public Object parseValueFromString(String serializedValue) {
	    return Double.parseDouble(serializedValue);
    }

	@Override
    public String serializeValue() {
	    return getValue().toString();
    }
}
