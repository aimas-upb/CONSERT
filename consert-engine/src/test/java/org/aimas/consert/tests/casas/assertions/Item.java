package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Item extends BinaryContextAssertion {
	
	String sensorId;
	
	/** can be one of {ABSENT, PRESENT} */
	String status;
	
	public Item() {}
	
	public Item(String sensorId, String status, AnnotationData annotations) {
		super(new StringLiteral(sensorId), new StringLiteral(status), AcquisitionType.PROFILED, annotations);
	
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		setObject(new StringLiteral(status));
	}
}
