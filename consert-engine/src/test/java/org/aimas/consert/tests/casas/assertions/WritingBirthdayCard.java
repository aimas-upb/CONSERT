package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.AssertionRole;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;


public class WritingBirthdayCard extends BinaryContextAssertion {

	@AssertionRole("subject")
    String sensorId;

    /** can be one of {ON, OFF} */
	@AssertionRole("object")
	String status;

    public WritingBirthdayCard() {}

    @Override
    public ContextAssertion cloneContent() {
        return new WritingBirthdayCard(null);
    }

    public WritingBirthdayCard(AnnotationData annotations) {
        super(new StringLiteral("WritingBirthdayCard"),new StringLiteral("WritingBirthdayCard"),  AcquisitionType.DERIVED, annotations);

    }
    public WritingBirthdayCard(String sensorId, String status, AnnotationData annotations) {
        super(new StringLiteral(sensorId), new StringLiteral(status), AcquisitionType.DERIVED, annotations);

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