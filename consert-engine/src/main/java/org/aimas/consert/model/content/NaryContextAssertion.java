package org.aimas.consert.model.content;

import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.model.annotations.AnnotationData;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class NaryContextAssertion extends ContextAssertion {
	
	protected Map<String, ContextEntity> entities;
	
	public NaryContextAssertion() {	
		entities = new HashMap<String, ContextEntity>();
	}
	
	public NaryContextAssertion(Map<String, ContextEntity> entities,
	        AcquisitionType generationType, AnnotationData annotations) {
		
		super(generationType, ContextAssertion.NARY, annotations);
		
		this.entities = entities;
	}
	
	@Override
	@JsonIgnore
	public Map<String, ContextEntity> getEntities() {
		return entities;
	}
	
	public void setEntities(Map<String, ContextEntity> entities) {
		this.entities = entities;
	}
	
	public void addEntity(String entityRole, ContextEntity entity) {
		entities.put(entityRole, entity);
	}
}
