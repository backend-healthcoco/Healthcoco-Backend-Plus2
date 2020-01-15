package com.dpdocter.enums;

public enum QuantityEnum {
	DAYS("DAYS"), QTY("QTY"), KG("KG"), G("G"), MG("MG"), UG("UG"), IU("IU"), TABLE_SPOON("TABLE_SPOON"),
	TEA_SPOON("TEA_SPOON"), PERCENT("PERCENT"), CUP("CUP"), BOWL("BOWL"), BOWL_MEDIUM("BOWL_MEDIUM"),
	BOWL_LARGE("BOWL_LARGE"), KATORI("KATORI"), KOTORI_MEDIUM("KATORI_MEDIUM"), KOTORI_LARGE("KOTORI_LARGE"),
	PLATE("PLATE"), PLATE_MEDIUM("PLATE_MEDIUM"), PLATE_LARGE("PLATE_LARGE"), LITRE("LITRE"), MILLI_LITRE("MILLI_LITRE"),
	GLASS("GLASS"), CAL("CAL"), KCAL("KCAL"), UNIT("UNIT"), PINCH("PINCH"), ACCORDING_TO_TASTE("ACCORDING_TO_TASTE"),
	SMALL_QTY("SMALL_QTY"), MEDIUM_QTY("MEDIUM_QTY"), LARGE_QTY("LARGE_QTY"),
	MILI_LITRE("MILI_LITRE");

	private String duration;

	public String getDuration() {
		return duration;
	}

	private QuantityEnum(String duration) {
		this.duration = duration;
	}

}
