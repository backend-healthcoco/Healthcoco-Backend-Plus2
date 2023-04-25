package com.dpdocter.beans;

import com.dpdocter.enums.UnitType;

public class Tax {

	private Double value = 0.0;

	private UnitType unit;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public UnitType getUnit() {
		return unit;
	}

	public void setUnit(UnitType unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Tax [value=" + value + ", unit=" + unit + "]";
	}
}
