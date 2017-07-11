package com.dpdocter.enums;

public enum PharmacyTypeEnum {
	ALLOPATHIC("ALLOPATHIC"), HOMEOPATHIC("HOMEOPATHIC"), AYURVEDIC("AYURVEDIC");

	private String type;

	public String getType() {
		return type;
	}

	private PharmacyTypeEnum(String type) {
		this.type = type;
	}

}
