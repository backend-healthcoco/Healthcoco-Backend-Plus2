package com.dpdocter.beans;

import java.util.List;

/**
 * @author veeraj
 */
public class Hospital {
	private String id;
	private String name;
	private String phoneNumber;
	private String imageUrl;
	private String description;
	private List<Locations> locations;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Locations> getLocations() {
		return locations;
	}
	public void setLocations(List<Locations> locations) {
		this.locations = locations;
	}
	@Override
	public String toString() {
		return "Hospital [id=" + id + ", name=" + name + ", phoneNumber="
				+ phoneNumber + ", imageUrl=" + imageUrl + ", description="
				+ description + ", locations=" + locations + "]";
	}
	
	
	
}
