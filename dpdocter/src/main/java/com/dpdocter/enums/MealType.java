package com.dpdocter.enums;

public enum MealType {
	
	Early_Morning("Early Morning"),
		Breakfast("Breakfast"),
		Mid_Morning("Mid Morning"),
		Lunch("Lunch"),
		Snacks("Snacks"),
		Dinner("Dinner");
	
	  private String type;

	private MealType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	  
	

}
