package org.aimas.consert.model.annotations;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces("annotation = " + Constants.ANNOTATION_NS)
@RDFBean("annotation:TemporalValidityAnnotation")
public class TemporalValidityAnnotation extends StructuredAnnotation {
	
	DatetimeInterval value;
	private String continuityFunction;
	private String extensionOperator;
	private String combinationOperator;
	
	private long differenceThreshold;
	
	
	private static Map<String, BiFunction<TemporalValidityAnnotation, 
		TemporalValidityAnnotation, TemporalValidityAnnotation>> operatorMap;

	static {
		operatorMap = new HashMap<String, 
				BiFunction<TemporalValidityAnnotation,TemporalValidityAnnotation,TemporalValidityAnnotation>>();
		operatorMap.put("intersect", TemporalValidityAnnotation::intersect);
		operatorMap.put("extend", TemporalValidityAnnotation::extend);
	}
	
	public TemporalValidityAnnotation() { }

	public TemporalValidityAnnotation(DatetimeInterval value,
            String continuityFunction, String extensionOperator,
            String combinationOperator,
            long differenceThreshold) {
	    this.value = value;
	    this.continuityFunction = continuityFunction;
	    this.extensionOperator = extensionOperator;
	    this.combinationOperator = combinationOperator;
	    
	    this.differenceThreshold = differenceThreshold; 
    }
	
	public TemporalValidityAnnotation(TemporalValidityAnnotation other) {
		// conveniance constructor to build a copy
		this.value = other.getValue();
		this.differenceThreshold = other.getDifferenceThreshold();
		this.continuityFunction = other.getContinuityFunction();
		this.combinationOperator = other.getCombinationOperator();
		this.extensionOperator = other.getExtensionOperator();
	}
	
	@Override
	@RDF("annotation:hasValue")
    public DatetimeInterval getValue() {
	    return value;
    }
	
	@RDF("annotation:hasContinuityDifferenceThreshold")
	public long getDifferenceThreshold() {
		return differenceThreshold;
	}

	public void setDifferenceThreshold(long differenceThreshold) {
		this.differenceThreshold = differenceThreshold;
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
    public TemporalValidityAnnotation applyCombinationOperator(StructuredAnnotation other) {
		if (!(other instanceof TemporalValidityAnnotation)) {
			return new TemporalValidityAnnotation(this);
		}
		
		TemporalValidityAnnotation otherAnn = (TemporalValidityAnnotation)other;
		BiFunction<TemporalValidityAnnotation, 
			TemporalValidityAnnotation,
			TemporalValidityAnnotation> combinationOp = operatorMap.get(combinationOperator);
		
		return combinationOp.apply(this, otherAnn);
    }

	@Override
    public TemporalValidityAnnotation applyExtensionOperator(StructuredAnnotation other) {
		if (!(other instanceof TemporalValidityAnnotation)) {
			return new TemporalValidityAnnotation(this);
		}
		
		TemporalValidityAnnotation otherAnn = (TemporalValidityAnnotation)other;
		BiFunction<TemporalValidityAnnotation, 
			TemporalValidityAnnotation,
			TemporalValidityAnnotation> extensionOp = operatorMap.get(extensionOperator);
		
		return extensionOp.apply(this, otherAnn);
    }
	
	
	@Override
	public boolean allowsContinuity(StructuredAnnotation other) {
		if (!(other instanceof TemporalValidityAnnotation)) {
			return false;
		}
		
		TemporalValidityAnnotation otherAnn = (TemporalValidityAnnotation)other;
		
		return otherAnn.getValue().getStart().getTime() - getValue().getEnd().getTime() < differenceThreshold;
	}
	
	
	@Override
    public boolean allowsInsertion() {
	    return true;
    }
	
	
	public static TemporalValidityAnnotation intersect(TemporalValidityAnnotation t1, TemporalValidityAnnotation t2) {
		DatetimeInterval intersectionInterval = AnnotationUtils.computeIntersection(t1.getValue(), t2.getValue());
		
		TemporalValidityAnnotation t = new TemporalValidityAnnotation(t1);
		t.setValue(intersectionInterval);
		
		return t;
	}
	
	
	public static TemporalValidityAnnotation extend(TemporalValidityAnnotation t1, TemporalValidityAnnotation t2) {
		DatetimeInterval extensionInterval = AnnotationUtils.extendTimeInterval(t1.getValue(), t2.getValue());
		
		TemporalValidityAnnotation t = new TemporalValidityAnnotation(t1);
		t.setValue(extensionInterval);
		
		return t;
	}
	
}
