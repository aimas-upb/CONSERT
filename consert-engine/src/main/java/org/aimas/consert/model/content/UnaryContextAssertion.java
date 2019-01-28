package org.aimas.consert.model.content;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.model.annotations.AnnotationData;

import com.fasterxml.jackson.annotation.JsonIgnore;



public abstract class UnaryContextAssertion extends ContextAssertion {
	
	protected ContextEntity entity;
	
	public UnaryContextAssertion() {}
	
	public UnaryContextAssertion(ContextEntity entity,
			AcquisitionType generationType, AnnotationData annotations) {
		super(generationType, ContextAssertion.UNARY, annotations);
		
		this.entity = entity;
	}
	
	public void setEntity(ContextEntity entity) {
		this.entity = entity;
	}
	
	//@JsonIgnore
	public ContextEntity getEntity() {
		return entity;
	}
		
	@Override
	public Map<String, ContextEntity> getEntities() {
		Map<String, ContextEntity> entities = new HashMap<String, ContextEntity>();
		
		String roleName = AssertionRole.ENTITY;
		for (Field field: getClass().getDeclaredFields()) {
			AssertionRole roleAnnotation = field.getAnnotation(AssertionRole.class);
			if (roleAnnotation != null && roleAnnotation.value().equals(AssertionRole.ENTITY)) {
				roleName = field.getName();
				
				// find the first such usage
				break;
			}
		}
		
		entities.put(roleName, entity);
		
		return entities;
	}
}
