package com.dpdocter.beans;

public class AddEditSEORequest {

	private String doctorId;

	private String metaTitle;

	private String metaDesccription;

	private String metaKeyword;

	private String slugUrl;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getMetaDesccription() {
		return metaDesccription;
	}

	public void setMetaDesccription(String metaDesccription) {
		this.metaDesccription = metaDesccription;
	}

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public String getSlugUrl() {
		return slugUrl;
	}

	public void setSlugUrl(String slugUrl) {
		this.slugUrl = slugUrl;
	}

	@Override
	public String toString() {
		return "AddEditSEORequest [doctorId=" + doctorId + ", metaTitle=" + metaTitle + ", metaDesccription="
				+ metaDesccription + ", metaKeyword=" + metaKeyword + ", slugUrl=" + slugUrl + "]";
	}

}
