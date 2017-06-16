package org.aimas.consert.model;

import java.util.Map;

public abstract class NaryContextAssertion extends ContextAssertion {
	
	protected Map<String, ContextEntity> involvedEntities;
	
	public NaryContextAssertion() {	}
	
	public NaryContextAssertion(Map<String, ContextEntity> involvedEntities,
	        AcquisitionType generationType, AnnotationData annotations) {
		
		super(generationType, ContextAssertion.NARY, annotations);
		this.involvedEntities = involvedEntities;
	}
	
	@Override
	public Map<String, ContextEntity> getEntities() {
		return involvedEntities;
	}
	
}
