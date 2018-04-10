package com.dpdocter.enums;

public enum SleepWhen {
	
	DAY("DAY"),NIGHT("NIGHT");
	
	  private String type;

	private SleepWhen(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	  
	

}
