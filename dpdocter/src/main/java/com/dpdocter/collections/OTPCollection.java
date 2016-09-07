package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.OTPState;

@Document(collection = "otp_cl")
public class OTPCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String otpNumber;

	@Field
	private String countryCode;

	@Field
	private String mobileNumber;

	@Field
	private String generatorId;

	@Indexed
	private OTPState state = OTPState.NOTVERIFIED;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getOtpNumber() {
		return otpNumber;
	}

	public void setOtpNumber(String otpNumber) {
		this.otpNumber = otpNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public OTPState getState() {
		return state;
	}

	public void setState(OTPState state) {
		this.state = state;
	}

	public String getGeneratorId() {
		return generatorId;
	}

	public void setGeneratorId(String generatorId) {
		this.generatorId = generatorId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "OTPCollection [id=" + id + ", otpNumber=" + otpNumber + ", mobileNumber=" + mobileNumber
				+ ", generatorId=" + generatorId + ", state=" + state + "]";
	}

}
