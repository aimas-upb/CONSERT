package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.AssertionRole;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.StringLiteral;
import org.aimas.consert.model.content.UnaryContextAssertion;

public class PersonLocation extends UnaryContextAssertion {
	@AssertionRole
	private String location;
	
	public PersonLocation() {
	}

	@Override
	public ContextAssertion cloneContent() {
		return new PersonLocation(location, null);
	}

	public PersonLocation(String location, AnnotationData annotations) {
		super(new StringLiteral(location), AcquisitionType.DERIVED, annotations);
		
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
		setEntity(new StringLiteral(location));
	}
}
