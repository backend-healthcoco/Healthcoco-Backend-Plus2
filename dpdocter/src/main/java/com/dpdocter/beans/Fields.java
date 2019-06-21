package com.dpdocter.beans;

public class Fields {

	private String key;
	
	private String value;

	private String type;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Fields [key=" + key + ", value=" + value + ", type=" + type + "]";
	}	
}
