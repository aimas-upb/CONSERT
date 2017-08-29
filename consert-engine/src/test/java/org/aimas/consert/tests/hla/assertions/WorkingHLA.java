package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/*
 * Class for modeling a working  high level activity
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:WorkingHLA")
public class WorkingHLA extends HLA {
    public WorkingHLA() {
        super(new HLAType("WORKING"));
    }

    public WorkingHLA(Person person, AnnotationData annotationData) {
        super(person, new HLAType("WORKING"), annotationData);
    }

}
