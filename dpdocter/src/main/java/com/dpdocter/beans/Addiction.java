package com.dpdocter.beans;

import com.dpdocter.enums.AddictionType;

public class Addiction {

	private AddictionType type;

	private String alcoholType;

	private Integer noOfTime = 0;

	private Integer quantiry;

	public AddictionType getType() {
		return type;
	}

	public void setType(AddictionType type) {
		this.type = type;
	}

	public String getAlcoholType() {
		return alcoholType;
	}

	public void setAlcoholType(String alcoholType) {
		this.alcoholType = alcoholType;
	}

	public Integer getNoOfTime() {
		return noOfTime;
	}

	public void setNoOfTime(Integer noOfTime) {
		this.noOfTime = noOfTime;
	}

	public Integer getQuantiry() {
		return quantiry;
	}

	public void setQuantiry(Integer quantiry) {
		this.quantiry = quantiry;
	}

}
