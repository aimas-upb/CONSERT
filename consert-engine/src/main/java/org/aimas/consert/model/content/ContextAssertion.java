package org.aimas.consert.model.content;

import java.util.Map;

import org.aimas.consert.model.Constants;
import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.annotations.AnnotationUtils;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
	"core = " + Constants.CORE_NS,
	"annotation = " + Constants.ANNOTATION_NS,
	"rdfbeans = " + Constants.RDFBEANS_URI
})
@RDFBean("core:ContextAssertion")
public abstract class ContextAssertion {
	private static int assertionCounter = 1;
	
	protected String assertionIdentifier = Constants.CORE_NS + "assertion-" + (assertionCounter++);
	
	/* ================== ContextAssertion characterization ================== */
	public enum AcquisitionType {
		DERIVED,
		PROFILED,
		SENSED
	}
	
	public static final int UNARY = 1;
	public static final int BINARY = 2;
	public static final int NARY = 3;
	
	
	public ContextAssertion() {}
	
	public ContextAssertion(AcquisitionType acquisitionType, int arity, AnnotationData annotationData) {
		this.acquisitionType = acquisitionType;
		this.arity = arity;
		setAnnotations(annotationData);
	}
	
	
	/* ================== ContextAssertion content ================== */
	protected abstract Map<String, ContextEntity> getEntities();
	
	@RDFSubject
	public String getAssertionIdentifier() {
		return assertionIdentifier;
	}
	
	public void setAssertionIdentifier(String assertionIdentifier) {
		this.assertionIdentifier = assertionIdentifier;
	}
	
	@RDF("rdfbeans:bindingClass")
	public String getQualifiedBindingClassName() {
		return this.getClass().getName();
	}
	
	public void setQualifiedBindingClassName(String name) {	}
	
	protected int arity = BINARY;
	protected AcquisitionType acquisitionType = AcquisitionType.SENSED;
	
	/**
	 * Get the acquisition method of the ContextAssertion
	 * @return the {@see org.aimas.consert.model.AcquisitionType} of the ContextAssertion
	 */
	public AcquisitionType getAcquisitionType() {
		return acquisitionType;
	}
	
	public void setAcquisitionType(AcquisitionType acquisitionType) {
		this.acquisitionType = acquisitionType;
	}
	
	public boolean isDerived() {
		return acquisitionType == AcquisitionType.DERIVED;
	}
	
	
	public boolean isSensed() {
		return acquisitionType == AcquisitionType.SENSED;
	}
	
	public boolean isProfiled() {
		return acquisitionType == AcquisitionType.PROFILED;
	}
	
	
	/**
	 * Get the arity of this ContextAssertion
	 * @return 1 for UnaryContextAssertion, 2 for binary and 3 for n-ary
	 */
	public int getAssertionArity() {
		return arity;
	}
	
	
	public boolean isUnary() {
		return arity == UNARY;
	}
	
	public boolean isBinary() {
		return arity == BINARY;
	}
	
	public boolean isNary() {
		return arity == NARY;
	}
	
	
	/* ================== ContextAssertion annotations ================== */
	protected AnnotationData annotationData;
	
	
	protected double startTimestamp;
	protected long eventDuration;

	private void setOccurrenceInfo(AnnotationData annotationData) {
		if (annotationData != null) {
			startTimestamp = annotationData.getTimestamp();
	    	eventDuration = annotationData.getDuration();
		}
	}
	
	
	/* ================== Internal performance monitoring ================== */ 
	protected long processingTimeStamp;
	
	public void setProcessingTimeStamp(long processingTimeStamp) {
        this.processingTimeStamp = processingTimeStamp;
    }

	public long getProcessingTimeStamp() {
		return processingTimeStamp;
	}
	
	
	@RDF("annotation:hasAnnotation")
	public AnnotationData getAnnotations() {
		return annotationData;
	}

	public void setAnnotations(AnnotationData annotationData) {
		this.annotationData = annotationData;
		setOccurrenceInfo(annotationData);
	}
	
	public double getStartTimestamp() {
		return startTimestamp;
	}

	public long getEventDuration() {
		return eventDuration;
	}
	
	
	/* ================== Auxiliary methods ================== */ 
	public String getStreamName() {
		return getClass().getSimpleName() + "Stream";
	};
	
	public String getExtendedStreamName() {
		return "Extended" + getStreamName();
	}
	
	public int getContentHash() {
		final int prime = 31;
		int result = 1;
	    
		for (ContextEntity entity : getEntities().values()) {
			result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		}
	    
	    return result;
	}
	
	
	public boolean allowsContentContinuity(ContextAssertion event) {
		if (event == null)
			return false;
		
		if (!getClass().equals(event.getClass()))
			return false;
		
		Map<String, ContextEntity> myEntities = getEntities();
		Map<String, ContextEntity> otherEntities = event.getEntities();
		
		for (String entityRole : myEntities.keySet()) {
			if (!otherEntities.containsKey(entityRole)) {
				return false;
			}
			else {
				Object myEntityVal = myEntities.get(entityRole).getValue();
				Object otherEntityVal = otherEntities.get(entityRole).getValue();
				
				if (!myEntityVal.equals(otherEntityVal))
					return false;
			}
		}
		
		return true;
	}
	
	public boolean isOverlappedBy(ContextAssertion event) {
	    return AnnotationUtils.isValidityOverlap(getAnnotations(), event.getAnnotations());
	}    

}
