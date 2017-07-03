package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.MotionStatus;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Motion extends BinaryContextAssertion {
	
	String sensorId;
	MotionStatus status;
	
	public Motion() {}
	
	public Motion(String sensorId, MotionStatus status, AnnotationData annotations) {
		super(new StringLiteral(sensorId), status, AcquisitionType.SENSED, annotations);
		
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

	public MotionStatus getStatus() {
		return status;
	}

	public void setStatus(MotionStatus status) {
		this.status = status;
		setObject(status);
	}
}
