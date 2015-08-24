package com.dpdocter.beans;

public class Locality {

    private String id;

    private String cityId;

    private String locality;

    private String description;

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

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "Locality [id=" + id + ", cityId=" + cityId + ", locality=" + locality + ", description=" + description + "]";
    }

}
