package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Phone extends UnaryContextAssertion {
	/** can be one of {START, END} */
	String value;
	
	public Phone() {}
	
	public Phone(String value, AnnotationData annotations) {
		super(new StringLiteral(value), AcquisitionType.SENSED, annotations);
		
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		setInvolvedEntity(new StringLiteral(value));
	}

	public String getSensorId() {
		return "phone";
	}
}
