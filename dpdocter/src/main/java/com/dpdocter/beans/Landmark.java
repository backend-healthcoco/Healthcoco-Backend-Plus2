package com.dpdocter.beans;

public class Landmark {

    private String id;

    private String cityId;

    private String landmark;

    private String explanation;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCityId() {
	return cityId;
    }

    public void setCityId(String cityId) {
	this.cityId = cityId;
    }

    public String getLandmark() {
	return landmark;
    }

    public void setLandmark(String landmark) {
	this.landmark = landmark;
    }

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "Landmark [id=" + id + ", cityId=" + cityId + ", landmark=" + landmark + ", explanation=" + explanation
				+ "]";
	}
}
