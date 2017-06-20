package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.MotionStatus;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Motion extends BinaryContextAssertion {
	
	String sensorId;
	MotionStatus motionStatus;
	
	public Motion() {}
	
	public Motion(String sensorId, MotionStatus motionStatus, AnnotationData annotations) {
		super(new StringLiteral(sensorId), motionStatus, AcquisitionType.SENSED, annotations);
		
		this.sensorId = sensorId;
		this.motionStatus = motionStatus;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
		setSubject(new StringLiteral(sensorId));
	}

	public MotionStatus getMotionStatus() {
		return motionStatus;
	}

	public void setMotionStatus(MotionStatus motionStatus) {
		this.motionStatus = motionStatus;
		setObject(motionStatus);
	}
}
