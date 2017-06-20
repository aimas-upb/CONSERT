package org.aimas.consert.tests.casas.entities;

import org.aimas.consert.model.ContextEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ItemType implements ContextEntity {
	
	private String itemType;

	public ItemType(String itemType) {
	    this.itemType = itemType;
    }

	@Override
    public boolean isLiteral() {
	    return true;
    }

	@Override
	@JsonIgnore
    public Object getValue() {
	    return itemType;
    }
	
	
}
