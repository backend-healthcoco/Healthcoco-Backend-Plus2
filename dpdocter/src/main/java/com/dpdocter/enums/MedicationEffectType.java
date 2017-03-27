package com.dpdocter.enums;

public enum MedicationEffectType {
	EXCELLENT("EXCELLENT"), GOOD("GOOD"), OK("OK"), BAD("BAD"), WORSE("WORSE");

	private String type;

	public String getType() {
		return type;
	}

	private MedicationEffectType(String type) {
		this.type = type;
	}

}
