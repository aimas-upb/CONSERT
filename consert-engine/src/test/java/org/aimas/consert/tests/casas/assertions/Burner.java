package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.NumericLiteral;

public class Burner extends UnaryContextAssertion {
	
	double value;
	
	public Burner() {}
	
	public Burner(double value, AnnotationData annotations) {
		super(new NumericLiteral(value), AcquisitionType.SENSED, annotations);
		
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		setInvolvedEntity(new NumericLiteral(value));
	}
}
