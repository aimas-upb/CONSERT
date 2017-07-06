package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;

public class TemporalValidityAnnotation implements StructuredAnnotation {
	private static long instanceCt = 1;
	private String annotationIdentifier;
	
	DatetimeInterval value;
	private String continuityFunction;
	private String extensionOperator;
	private String combinationOperator;
	
	public TemporalValidityAnnotation() { }
	
	public TemporalValidityAnnotation(DatetimeInterval value,
            String continuityFunction, String extensionOperator,
            String combinationOperator) {
	    this.value = value;
	    this.continuityFunction = continuityFunction;
	    this.extensionOperator = extensionOperator;
	    this.combinationOperator = combinationOperator;
    }

	@Override
	public String getAnnotationIdentifier() {
		if (annotationIdentifier == null) {
			annotationIdentifier = Constants.ANNOTATION_BASE_URI + "NumerticCertaintyAnnotation#Certainty" + (instanceCt++); 
		}
		
		return annotationIdentifier;
	}
	
	@Override
	public void setAnnotationIdentifier(String annotationId) {
		this.annotationIdentifier = annotationId;
	}
	
	@Override
    public Object getValue() {
	    return value;
    }

	@Override
    public String getContinuityFunction() {
	    return continuityFunction;
    }

	@Override
    public String getExtensionOperator() {
	    return extensionOperator;
    }

	@Override
    public String getCombinationOperator() {
	    return combinationOperator;
    }

	public void setValue(DatetimeInterval value) {
		this.value = value;
	}

	public void setContinuityFunction(String continuityFunction) {
		this.continuityFunction = continuityFunction;
	}

	public void setExtensionOperator(String extensionOperator) {
		this.extensionOperator = extensionOperator;
	}

	public void setCombinationOperator(String combinationOperator) {
		this.combinationOperator = combinationOperator;
	}
	
	@Override
    public String getBindingClassName() {
	    return getClass().getName();
    }
	
}
