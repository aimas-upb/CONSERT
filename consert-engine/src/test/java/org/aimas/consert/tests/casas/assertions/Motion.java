package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Motion extends BinaryContextAssertion {
	
	String sensorId;
	
	/** can be one of {ON, OFF} */
	String status;
	
	public Motion() {}

	@Override
	public ContextAssertion cloneContent() {
		return new Motion(sensorId, status, null);
	}

	public Motion(String sensorId, String status, AnnotationData annotations) {
		super(new StringLiteral(sensorId), new StringLiteral(status), AcquisitionType.SENSED, annotations);
		
		this.sensorId = sensorId;
		this.status = status;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
		setSubject(new StringLiteral(sensorId));
	}

	public String  getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		setObject(new StringLiteral(status));
	}
}
