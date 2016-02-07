package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_otp_cl")
public class DoctorOTPCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String otpId;

    @Field
    private String userLocationId;

    @Field
    private String patientId;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getOtpId() {
	return otpId;
    }

    public void setOtpId(String otpId) {
	this.otpId = otpId;
    }

    public String getUserLocationId() {
	return userLocationId;
    }

    public void setUserLocationId(String userLocationId) {
	this.userLocationId = userLocationId;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    @Override
    public String toString() {
	return "DoctorOTPCollection [id=" + id + ", otpId=" + otpId + ", userLocationId=" + userLocationId + ", patientId=" + patientId + "]";
    }
}
