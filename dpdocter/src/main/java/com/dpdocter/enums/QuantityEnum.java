package com.dpdocter.enums;

public enum QuantityEnum {
	DAYS("DAYS"), QTY("QTY"), KG("KG"), G("G"), MG("MG"), UG("UG"), IG("IG"), TABLE_SPOON("TABLE_SPOON"),
	TEA_SPOON("TEA_SPOON"), PERCENT("PERCENT"), CUP("CUP"), BOWL("BOWL"), LITRE("LITRE"), MILI_LITRE("MILI_LITRE"),
	GLASS("GLASS"), CAL("CAL"), KCAL("KCAL");

	private String duration;

	public String getDuration() {
		return duration;
	}

	private QuantityEnum(String duration) {
		this.duration = duration;
	}

}
