package com.dpdocter.beans;

import com.dpdocter.enums.UnitType;

public class Tax {

	private double value;
	
	private UnitType unit;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
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
