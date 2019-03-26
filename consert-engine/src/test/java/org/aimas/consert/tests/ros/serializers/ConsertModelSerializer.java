package org.aimas.consert.tests.ros.serializers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.BaseContextEntity;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextAssertion.AcquisitionType;
import org.aimas.consert.model.content.ContextAssertionContent;
import org.aimas.consert.model.content.ContextEntity;
import org.aimas.consert.model.content.EntityDescription;
import org.aimas.consert.model.content.NaryContextAssertion;
import org.aimas.consert.model.eventwindow.EventWindow;
import org.aimas.consert.utils.TimestampPair;
import org.apache.commons.lang.StringUtils;
import org.drools.core.util.LinkedList;
import org.ros.message.MessageFactory;

import consert.EntityRole;

public class ConsertModelSerializer {
	
	private MessageFactory messageFactory;
	
	public ConsertModelSerializer(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	// =========================== Read and write ContextEntities =========================== //
	// ====================================================================================== //
	public consert.ContextEntity writeEntity(ContextEntity entity) {
		consert.ContextEntity msg = messageFactory.newFromType(consert.ContextEntity._TYPE);
		msg.setId(entity.getEntityId());
		msg.setType(entity.getClass().getName());
		msg.setIsLiteral(entity.isLiteral());
		msg.setValue(entity.getValue().toString());
		
		return msg;
	}
	
	public ContextEntity readEntity(consert.ContextEntity rosMessage) {
		String entityType = rosMessage.getType();
		try {
	        BaseContextEntity entity = (BaseContextEntity)Class.forName(entityType).newInstance();
	        
	        entity.setEntityId(rosMessage.getId());
	        entity.setLiteral(rosMessage.getIsLiteral());
	        entity.setValue(entity.parseValueFromString(rosMessage.getValue()));
	        
	        return entity;
		}
        catch (Exception e) {
	        e.printStackTrace();
        }
		
		return null;
	}
	
	// =========================== Read and write ContextAnnotations =========================== //
	// ========================================================================================= //
	public List<consert.ContextAnnotation> writeAnnotations(AnnotationData annotations) {
		DefaultAnnotationData annotationData = (DefaultAnnotationData) annotations;
		List<consert.ContextAnnotation> annMessages = new ArrayList<consert.ContextAnnotation>();
		
		// add timestamp annotation
		consert.ContextAnnotation timestampMsg = messageFactory.newFromType(consert.ContextAnnotation._TYPE);
		timestampMsg.setId("" + annotationData.getLastUpdated());
		timestampMsg.setType(DefaultAnnotationData.TIMESTAMP_TYPE);
		timestampMsg.setValue("" + annotationData.getLastUpdated());
		annMessages.add(timestampMsg);
		
		// add the certainty annotation
		consert.ContextAnnotation certaintyMsg = messageFactory.newFromType(consert.ContextAnnotation._TYPE);
		certaintyMsg.setId("" + annotationData.getConfidence());
		certaintyMsg.setType(DefaultAnnotationData.CERTAINTY_TYPE);
		certaintyMsg.setValue("" + annotationData.getConfidence());
		annMessages.add(certaintyMsg);
		
		// add the validity annotation
		consert.ContextAnnotation validityMsg = messageFactory.newFromType(consert.ContextAnnotation._TYPE);
		TimestampPair tsPair = new TimestampPair(annotationData.getStartTime().getTime(), annotationData.getEndTime().getTime());
		validityMsg.setId(tsPair.toString());
		validityMsg.setType(DefaultAnnotationData.VALIDITY_TYPE);
		validityMsg.setValue(tsPair.toString());
		annMessages.add(validityMsg);
		
		return annMessages;
	}
	
	
	public AnnotationData readAnnotations(List<consert.ContextAnnotation> rosMessages) {
		DefaultAnnotationData annData = new DefaultAnnotationData();
		
		if (rosMessages == null || rosMessages.isEmpty()) {
			// return an Annotation list that corresponds to a perfect atomic event
			annData.setConfidence(1.0);
			annData.setTimestamp(System.currentTimeMillis());
			annData.setStartTime(Calendar.getInstance().getTime());
			annData.setEndTime(Calendar.getInstance().getTime());
		}
		else {
			for (consert.ContextAnnotation ann : rosMessages) {
				if (ann.getType().equals(DefaultAnnotationData.TIMESTAMP_TYPE)) {
					annData.setTimestamp(Double.parseDouble(ann.getValue()));
				}
				else if (ann.getType().equals(DefaultAnnotationData.CERTAINTY_TYPE)) {
					annData.setConfidence(Double.parseDouble(ann.getValue()));
				}
				else if (ann.getType().equals(DefaultAnnotationData.VALIDITY_TYPE)) {
					TimestampPair tsPair = TimestampPair.parseString(ann.getValue());
					annData.setStartTime(new Date(tsPair.getStart()));
					annData.setEndTime(new Date(tsPair.getEnd()));
				}
			}
		}
		
		return annData;
	}
	
	// =========================== Read and write EntityDescriptions =========================== //
	// ========================================================================================= //
	public consert.EntityDescription writeEntityDescription(EntityDescription entityDescription) {
		consert.EntityDescription msg = messageFactory.newFromType(consert.EntityDescription._TYPE);
		
		msg.setId(entityDescription.getClass().getSimpleName());
		msg.setType(entityDescription.getClass().getName());
		msg.setSubject(writeEntity(entityDescription.getSubject()));
		msg.setObject(writeEntity(entityDescription.getObject()));
		
		return msg;
	}
	
	public EntityDescription readEntityDescription(consert.EntityDescription rosMessage) {
		String entityDescriptionType = rosMessage.getType();
		try {
	        EntityDescription entityDescription = (EntityDescription)Class.forName(entityDescriptionType).newInstance();
	        
	        entityDescription.setSubject(readEntity(rosMessage.getSubject()));
	        entityDescription.setObject(readEntity(rosMessage.getObject()));
	        
	        return entityDescription;
		}
        catch (Exception e) {
	        e.printStackTrace();
        }
        
		return null;
	}
	
	// =========================== Read and write ContextAssertions =========================== //
	// ======================================================================================== //
	public consert.ContextAssertion writeAssertion(ContextAssertion assertion) {
		consert.ContextAssertion msg = messageFactory.newFromType(consert.ContextAssertion._TYPE);
		
		msg.setId(assertion.getAssertionIdentifier());
		msg.setType(assertion.getClass().getName());
		msg.setAcquisitionType(assertion.getAcquisitionType().name());
		
		msg.setAnnotations(writeAnnotations(assertion.getAnnotations()));
		
		Set<Entry<String, ContextEntity>> roles = assertion.getEntities().entrySet();
		List<consert.EntityRole> rolesList = new ArrayList<EntityRole>();
		
		for (Entry<String, ContextEntity> entityRole : roles) {
			consert.EntityRole roleMsg = messageFactory.newFromType(consert.EntityRole._TYPE);
			roleMsg.setRole(entityRole.getKey());
			roleMsg.setEntity(writeEntity(entityRole.getValue()));
			
			rolesList.add(roleMsg);
		}
		
		msg.setEntities(rolesList);
		
		return msg;
	}
	
	
	private ContextAssertion readAssertionCommon(String assertionType, String assertionIdentifier, String acquisitionType, 
			List<consert.ContextAnnotation> annotations) 
					throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		ContextAssertion assertion = (ContextAssertion)Class.forName(assertionType).newInstance();
        assertion.setAssertionIdentifier(assertionIdentifier);
        assertion.setAcquisitionType(AcquisitionType.valueOf(acquisitionType));
        
        // set all the annotations
        AnnotationData annData = readAnnotations(annotations);
        assertion.setAnnotations(annData);
        
        return assertion;
	}
	
	
	
