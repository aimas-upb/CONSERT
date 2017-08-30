package org.aimas.consert.model.annotations;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces("annotation = " + Constants.ANNOTATION_NS)
@RDFBean("annotation:NumericCertaintyAnnotation")
public class NumericCertaintyAnnotation extends StructuredAnnotation {
	
	private double value;
	private String continuityFunction;
	private String extensionOperator;
	private String combinationOperator;

	private double differenceThreshold;
	private double valueThreshold;
	
	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	private static Map<String, BiFunction<NumericCertaintyAnnotation, 
		NumericCertaintyAnnotation, NumericCertaintyAnnotation>> operatorMap;
	
	static {
		operatorMap = new HashMap<String, 
				BiFunction<NumericCertaintyAnnotation,NumericCertaintyAnnotation,NumericCertaintyAnnotation>>();
		operatorMap.put("min", NumericCertaintyAnnotation::min);
		operatorMap.put("max", NumericCertaintyAnnotation::max);
		operatorMap.put("avg", NumericCertaintyAnnotation::avg);
	}
	
	public NumericCertaintyAnnotation() {
		super();
	}
	
	public NumericCertaintyAnnotation(Double value, String continuityFunction,
            String extensionOperator, String combinationOperator, 
            double valueThreshold, double differenceThreshold) {
	    this.value = value;
	    this.continuityFunction = continuityFunction;
	    this.extensionOperator = extensionOperator;
	    this.combinationOperator = combinationOperator;
	    
	    this.valueThreshold = valueThreshold;
	    this.differenceThreshold = differenceThreshold;
    }
	
	
	public NumericCertaintyAnnotation(NumericCertaintyAnnotation other) {
		// conveniance constructor to build a copy
		
		this.value = other.getValue();
		this.valueThreshold = other.getValueThreshold();
		this.differenceThreshold = other.getDifferenceThreshold();
		this.continuityFunction = other.getContinuityFunction();
		this.combinationOperator = other.getCombinationOperator();
		this.extensionOperator = other.getExtensionOperator();
	}
	
	public void setValue(Double value) {
		if (value != null) {
			this.value = value;
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

	public void setDifferenceThreshold(double differenceThreshold) {
		this.differenceThreshold = differenceThreshold;
	}
	
	public void setValueThreshold(double valueThreshold) {
		this.valueThreshold = valueThreshold;
	}
	
	
	
	@RDF("annotation:hasContinuityDifferenceThreshold")
	public double getDifferenceThreshold() {
		return differenceThreshold;
	}
	
	@RDF("annotation:hasValueThreshold")
	public double getValueThreshold() {
		return valueThreshold;
	}
	
	@Override
	@RDF("annotation:hasValue")
    public Double getValue() {
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
	
	
	public boolean allowsInsertion() {
		if (value < valueThreshold)
			return false;

		return true;
	}
	
	@Override
	public String toString() {
		return "[NumericCertaintyAnnotation value = " + value + "]";
	}
	
	
	@Override
    public NumericCertaintyAnnotation applyCombinationOperator(StructuredAnnotation other) {
		if (!(other instanceof NumericCertaintyAnnotation)) {
			return new NumericCertaintyAnnotation(this);
		}
		
		NumericCertaintyAnnotation otherAnn = (NumericCertaintyAnnotation)other;
		BiFunction<NumericCertaintyAnnotation, 
			NumericCertaintyAnnotation,
			NumericCertaintyAnnotation> combinationOp = operatorMap.get(combinationOperator);
		
		return combinationOp.apply(this, otherAnn);
    }


	@Override
    public NumericCertaintyAnnotation applyExtensionOperator(StructuredAnnotation other) {
		if (!(other instanceof NumericCertaintyAnnotation)) {
			return new NumericCertaintyAnnotation(this);
		}
		
		NumericCertaintyAnnotation otherAnn = (NumericCertaintyAnnotation)other;
		BiFunction<NumericCertaintyAnnotation, 
			NumericCertaintyAnnotation,
			NumericCertaintyAnnotation> extensionOp = operatorMap.get(extensionOperator);
		
		return extensionOp.apply(this, otherAnn);
    }


	@Override
    public boolean allowsContinuity(StructuredAnnotation other) {
		if (!(other instanceof NumericCertaintyAnnotation)) {
			return false;
		}
		
		NumericCertaintyAnnotation otherAnn = (NumericCertaintyAnnotation)other;
		
		if (otherAnn.getValue() < valueThreshold) {
			return false;
		}
		
		if (Math.abs(getValue() - otherAnn.getValue()) > differenceThreshold)
			return false;
		
	    return true;
    }
	
	
	public static NumericCertaintyAnnotation min(NumericCertaintyAnnotation c1, NumericCertaintyAnnotation c2) {
		
		double minVal = c1.getValue() < c2.getValue() ? c1.getValue() : c2.getValue();
		
		NumericCertaintyAnnotation c = new NumericCertaintyAnnotation(c1);
		c.setValue(minVal);
		
		return c;
	}
	
	
	public static NumericCertaintyAnnotation max(NumericCertaintyAnnotation c1, NumericCertaintyAnnotation c2) {
		
		double maxVal = c1.getValue() > c2.getValue() ? c1.getValue() : c2.getValue();
		
		NumericCertaintyAnnotation c = new NumericCertaintyAnnotation(c1);
		c.setValue(maxVal);
		
		return c;
	}
	
	
	public static NumericCertaintyAnnotation avg(NumericCertaintyAnnotation c1, NumericCertaintyAnnotation c2) {
		
		double avg = (c1.getValue() + c2.getValue()) / 2;
		
		NumericCertaintyAnnotation c = new NumericCertaintyAnnotation(c1);
		c.setValue(avg);
		
		return c;
	}
	
	
}
