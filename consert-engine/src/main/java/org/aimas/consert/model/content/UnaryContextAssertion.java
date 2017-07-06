package org.aimas.consert.model.content;

import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.model.annotations.AnnotationData;

import com.fasterxml.jackson.annotation.JsonIgnore;



public abstract class UnaryContextAssertion extends ContextAssertion {
	
	protected ContextEntity involvedEntity;
	
	public UnaryContextAssertion() {}
	
	public UnaryContextAssertion(ContextEntity involvedEntity,
			AcquisitionType generationType, AnnotationData annotations) {
		super(generationType, ContextAssertion.UNARY, annotations);
		
		this.involvedEntity = involvedEntity;
	}
	
	public void setInvolvedEntity(ContextEntity involvedEntity) {
		this.involvedEntity = involvedEntity;
	}
	
	@JsonIgnore
	public ContextEntity getInvolvedEntity() {
		return involvedEntity;
	}
		
	@Override
	public Map<String, ContextEntity> getEntities() {
		Map<String, ContextEntity> entities = new HashMap<String, ContextEntity>();
		entities.put("hasEntity", involvedEntity);
		
		return entities;
	}
	
}
