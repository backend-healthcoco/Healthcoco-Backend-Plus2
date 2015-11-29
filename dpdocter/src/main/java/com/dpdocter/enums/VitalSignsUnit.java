package com.dpdocter.enums;

public enum VitalSignsUnit {
    PULSE("beats/min"), BREATHING("breaths/min"), BLOODPRESSURE("mmHg");

    private String unit;

    public String getUnit() {
	return unit;
    }

    private VitalSignsUnit(String unit) {
	this.unit = unit;
    }
}
