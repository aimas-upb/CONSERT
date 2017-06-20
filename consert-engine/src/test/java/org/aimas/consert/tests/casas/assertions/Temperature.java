package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.NumericLiteral;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Temperature extends BinaryContextAssertion {
	
	String sensorId;
	double value;
	
	public Temperature() {}
	
	public Temperature(String sensorId, double value, AnnotationData annotations) {
		super(new StringLiteral(sensorId), new NumericLiteral(value), AcquisitionType.SENSED, annotations);
		
		this.sensorId = sensorId;
		this.value = value;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
		setSubject(new StringLiteral(sensorId));
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		setObject(new NumericLiteral(value));
	}
	
	
}
