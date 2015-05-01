package com.dpdocter.beans;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import com.dpdocter.enums.StrengthUnitEnum;

@JsonAutoDetect
public class Strength {

	private String value;

	private StrengthUnitEnum unit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public StrengthUnitEnum getUnit() {
		return unit;
	}

	public void setUnit(StrengthUnitEnum unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Strength [value=" + value + ", unit=" + unit + "]";
	}

}
