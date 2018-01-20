package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Phone extends BinaryContextAssertion {
	
	String sensorId;
	
	/** can be one of {START, END} */
	String status;
	
	public Phone() {
		setAcquisitionType(AcquisitionType.SENSED);
	}
	
	public Phone(String sensorId, String status, AnnotationData annotations) {
		super(new StringLiteral(sensorId), new StringLiteral(status), AcquisitionType.SENSED, annotations);
		
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
		setObject(new StringLiteral(status));
	}
	
	public String getSensorId() {
		return "phone";
	}
	
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
		setSubject(new StringLiteral(sensorId));
	}
}
