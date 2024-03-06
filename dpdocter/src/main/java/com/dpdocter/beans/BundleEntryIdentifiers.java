package com.dpdocter.beans;

public class BundleEntryIdentifiers {

	private BundlePatientIdentifier type;
	
	private String system;
	
	private String value;

	public BundlePatientIdentifier getType() {
		return type;
	}

	public void setType(BundlePatientIdentifier type) {
		this.type = type;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
