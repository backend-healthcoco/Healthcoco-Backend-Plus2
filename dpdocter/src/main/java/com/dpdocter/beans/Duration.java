package com.dpdocter.beans;

import com.dpdocter.enums.DurationUnitEnum;

public class Duration {
	private String value;
	private DurationUnitEnum unit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DurationUnitEnum getUnit() {
		return unit;
	}

	public void setUnit(DurationUnitEnum unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Duration [value=" + value + ", unit=" + unit + "]";
	}

}
