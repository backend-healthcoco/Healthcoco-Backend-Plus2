package com.dpdocter.beans;


public class Tags {
	private String id;
	private String tag;
	private String description;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "Tags [id=" + id + ", tag=" + tag + ", description="
				+ description + "]";
	}
	
	
}
