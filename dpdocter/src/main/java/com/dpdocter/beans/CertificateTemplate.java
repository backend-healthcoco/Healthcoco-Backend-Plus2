package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class CertificateTemplate extends GenericCollection{

	private String id;
	
	private String type;
	
	private String htmlText;
	
	private List<String> specialities;
	
	private List<Fields> inputElements;

	private String doctorId;
	
	private String locationId;
	
	private String hospitalId;
	
	private Boolean discarded = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<Fields> getInputElements() {
		return inputElements;
	}

	public void setInputElements(List<Fields> inputElements) {
		this.inputElements = inputElements;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "CertificateTemplate [id=" + id + ", type=" + type + ", htmlText=" + htmlText + ", specialities="
				+ specialities + ", inputElements=" + inputElements + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
