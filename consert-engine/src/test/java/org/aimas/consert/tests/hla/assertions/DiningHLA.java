package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;


/*
 *   Class for modeling a dining  high level activity
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:DiningHLA")
public class DiningHLA extends HLA {

    public DiningHLA() {
        super(new HLAType("DINING"));
    }

    public DiningHLA(Person person, AnnotationData annotationData) {
        super(person, new HLAType("DINING"), annotationData);
    }
}
