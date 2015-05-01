package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "referrences_cl")
public class ReferrencesCollection {
	@Id
	private String id;
	@Field
	private String referrence;
	@Field
	private String description;
	@Field
	private String doctorId;
	@Field
	private String locationId;
	@Field
	private String hospitalId;
	@Field
	private boolean isdeleted;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferrence() {
		return referrence;
	}

	public void setReferrence(String referrence) {
		this.referrence = referrence;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public boolean isIsdeleted() {
		return isdeleted;
	}

	public void setIsdeleted(boolean isdeleted) {
		this.isdeleted = isdeleted;
	}

	@Override
	public String toString() {
		return "ReferrencesCollection [id=" + id + ", referrence=" + referrence + ", description=" + description + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", isdeleted=" + isdeleted + "]";
	}

}
