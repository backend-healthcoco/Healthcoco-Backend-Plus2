package com.dpdocter.beans;

public class TreatmentFields {

	private String key;
	
	private String value;

	@Override
	public String toString() {
		return "TreatmentFields [key=" + key + ", value=" + value + "]";
	}

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
	
}
