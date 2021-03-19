package com.dpdocter.enums;

public enum VitalSignsUnit {
	PULSE("beats/min"), BREATHING("breaths/min"), BLOODPRESSURE("mmHg"), HEIGHT("cm"), TEMPERATURE("°F"), WEIGHT(
			"kg"), SPO2("%"), BSA("m²"),BMI("kg/m²"),FIO2("%"),VENTILATION_MODE("VCV"),CVP("mmHg"),URINE("cc"),IBP("mmHg"),
	BLOODSUGAR("mg/dL");
	
	private String unit;

	public String getUnit() {
		return unit;
	}

	private VitalSignsUnit(String unit) {
		this.unit = unit;
	}
}
