package com.dpdocter.enums;

public enum NmcHcmType {

	IMA("IMA"),NMC("NMC");
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private NmcHcmType(String type) {
		this.type = type;
	}
	
	
}
