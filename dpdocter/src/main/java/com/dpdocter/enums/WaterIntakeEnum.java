package com.dpdocter.enums;

public enum WaterIntakeEnum {

	GLASS_250ML("GLASS_250ML"), BOTTLE_500ML("BOTTLE_500ML");

	private String type;

	public String getType() {
		return type;
	}

	private WaterIntakeEnum(String type) {
		this.type = type;
	}

}
