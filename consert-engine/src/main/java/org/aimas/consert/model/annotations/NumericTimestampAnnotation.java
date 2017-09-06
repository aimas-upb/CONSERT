package org.aimas.consert.model.annotations;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces("annotation = " + Constants.ANNOTATION_NS)
@RDFBean("annotation:NumericTimestampAnnotation")
public class NumericTimestampAnnotation extends StructuredAnnotation {
	
	private double value;
	private String continuityFunction;
	private String extensionOperator;
	private String combinationOperator;
	
	private double differenceThreshold;
	
	private static Map<String, BiFunction<NumericTimestampAnnotation, 
		NumericTimestampAnnotation, NumericTimestampAnnotation>> operatorMap;

	static {
		operatorMap = new HashMap<String, 
				BiFunction<NumericTimestampAnnotation,NumericTimestampAnnotation,NumericTimestampAnnotation>>();
		operatorMap.put("min", NumericTimestampAnnotation::min);
		operatorMap.put("max", NumericTimestampAnnotation::max);
	}
	
	
	public NumericTimestampAnnotation() {
		
	}

	public NumericTimestampAnnotation(Double value, String continuityFunction,
            String extensionOperator, String combinationOperator,
            double differenceThreshold) {
	    this.value = value;
	    this.continuityFunction = continuityFunction;
	    this.extensionOperator = extensionOperator;
	    this.combinationOperator = combinationOperator;
	    
	    this.differenceThreshold = differenceThreshold;
    }
	
	
	public NumericTimestampAnnotation(NumericTimestampAnnotation other) {
		// conveniance constructor to build a copy
		this.value = other.getValue();
		this.differenceThreshold = other.getDifferenceThreshold();
		this.continuityFunction = other.getContinuityFunction();
		this.combinationOperator = other.getCombinationOperator();
		this.extensionOperator = other.getExtensionOperator();
	}
	
	@Override
	@RDF("annotation:hasValue")
    public Double getValue() {
	    return value;
    }
	
	@RDF("annotation:hasContinuityDifferenceThreshold")
	public double getDifferenceThreshold() {
		return differenceThreshold;
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

	
	public void setValue(Double value) {
		if (value != null) {
			this.value = value;
		}
		else {
			this.value = 0;
		}
	}
	
	public void setDifferenceThreshold(double differenceThreshold) {
		this.differenceThreshold = differenceThreshold;
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
	public String toString() {
		return "[NumericTimestampAnnotation value = " + value + "]";
	}

	
	
	@Override
    public boolean allowsInsertion() {
	    return true;
    }
	
	
	@Override
	public boolean allowsContinuity(StructuredAnnotation other) {
		if (!(other instanceof NumericTimestampAnnotation)) {
			return false;
		}
		
		NumericTimestampAnnotation otherAnn = (NumericTimestampAnnotation)other;
		
		if (Math.abs(getValue() - otherAnn.getValue()) > differenceThreshold)
			return false;
		
	    return true;
	}
	
	
	@Override
    public NumericTimestampAnnotation applyCombinationOperator(StructuredAnnotation other) {
		if (!(other instanceof NumericTimestampAnnotation)) {
			return new NumericTimestampAnnotation(this);
		}
		
		NumericTimestampAnnotation otherAnn = (NumericTimestampAnnotation)other;
		BiFunction<NumericTimestampAnnotation, 
			NumericTimestampAnnotation,
			NumericTimestampAnnotation> combinationOp = operatorMap.get(combinationOperator);
		
		return combinationOp.apply(this, otherAnn);
    }
	
	
	@Override
    public NumericTimestampAnnotation applyExtensionOperator(StructuredAnnotation other) {
		if (!(other instanceof NumericTimestampAnnotation)) {
			return new NumericTimestampAnnotation(this);
		}
		
		NumericTimestampAnnotation otherAnn = (NumericTimestampAnnotation)other;
		BiFunction<NumericTimestampAnnotation, 
			NumericTimestampAnnotation,
			NumericTimestampAnnotation> extensionOp = operatorMap.get(extensionOperator);
		
		return extensionOp.apply(this, otherAnn);
    }
	
	
	public static NumericTimestampAnnotation min(NumericTimestampAnnotation c1, NumericTimestampAnnotation c2) {
		
		double minVal = c1.getValue() < c2.getValue() ? c1.getValue() : c2.getValue();
		
		NumericTimestampAnnotation c = new NumericTimestampAnnotation(c1);
		c.setValue(minVal);
		
		return c;
	}
	
	
	public static NumericTimestampAnnotation max(NumericTimestampAnnotation c1, NumericTimestampAnnotation c2) {
		
		double maxVal = c1.getValue() > c2.getValue() ? c1.getValue() : c2.getValue();
		
		NumericTimestampAnnotation c = new NumericTimestampAnnotation(c1);
		c.setValue(maxVal);
		
		return c;
	}
}
