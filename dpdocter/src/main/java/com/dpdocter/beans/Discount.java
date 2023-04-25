package com.dpdocter.beans;

import com.dpdocter.enums.UnitType;

public class Discount {
	private Double value = 0.0;
	private UnitType unit = UnitType.INR;

	public UnitType getUnit() {
		return unit;
	}

	public void setUnit(UnitType unit) {
		this.unit = unit;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Discount [value=" + value + ", unit=" + unit + "]";
	}

}
