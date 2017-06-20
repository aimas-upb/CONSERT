package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.NumericLiteral;
import org.aimas.consert.tests.casas.entities.WaterType;

public class Water extends BinaryContextAssertion {
	
	WaterType waterType;
	double value;
	
	public Water() {}
	
	public Water(WaterType waterType, double value, AnnotationData annotationData) {
	    super(waterType, new NumericLiteral(value), AcquisitionType.SENSED, annotationData);
		
	    this.waterType = waterType;
	    this.value = value;
    }



	public WaterType getWaterType() {
		return waterType;
	}

	public void setWaterType(WaterType waterType) {
		this.waterType = waterType;
		setSubject(waterType);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		setObject(new NumericLiteral(value));		
	}
	
	
}
