package com.dpdocter.beans;

import com.dpdocter.enums.QuantityEnum;

public class EquivalentQuantities {
	private QuantityEnum servingType;
	private double value;
	private QuantityEnum type;

	public QuantityEnum getServingType() {
		return servingType;
	}

	public void setServingType(QuantityEnum servingType) {
		this.servingType = servingType;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public QuantityEnum getType() {
		return type;
	}

	public void setType(QuantityEnum type) {
		this.type = type;
	}

}
