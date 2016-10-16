package com.dpdocter.beans;

import com.dpdocter.enums.UnitType;

public class Discount {
	private double value;
	private UnitType unit;
	
	public UnitType getUnit() {
		return unit;
	}
	public void setUnit(UnitType unit) {
		this.unit = unit;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	

}
