package org.aimas.consert.tests.hhcpd.assertions;

import java.util.HashMap;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.AssertionRole;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextEntity;
import org.aimas.consert.model.content.NaryContextAssertion;
import org.aimas.consert.model.content.NumericLiteral;
import org.aimas.consert.model.content.StringLiteral;

public class Motion extends NaryContextAssertion {
	
	@AssertionRole("entity")
    String sensorId;

    /** can be one of {ON, OFF} */
	@AssertionRole("entity")
	String status;
	
	/** high level room information of where the sensor event triggers **/
	String location;
	
	/** auxiliary information marking the index of the sensor event in the recorded stream **/
	int eventId;
	
	public Motion() {}

	@Override
	public ContextAssertion cloneContent() {
		return new Motion(sensorId, status, location, eventId, null);
	}

	public Motion(String sensorId, String status, String location, int eventId,
			AnnotationData annotations) {
		super(
				new HashMap<String, ContextEntity>() {{
					put("sensorId", new StringLiteral(sensorId));
					put("status", new StringLiteral(status));
					put("location", new StringLiteral(location));
					put("eventId", new NumericLiteral((double)eventId));
				}}, 
				AcquisitionType.SENSED, annotations
			);
		
		this.sensorId = sensorId;
		this.status = status;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
		addEntity("sensorId", new StringLiteral(sensorId));
	}

	public String  getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		addEntity("status", new StringLiteral(status));
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
		addEntity("location", new StringLiteral(location));
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
		addEntity("eventId", new NumericLiteral((double)eventId));
	}
}
