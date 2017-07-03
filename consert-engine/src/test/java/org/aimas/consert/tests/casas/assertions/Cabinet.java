package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.CabinetStatus;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Cabinet extends BinaryContextAssertion {
	
	String sensorId;
	CabinetStatus status;
	
	public Cabinet() {}
	
	public Cabinet(String sensorId, CabinetStatus status, AnnotationData annotations) {
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

	public CabinetStatus getCabinetStatus() {
		return status;
	}

	public void setCabinetStatus(CabinetStatus status) {
		this.status = status;
		setObject(status);
	}
}
