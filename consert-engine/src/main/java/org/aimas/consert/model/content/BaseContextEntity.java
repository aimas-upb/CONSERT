package org.aimas.consert.model.content;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseContextEntity implements ContextEntity {
	
	protected String id;
	protected boolean isLiteral;
	protected Object value;
	
	public BaseContextEntity() {}
	
	public BaseContextEntity(String id, boolean isLiteral, Object value) {
		this.id = id;
		this.isLiteral = isLiteral;
		this.value = value;
	}
	
	@Override
	public String getEntityId() {
		return id;
	}
	
	public void setEntityId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean isLiteral() {
		return isLiteral;
	}

	public void setLiteral(boolean isLiteral) {
		this.isLiteral = isLiteral;
	}
	
	@Override
	@JsonIgnore
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}	
	
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((value == null) ? 0 : value.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    StringLiteral other = (StringLiteral) obj;
	    if (value == null) {
		    if (other.value != null)
			    return false;
	    }
	    else if (!value.equals(other.value))
		    return false;
	    return true;
    }
	
	
	public abstract Object parseValueFromString(String serializedValue);
	
	public abstract String serializeValue();
}
