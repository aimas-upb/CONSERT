package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;

public class NumericTimestampAnntation implements StructuredAnnotation {
	private static long instanceCt = 1;
	private String annotationIdentifier;
	
	private double value;
	private String continuityFunction;
	private String extensionOperator;
	private String combinationOperator;
	
	public NumericTimestampAnntation() {
		
	}

	public NumericTimestampAnntation(double value, String continuityFunction,
            String extensionOperator, String combinationOperator) {
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

	public void setValue(Object value) {
		if (value instanceof Double) {
			this.value = ((Double) value).doubleValue();
		}
		else if (value instanceof String) {
			try {
				double val = Double.parseDouble((String) value);
				this.value = val;
			}
			catch (NumberFormatException ex) {
				this.value = 0;
			}
		}
		else {
			this.value = 0;
		}
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
