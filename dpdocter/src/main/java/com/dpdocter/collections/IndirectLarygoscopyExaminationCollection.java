package com.dpdocter.collections;

import org.bson.types.ObjectId;

public class IndirectLarygoscopyExaminationCollection extends GenericCollection {

	private ObjectId id;

	private String indirectLarygoscopyExam;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private Boolean discarded = false;

	private String speciality;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getIndirectLarygoscopyExam() {
		return indirectLarygoscopyExam;
	}

	public void setIndirectLarygoscopyExam(String indirectLarygoscopyExam) {
		this.indirectLarygoscopyExam = indirectLarygoscopyExam;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	@Override
	public String toString() {
		return "IndirectLarygoscopyExaminationCollection [id=" + id + ", indirectLarygoscopyExam="
				+ indirectLarygoscopyExam + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", discarded=" + discarded + ", speciality=" + speciality + "]";
	}

}
