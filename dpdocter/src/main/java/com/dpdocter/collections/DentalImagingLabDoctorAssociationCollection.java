package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DentalImagingLabDoctorAssociationCollection extends GenericCollection{

	@Id
	private String id;
	@Field
	private String doctorId;
	@Field
	private String dentalImagingLocationId;
	@Field
	private String dentalImagingHospitalId;
	@Field
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDentalImagingLocationId() {
		return dentalImagingLocationId;
	}

	public void setDentalImagingLocationId(String dentalImagingLocationId) {
		this.dentalImagingLocationId = dentalImagingLocationId;
	}

	public String getDentalImagingHospitalId() {
		return dentalImagingHospitalId;
	}

	public void setDentalImagingHospitalId(String dentalImagingHospitalId) {
		this.dentalImagingHospitalId = dentalImagingHospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
