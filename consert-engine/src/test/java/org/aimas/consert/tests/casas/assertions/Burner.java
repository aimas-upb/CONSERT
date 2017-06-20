package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.ContextAssertion.AcquisitionType;
import org.aimas.consert.model.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.NumericLiteral;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Burner extends UnaryContextAssertion {
	
	double value;
	
	public Burner() {}
	
	public Burner(String burnerId, double value, AnnotationData annotations) {
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
