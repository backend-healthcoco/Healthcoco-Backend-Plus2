package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class CollectionBoyDentalLabAssociation extends GenericCollection {

	private String id;
	private String collectionBoyId;
	private String dentalLabId;
	private String doctorId;
	private Boolean isActive = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(String dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "CollectionBoyDentalLabAssociation [id=" + id + ", collectionBoyId=" + collectionBoyId + ", dentalLabId="
				+ dentalLabId + ", doctorId=" + doctorId + ", isActive=" + isActive + "]";
	}

}
