package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces("annotation = " + Constants.ANNOTATION_NS)
@RDFBean("annotation:NumericTimestampAnnotation")
public class NumericTimestampAnnotation extends StructuredAnnotation {
	
	private double value;
	private String continuityFunction;
	private String extensionOperator;
	private String combinationOperator;
	
	public NumericTimestampAnnotation() {
		
	}

	public NumericTimestampAnnotation(double value, String continuityFunction,
            String extensionOperator, String combinationOperator) {
	    this.value = value;
	    this.continuityFunction = continuityFunction;
	    this.extensionOperator = extensionOperator;
	    this.combinationOperator = combinationOperator;
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
	
}
