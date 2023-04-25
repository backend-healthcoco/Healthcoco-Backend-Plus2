package com.dpdocter.request;

public class CityAddRequest {

	private String id;

	private String city;

	private String explanation;

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

	public Boolean getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "CityAddRequest [id=" + id + ", city=" + city + ", explanation=" + explanation + ", isActivated="
				+ isActivated + "]";
	}
}
