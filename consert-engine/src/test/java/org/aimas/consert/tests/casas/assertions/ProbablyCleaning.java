package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.AssertionRole;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

import java.util.Date;

public class ProbablyCleaning extends BinaryContextAssertion {

	@AssertionRole("subject")
    String sensorId;

    /** can be one of {ON, OFF} */
	@AssertionRole("object")
	String status;
    
    
    long timestamp;
    double confidence;
    Date start;
    Date current;
    public ProbablyCleaning() {}

    @Override
    public ContextAssertion cloneContent() {
        return new ProbablyCleaning(null);
    }

    public ProbablyCleaning(AnnotationData annotations) {
        super(new StringLiteral("ProbablyCleaning"),new StringLiteral("ProbablyCleaning"),  AcquisitionType.DERIVED, annotations);

    }
    
    public ProbablyCleaning(long timestamp, double confidence, Date start, Date current) {
        super(new StringLiteral("ProbablyCleaning"),new StringLiteral("ProbablyCleaning"),  AcquisitionType.DERIVED, 
        		new DefaultAnnotationData(timestamp, confidence, start, current));

    }
    
    public ProbablyCleaning(String sensorId, String status, AnnotationData annotations) {
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