	public ContextAssertion readAssertion(consert.ContextAssertion rosMessage) {
		try {
			ContextAssertion assertion = readAssertionCommon(rosMessage.getType(), rosMessage.getId(), 
	        		rosMessage.getAcquisitionType(), rosMessage.getAnnotations());
			
			// if we are dealing with unary or binary assertions
			for (consert.EntityRole entityRole: rosMessage.getEntities()) {
				String entityMethodName = "set" + StringUtils.capitalize(entityRole.getRole());
				ContextEntity entity = readEntity(entityRole.getEntity());
				
				try {
					Method entityMethod = assertion.getClass().getMethod(entityMethodName, (Class<?>[])null);
					
					// if it is NOT a literal, the setter will take the whole ContextEntity instance as an argument
			        if (!entityRole.getEntity().getIsLiteral()) 
			        	entityMethod.invoke(assertion, entity);
			        else 
			        	// otherwise, the setter of the method takes in the value of the literal itself 
			        	entityMethod.invoke(assertion, entity.getValue());
				}
				catch (NoSuchMethodException ex) {
					// if there is no setter method (either generic or specific), check if it is an NaryContextAssertion
					if (assertion instanceof NaryContextAssertion) {
						NaryContextAssertion naryAssertion = (NaryContextAssertion) assertion;
						naryAssertion.addEntity(entityRole.getRole(), entity);
					}
					else 
						throw new NoSuchFieldException("No " + entityRole.getRole() + " found in ContextAssertion class of type: " + 
							rosMessage.getType() + " with arity: " + rosMessage.getArity());
				}
		        
			}
			
			return assertion;
        
		}
        catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        
		return null;
	}
	
	
	public consert.ContextAssertionContent writeContextAssertionContent(ContextAssertionContent assertionContent) {
		consert.ContextAssertionContent msg = messageFactory.newFromType(consert.ContextAssertion._TYPE);
		
		msg.setType(assertionContent.getType());
		msg.setAcquisitionType(assertionContent.getAcquisitionType());
		
		
		Set<Entry<String, ContextEntity>> roles = assertionContent.getEntities().entrySet();
		List<consert.EntityRole> rolesList = new ArrayList<EntityRole>();
		
		for (Entry<String, ContextEntity> entityRole : roles) {
			consert.EntityRole roleMsg = messageFactory.newFromType(consert.EntityRole._TYPE);
			roleMsg.setRole(entityRole.getKey());
			roleMsg.setEntity(writeEntity(entityRole.getValue()));
			
			rolesList.add(roleMsg);
		}
		
		msg.setEntities(rolesList);
		
		return msg;
	}
	
