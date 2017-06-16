package org.aimas.consert.model;

import java.util.HashMap;
import java.util.Map;

public abstract class BinaryContextAssertion extends ContextAssertion {
	
	protected ContextEntity subject;
	protected ContextEntity object;
	
	public BinaryContextAssertion() {}
	
	public BinaryContextAssertion(ContextEntity subject, ContextEntity object,
			AcquisitionType generationType, AnnotationData annotations) {
		super(generationType, ContextAssertion.BINARY, annotations);
	}
	
	@Override
	public Map<String, ContextEntity> getEntities() {
		Map<String, ContextEntity> entities = new HashMap<String, ContextEntity>();
		entities.put("hasSubject", subject);
		entities.put("hasObject", object);
		
		return entities;
	}
}
