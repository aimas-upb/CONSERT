package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.tests.casas.entities.ItemStatus;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class Item extends BinaryContextAssertion {
	
	String itemName;
	ItemStatus itemStatus;
	
	public Item() {}
	
	public Item(String itemName, ItemStatus itemStatus, AnnotationData annotations) {
		super(new StringLiteral(itemName), itemStatus, AcquisitionType.SENSED, annotations);
	
		this.itemName = itemName;
		this.itemStatus = itemStatus;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
		setSubject(new StringLiteral(itemName));
	}

	public ItemStatus getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(ItemStatus itemStatus) {
		this.itemStatus = itemStatus;
		setObject(itemStatus);
	}
}
