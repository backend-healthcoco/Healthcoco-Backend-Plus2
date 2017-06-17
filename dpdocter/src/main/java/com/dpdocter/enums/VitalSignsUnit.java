package com.dpdocter.enums;

public enum VitalSignsUnit {
	PULSE("beats/min"), BREATHING("breaths/min"), BLOODPRESSURE("mmHg"), HEIGHT("cm"), TEMPERATURE("°F"), WEIGHT(
			"Kg"), SPO2("%"), BSA("m²"),BMI("kg/m²");
	
	private String unit;

	public String getUnit() {
		return unit;
	}

	private VitalSignsUnit(String unit) {
		this.unit = unit;
	}
}
