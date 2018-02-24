package com.dpdocter.enums;

public enum MedicationEffectType {

	EXCELLENT("EXCELLENT"), GOOD("GOOD"), OK("OK"), BAD("BAD"), WORSE("WORSE"),
	GREAT("GREAT"), BETTER("BETTER"), NOT_WELL("NOT_WELL"), SICK("SICK");

	private String type;

	public String getType() {
		return type;
	}

	private MedicationEffectType(String type) {
		this.type = type;
	}

}
