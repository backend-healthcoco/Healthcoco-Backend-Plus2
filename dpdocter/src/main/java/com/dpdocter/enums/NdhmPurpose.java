package com.dpdocter.enums;

public enum NdhmPurpose {
	LINK("LINK"), KYC("KYC"), KYC_AND_LINK("KYC_AND_LINK");
	
	private String type;

	public String getType() {
		return type;
	}

	private NdhmPurpose(String type) {
		this.type = type;
	}
	
	
}
