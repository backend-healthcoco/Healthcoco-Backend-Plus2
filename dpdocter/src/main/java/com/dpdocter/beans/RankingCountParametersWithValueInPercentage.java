package com.dpdocter.beans;

import com.dpdocter.enums.RankingCountParatmeter;

public class RankingCountParametersWithValueInPercentage {

	private RankingCountParatmeter type;

	private double value = 0.0;

	public RankingCountParametersWithValueInPercentage(RankingCountParatmeter type, double value) {
		super();
		this.type = type;
		this.value = value;
	}

	public RankingCountParatmeter getType() {
		return type;
	}

	public void setType(RankingCountParatmeter type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "RankingCountParametersWithValueInPercentage [type=" + type + ", value=" + value + "]";
	}
}
