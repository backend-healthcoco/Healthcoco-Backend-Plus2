package com.dpdocter.beans;

public class FoodPattern {

	private String type;
	private String food;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFood() {
		return food;
	}

	public void setFood(String food) {
		this.food = food;
	}

	@Override
	public String toString() {
		return "FoodPattern [type=" + type + ", food=" + food + "]";
	}

}
