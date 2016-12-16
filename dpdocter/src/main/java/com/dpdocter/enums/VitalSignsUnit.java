package com.dpdocter.enums;

public enum VitalSignsUnit {
    PULSE("beats/min"), BREATHING("breaths/min"), BLOODPRESSURE("mmHg"), HEIGHT("feet"), TEMPERATURE("°F"), WEIGHT("Kg"), SPO2("%");

    private String unit;

    public String getUnit() {
	return unit;
    }

    private VitalSignsUnit(String unit) {
	this.unit = unit;
    }
}
