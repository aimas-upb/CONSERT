package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/*
 * Class for modeling a discussing  high level activity 
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:DiscussingHLA")
public class DiscussingHLA extends HLA {
    public DiscussingHLA() {
        super(new HLAType("DISCUSSING"));
    }
    
    
    public DiscussingHLA(Person person, AnnotationData annotationData) {
        super(person, new HLAType("DISCUSSING"), annotationData);
    }
}
