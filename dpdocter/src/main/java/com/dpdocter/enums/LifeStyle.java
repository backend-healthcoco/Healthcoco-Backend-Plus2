package com.dpdocter.enums;

public enum LifeStyle {
	
	Sedentary("Sedentary"),
	Moderate("Moderate"),
		Heavy("Heavy");
	
	  private String type;

	private LifeStyle(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	  
	

}
