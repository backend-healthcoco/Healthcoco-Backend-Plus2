package com.dpdocter.enums;

public enum QuantityEnum {
	DAYS("DAYS"), QTY("QTY"), KG("KG"), GM("GM"), MGM("MGM"), TABLE_SPOON("TABLE_SPOON"), TEA_SPOON("TEA_SPOON"),
	PERCENT("PERCENT"), CUP("CUP"), BOWL("BOWL");
	private String duration;

	public String getDuration() {
		return duration;
	}

	private QuantityEnum(String duration) {
		this.duration = duration;
	}

}
