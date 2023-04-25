package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_otp_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'doctorId' : 1, 'locationId' : 1, 'patientId': 1}") })
public class DoctorOTPCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId otpId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId patientId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getOtpId() {
		return otpId;
	}

	public void setOtpId(ObjectId otpId) {
		this.otpId = otpId;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	@Override
	public String toString() {
		return "DoctorOTPCollection [id=" + id + ", otpId=" + otpId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", patientId=" + patientId + "]";
	}

}
