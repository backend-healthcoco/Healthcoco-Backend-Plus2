package com.dpdocter.enums;

public enum CommunityDignosed {

POST("POST"),LEARNING_SESSION("LEARNING_SESSION"),ARTICLES("ARTICLES"),FORUM("FORUM");
	
	private String type;

	private CommunityDignosed(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
