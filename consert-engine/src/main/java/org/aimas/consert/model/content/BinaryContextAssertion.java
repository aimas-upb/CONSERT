package org.aimas.consert.model.content;

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
		entities.put("hasSubject", subject);
		entities.put("hasObject", object);
		
		return entities;
	}
	
	@JsonIgnore
	public ContextEntity getSubject() {
		return subject;
	}

	public void setSubject(ContextEntity subject) {
		this.subject = subject;
	}
	
	@JsonIgnore
	public ContextEntity getObject() {
		return object;
	}

	public void setObject(ContextEntity object) {
		this.object = object;
	}
	
}
