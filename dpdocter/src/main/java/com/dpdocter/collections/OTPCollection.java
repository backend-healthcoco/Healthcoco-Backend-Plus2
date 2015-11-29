package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "otp_cl")
public class OTPCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String otpNumber;

    @Field
    private Boolean isVerified = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getOtpNumber() {
	return otpNumber;
    }

    public void setOtpNumber(String otpNumber) {
	this.otpNumber = otpNumber;
    }

    public Boolean getIsVerified() {
	return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
	this.isVerified = isVerified;
    }

    @Override
    public String toString() {
	return "OTPCollection [id=" + id + ", otpNumber=" + otpNumber + ", isVerified=" + isVerified + "]";
    }
}
