package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.CabinetStatus;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Cabinet extends BinaryContextAssertion {
	
	String cabinetId;
	CabinetStatus cabinetStatus;
	
	public Cabinet() {}
	
	public Cabinet(String cabinetId, CabinetStatus cabinetStatus, AnnotationData annotations) {
		super(new StringLiteral(cabinetId), cabinetStatus, AcquisitionType.SENSED, annotations);
		
		this.cabinetId = cabinetId;
		this.cabinetStatus = cabinetStatus;
	}

	public String getCabinetId() {
		return cabinetId;
	}

	public void setCabinetId(String cabinetId) {
		this.cabinetId = cabinetId;
		setSubject(new StringLiteral(cabinetId));
	}

	public CabinetStatus getCabinetStatus() {
		return cabinetStatus;
	}

	public void setCabinetStatus(CabinetStatus cabinetStatus) {
		this.cabinetStatus = cabinetStatus;
		setObject(cabinetStatus);
	}
}
