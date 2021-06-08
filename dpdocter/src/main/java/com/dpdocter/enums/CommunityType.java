package com.dpdocter.enums;

public enum CommunityType {

POST("POST"),LEARNING_SESSION("LEARNING_SESSION"),ARTICLES("ARTICLES"),FORUM("FORUM");
	
	private String type;

	private CommunityType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
