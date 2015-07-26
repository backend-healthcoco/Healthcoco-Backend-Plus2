package com.dpdocter.beans;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class Strength {
	private String value;

	private DrugStrengthUnit strengthUnit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DrugStrengthUnit getStrengthUnit() {
		return strengthUnit;
	}

	public void setStrengthUnit(DrugStrengthUnit strengthUnit) {
		this.strengthUnit = strengthUnit;
	}

	@Override
	public String toString() {
		return "Strength [value=" + value + ", strengthUnit=" + strengthUnit + "]";
	}

	}
