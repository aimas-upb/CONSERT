package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;


public class WaterPlants extends BinaryContextAssertion {

    String sensorId;

    /** can be one of {ON, OFF} */
    String status;

    public WaterPlants() {}

    public WaterPlants(AnnotationData annotations) {
        super(new StringLiteral("WaterPlants"),new StringLiteral("WaterPlants"),  AcquisitionType.DERIVED, annotations);

    }
    public WaterPlants(String sensorId, String status, AnnotationData annotations) {
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