package org.aimas.consert.model.content;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.model.annotations.AnnotationData;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BinaryContextAssertion extends ContextAssertion {
	
	protected ContextEntity subject;
	protected ContextEntity object;
	
	public BinaryContextAssertion() {}
	
	public BinaryContextAssertion(ContextEntity subject, ContextEntity object,
			AcquisitionType generationType, AnnotationData annotations) {
		super(generationType, ContextAssertion.BINARY, annotations);
		
		this.subject = subject;
		this.object = object;
	}
	
	@Override
	public Map<String, ContextEntity> getEntities() {
		Map<String, ContextEntity> entities = new HashMap<String, ContextEntity>();
		
		String subjectRole = AssertionRole.SUBJECT;
		String objectRole = AssertionRole.OBJECT;
		
		boolean subjectFound = false;
		boolean objectFound = false;
		
		for (Field field: getClass().getDeclaredFields()) {
			AssertionRole roleAnnotation = field.getAnnotation(AssertionRole.class);
			if (roleAnnotation != null && roleAnnotation.value().equals(AssertionRole.SUBJECT) && !subjectFound) {
				subjectRole = field.getName();
				subjectFound = true;
			}
			else if (roleAnnotation != null && roleAnnotation.value().equals(AssertionRole.OBJECT) && !objectFound) {
				objectRole = field.getName();
				objectFound = true;
			}
			
			if (subjectFound && objectFound)
				break;
		}
		
		entities.put(subjectRole, subject);
		entities.put(objectRole, object);
		
		return entities;
	}
	
	//@JsonIgnore
	public ContextEntity getSubject() {
		return subject;
	}

	public void setSubject(ContextEntity subject) {
		this.subject = subject;
	}
	
	//@JsonIgnore
	public ContextEntity getObject() {
		return object;
	}

	public void setObject(ContextEntity object) {
		this.object = object;
	}
	
}
