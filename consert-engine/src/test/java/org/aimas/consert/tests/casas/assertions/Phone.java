package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.PhoneUsage;

public class Phone extends UnaryContextAssertion {
	
	PhoneUsage usage;
	
	public Phone() {}
	
	public Phone(PhoneUsage usage, AnnotationData annotations) {
		super(usage, AcquisitionType.SENSED, annotations);
		
		this.usage = usage;
	}

	public PhoneUsage getUsage() {
		return usage;
	}

	public void setUsage(PhoneUsage usage) {
		this.usage = usage;
		setInvolvedEntity(usage);
	}
}
