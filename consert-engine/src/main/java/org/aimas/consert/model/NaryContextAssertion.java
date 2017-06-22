package org.aimas.consert.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class NaryContextAssertion extends ContextAssertion {
	
	protected Map<String, ContextEntity> involvedEntities;
	
	public NaryContextAssertion() {	}
	
	public NaryContextAssertion(Map<String, ContextEntity> involvedEntities,
	        AcquisitionType generationType, AnnotationData annotations) {
		
		super(generationType, ContextAssertion.NARY, annotations);
		
		this.involvedEntities = involvedEntities;
	}
	
	@Override
	@JsonIgnore
	public Map<String, ContextEntity> getEntities() {
		return involvedEntities;
	}
	
	public void setEntities(Map<String, ContextEntity> involvedEntities) {
		this.involvedEntities = involvedEntities;
	}
	
	public void addEntity(String entityRole, ContextEntity entity) {
		involvedEntities.put(entityRole, entity);
	}
}
