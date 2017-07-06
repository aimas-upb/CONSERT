package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.PhoneUsage;

public class Phone extends UnaryContextAssertion {
	
	PhoneUsage value;
	
	public Phone() {}
	
	public Phone(PhoneUsage value, AnnotationData annotations) {
		super(value, AcquisitionType.SENSED, annotations);
		
		this.value = value;
	}

	public PhoneUsage getValue() {
		return value;
	}

	public void setValue(PhoneUsage value) {
		this.value = value;
		setInvolvedEntity(value);
	}
}
