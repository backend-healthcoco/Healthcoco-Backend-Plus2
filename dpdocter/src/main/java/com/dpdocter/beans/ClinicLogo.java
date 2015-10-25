package com.dpdocter.beans;

public class ClinicLogo {

    private String id;

    private String logoURL;
    
    private String logoThumbnailURL;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getLogoURL() {
	return logoURL;
    }

    public void setLogoURL(String logoURL) {
	this.logoURL = logoURL;
    }

	public String getLogoThumbnailURL() {
		return logoThumbnailURL;
	}

	public void setLogoThumbnailURL(String logoThumbnailURL) {
		this.logoThumbnailURL = logoThumbnailURL;
	}

	@Override
	public String toString() {
		return "ClinicLogo [id=" + id + ", logoURL=" + logoURL + ", logoThumbnailURL=" + logoThumbnailURL + "]";
	}
}