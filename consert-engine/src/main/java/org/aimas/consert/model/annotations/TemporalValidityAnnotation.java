package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.eclipse.rdf4j.query.algebra.Str;

@RDFNamespaces("annotation = " + Constants.ANNOTATION_NS)
@RDFBean("annotation:TemporalValidityAnnotation")
public class TemporalValidityAnnotation extends StructuredAnnotation {
	
	DatetimeInterval value;
	private String continuityFunction;
	private String extensionOperator;
	private String combinationOperator;
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 10000;		// in ms

	public TemporalValidityAnnotation() { }

	public boolean allowsContinuity(StructuredAnnotation other) {
		return ((TemporalValidityAnnotation)other).getValue().getStart().getTime() - getValue().getEnd().getTime() < TIMESTAMP_DIFF_THRESHOLD;
	}

	public TemporalValidityAnnotation(DatetimeInterval value,
            String continuityFunction, String extensionOperator,
            String combinationOperator) {
	    this.value = value;
	    this.continuityFunction = continuityFunction;
	    this.extensionOperator = extensionOperator;
	    this.combinationOperator = combinationOperator;
    }
	
	
	@Override
	@RDF("annotation:hasValue")
    public DatetimeInterval getValue() {
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
	
}
