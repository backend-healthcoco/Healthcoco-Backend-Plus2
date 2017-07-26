package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "oral_cavity_and_throat_examination_cl")
public class OralCavityAndThroatExaminationCollection extends GenericCollection {

	private ObjectId id;

	private String oralCavityExam;

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

	public String getOralCavityExam() {
		return oralCavityExam;
	}

	public void setOralCavityExam(String oralCavityExam) {
		this.oralCavityExam = oralCavityExam;
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
		return "OralCavityAndThroatExaminationCollection [id=" + id + ", oralCavityExam=" + oralCavityExam
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + ", speciality=" + speciality + "]";
	}

}
