package org.aimas.consert.model;

import java.util.HashMap;
import java.util.Map;



public abstract class UnaryContextAssertion extends ContextAssertion {
	
	protected ContextEntity involvedEntity;
	
	public UnaryContextAssertion() {}
	
	public UnaryContextAssertion(ContextEntity involvedEntity,
			AnnotationData annotations, 
			AcquisitionType generationType) {
		super(generationType, ContextAssertion.UNARY, annotations);
		this.involvedEntity = involvedEntity;
	}
	
	public void setInvolvedEntity(ContextEntity involvedEntity) {
		this.involvedEntity = involvedEntity;
	}

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
