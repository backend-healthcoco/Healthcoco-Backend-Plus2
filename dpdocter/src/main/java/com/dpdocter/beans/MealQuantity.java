package com.dpdocter.beans;

import com.dpdocter.enums.QuantityEnum;

public class MealQuantity {

	private double value;
	private QuantityEnum type;

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
