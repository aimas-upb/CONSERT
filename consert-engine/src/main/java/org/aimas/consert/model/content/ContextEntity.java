package org.aimas.consert.model.content;

import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({
    "hlatest = http://example.org/hlatest/" 
})
@RDFBean("hlatest:ContextEntity")
public interface ContextEntity {
	
	String getEntityId();
	
	boolean isLiteral();
	
	Object getValue();
}
