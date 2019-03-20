package org.aimas.consert.tests.casas.entitydescriptions;

import org.aimas.consert.model.content.EntityDescription;
import org.aimas.consert.model.content.NumericLiteral;
import org.aimas.consert.model.content.StringLiteral;

public class ActivityTriggered extends EntityDescription {
	
	private String name;
	
	private long timestamp;
	
	public ActivityTriggered() {
	}
	
	public ActivityTriggered(String name, long timestamp) {
		super(new StringLiteral(name), new NumericLiteral((double)timestamp));
		this.name = name;
		this.timestamp = timestamp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		setSubject(new StringLiteral(name));
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		setObject(new NumericLiteral((double)timestamp));
	}
	
	
	
}