	public consert.EventWindow writeEventWindow(EventWindow eventWindow) {
		consert.EventWindow msg = messageFactory.newFromType(consert.EventWindow._TYPE);
		
		msg.setPossibleContextAssertion(writeContextAssertionContent(eventWindow.getPossibleAssertion()));
		msg.setStartTimestamp(eventWindow.getWindowStart());
		msg.setEndTimestamp(eventWindow.getWindowEnd());
		
		List<consert.ContextAssertion> supportingAssertions = new ArrayList<>();
		for (ContextAssertion assertion : eventWindow.getSupportingAssertions()) {
			supportingAssertions.add(writeAssertion(assertion));
		}
		
		msg.setSupportingAssertions(supportingAssertions);
		
		return msg;
    }
	
	
//	public ContextAssertion readAssertion(BinaryAssertion rosMessage) {
//		try {
//	        ContextAssertion assertion = readAssertionCommon(rosMessage.getType(), rosMessage.getId(), 
//	        		rosMessage.getAcquisitionType(), rosMessage.getAnnotations()); 
//	        		
//	        // set subject entity
//	        String subjectMethodName = "set" + StringUtils.capitalize(rosMessage.getSubject().getRole());
//	        Method subjectMethod = assertion.getClass().getMethod(subjectMethodName, (Class<?>[])null);
//	        
//	        // if it is NOT a literal, the setter will take the whole ContextEntity instance as an argument
//	        if (!rosMessage.getSubject().getEntity().getIsLiteral()) 
//	        	subjectMethod.invoke(assertion, readEntity(rosMessage.getSubject().getEntity()));
//	        else
//	        	// otherwise, the setter of the method takes in the value of the literal itself 
//	        	subjectMethod.invoke(assertion, readEntity(rosMessage.getSubject().getEntity()).getValue());
//	        
//	        
//	        // set the object entity
//	        String objectMethodName = "set" + StringUtils.capitalize(rosMessage.getObject().getRole());
//	        Method objectMethod = assertion.getClass().getMethod(objectMethodName, (Class<?>[])null);
//	        
//	        // if it is NOT a literal, the setter will take the whole ContextEntity instance as an argument
//	        if (!rosMessage.getObject().getEntity().getIsLiteral()) 
//	        	objectMethod.invoke(assertion, readEntity(rosMessage.getObject().getEntity()));
//	        else
//	        	// otherwise, the setter of the method takes in the value of the literal itself 
//	        	objectMethod.invoke(assertion, readEntity(rosMessage.getObject().getEntity()).getValue());
//	        
//	        return assertion;
//        }
//        catch (Exception e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//        }
//		
//		return null;
//	}
//	
//	public ContextAssertion readAssertion(NaryAssertion rosMessage) {
//		try {
//	        NaryContextAssertion assertion = (NaryContextAssertion) readAssertionCommon(rosMessage.getType(), rosMessage.getId(), 
//	        		rosMessage.getAcquisitionType(), rosMessage.getAnnotations()); 
//	        		
//	        // set entities
//	        for (consert.EntityRole role : rosMessage.getEntities()) {
//	        	assertion.addEntity(role.getRole(), readEntity(role.getEntity()));
//	        }
//	        
//	        return assertion;
//        }
//        catch (Exception e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//        }
//		
//		return null;
//	}
//	
//	public UnaryAssertion writeUnaryAssertion(UnaryContextAssertion assertion) {
//		consert.UnaryAssertion msg = messageFactory.newFromType(consert.UnaryAssertion._TYPE);
//		
//		msg.setId(assertion.getAssertionIdentifier());
//		msg.setType(assertion.getClass().getName());
//		msg.setAcquisitionType(assertion.getAcquisitionType().name());
//		
//		msg.setAnnotations(writeAnnotations(assertion.getAnnotations()));
//		
//		Entry<String, ContextEntity> entityRole = assertion.getEntities().entrySet().iterator().next();
//		consert.EntityRole roleMsg = messageFactory.newFromType(consert.EntityRole._TYPE);
//		roleMsg.setRole(entityRole.getKey());
//		roleMsg.setEntity(writeEntity(entityRole.getValue()));
//		msg.setEntity(roleMsg);
//		
//		return msg;
//	}
//	
//	public BinaryAssertion writeBinaryAssertion(BinaryContextAssertion assertion) {
//		consert.BinaryAssertion msg = messageFactory.newFromType(consert.BinaryAssertion._TYPE);
//		
//		msg.setId(assertion.getAssertionIdentifier());
//		msg.setType(assertion.getClass().getName());
//		msg.setAcquisitionType(assertion.getAcquisitionType().name());
//		
//		msg.setAnnotations(writeAnnotations(assertion.getAnnotations()));
//		
//		Set<Entry<String, ContextEntity>> roles = assertion.getEntities().entrySet();
//		for (Entry<String, ContextEntity> entityRole : roles) {
//			consert.EntityRole roleMsg = messageFactory.newFromType(consert.EntityRole._TYPE);
//			roleMsg.setRole(entityRole.getKey());
//			roleMsg.setEntity(writeEntity(entityRole.getValue()));
//			
//			if (entityRole.getValue().equals(assertion.getSubject())) {
//				msg.setSubject(roleMsg);
//			}
//			else {
//				msg.setObject(roleMsg);
//			}
//		}
//		
//		return msg;
//	}
//	
//	public NaryAssertion writeNaryAssertion(NaryContextAssertion assertion) {
//		consert.NaryAssertion msg = messageFactory.newFromType(consert.NaryAssertion._TYPE);
//		
//		msg.setId(assertion.getAssertionIdentifier());
//		msg.setType(assertion.getClass().getName());
//		msg.setAcquisitionType(assertion.getAcquisitionType().name());
//		
//		msg.setAnnotations(writeAnnotations(assertion.getAnnotations()));
//		
//		Set<Entry<String, ContextEntity>> roles = assertion.getEntities().entrySet();
//		List<consert.EntityRole> rolesList = new ArrayList<EntityRole>();
//		
//		for (Entry<String, ContextEntity> entityRole : roles) {
//			consert.EntityRole roleMsg = messageFactory.newFromType(consert.EntityRole._TYPE);
//			roleMsg.setRole(entityRole.getKey());
//			roleMsg.setEntity(writeEntity(entityRole.getValue()));
//			
//			rolesList.add(roleMsg);
//		}
//		
//		msg.setEntities(rolesList);
//		
//		return msg;
//	}
	
}
