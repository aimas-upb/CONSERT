package org.aimas.consert.model.content;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AssertionRole {
	public static final String ENTITY = "entity";
	public static final String SUBJECT = "subject";
	public static final String OBJECT = "object";
	
	// Possible values of the assertion role are: entity (for unary and nary ContextAssertions), subject, object for binary assertions
	String value() default "entity" ;
}
