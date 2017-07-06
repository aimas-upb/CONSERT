package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.ItemStatus;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Item extends BinaryContextAssertion {
	
	String sensorId;
	ItemStatus status;
	
	public Item() {}
	
	public Item(String sensorId, ItemStatus status, AnnotationData annotations) {
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

	public ItemStatus getStatus() {
		return status;
	}

	public void setStatus(ItemStatus status) {
		this.status = status;
		setObject(status);
	}
}
