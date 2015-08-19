package com.dpdocter.beans;

public class City {

	private String id;

    private String city;

    private String description;

    private Boolean isActivated = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", city=" + city + ", description=" + description + ", isActivated=" + isActivated
				+ "]";
	}

}
