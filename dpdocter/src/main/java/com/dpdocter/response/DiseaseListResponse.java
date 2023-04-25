package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class DiseaseListResponse extends GenericCollection {
	private String id;

	private String disease;

	private String explanation;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	public DiseaseListResponse() {
		// TODO Auto-generated constructor stub
	}

	public DiseaseListResponse(String id, String disease, String explanation, String doctorId, String locationId,
			String hospitalId, Boolean discarded, Date createdTime, Date updatedTime, String createdBy) {
		this.id = id;
		this.disease = disease;
		this.explanation = explanation;
		this.doctorId = doctorId;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
		this.discarded = discarded;
		super.setCreatedTime(createdTime);
		super.setUpdatedTime(updatedTime);
		super.setCreatedBy(createdBy);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "DiseaseListResponse [id=" + id + ", disease=" + disease + ", explanation=" + explanation + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ "]";
	}
}
