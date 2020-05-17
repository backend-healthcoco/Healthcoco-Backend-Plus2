package com.dpdocter.enums;

public enum ConsultationType {

	CHAT("CHAT"), AUDIO("AUDIO"),VIDEO("VIDEO");
	
	private String type;

	private ConsultationType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}
