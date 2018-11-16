package com.dpdocter.beans;

import com.dpdocter.enums.AddictionType;
import com.dpdocter.enums.SearchType;

public class Addiction {

	private AddictionType type;

	private String alcoholType;

	private SearchType consumeTime;

	private Integer noOfTime = 0;

	private MealQuantity quantity;

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

	public SearchType getConsumeTime() {
		return consumeTime;
	}

	public void setConsumeTime(SearchType consumeTime) {
		this.consumeTime = consumeTime;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

}
