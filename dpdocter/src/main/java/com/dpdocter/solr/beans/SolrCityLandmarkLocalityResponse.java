package com.dpdocter.solr.beans;

public class SolrCityLandmarkLocalityResponse {

	private String id;

    private String city;

    private String locality;

    private String landmark;

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

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	@Override
	public String toString() {
		return "SolrCityLandmarkLocalityResponse [id=" + id + ", city=" + city + ", locality=" + locality
				+ ", landmark=" + landmark + "]";
	}
}
