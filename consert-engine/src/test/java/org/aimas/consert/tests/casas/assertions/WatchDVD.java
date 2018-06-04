package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;


public class WatchDVD extends BinaryContextAssertion {

    String sensorId;

    /** can be one of {ON, OFF} */
    String status;

    public WatchDVD() {}

    public WatchDVD(AnnotationData annotations) {
        super(new StringLiteral("WatchDVD"),new StringLiteral("WatchDVD"),  AcquisitionType.DERIVED, annotations);

    }
    public WatchDVD(String sensorId, String status, AnnotationData annotations) {
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