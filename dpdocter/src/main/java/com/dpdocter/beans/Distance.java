package com.dpdocter.beans;

import com.dpdocter.enums.DistanceEnum;

public class Distance {

	private double value;

	private DistanceEnum type;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public DistanceEnum getType() {
		return type;
	}

	public void setType(DistanceEnum type) {
		this.type = type;
	}

}
