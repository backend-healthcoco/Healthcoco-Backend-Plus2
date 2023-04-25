package com.dpdocter.beans;

import com.dpdocter.enums.QuantityEnum;

public class Quantity {
	private int value;
	private QuantityEnum type;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public QuantityEnum getType() {
		return type;
	}

	public void setType(QuantityEnum type) {
		this.type = type;
	}

}
