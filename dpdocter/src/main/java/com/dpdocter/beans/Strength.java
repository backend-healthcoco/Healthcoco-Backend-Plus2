package com.dpdocter.beans;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class Strength {
	private String value;

	private DrugStrengthUnit unit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DrugStrengthUnit getUnit() {
		return unit;
	}

	public void setUnit(DrugStrengthUnit unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Strength [value=" + value + ", unit=" + unit + "]";
	}

}
