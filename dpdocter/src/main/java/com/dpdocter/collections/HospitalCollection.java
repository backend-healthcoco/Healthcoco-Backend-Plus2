package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "hospital_cl")
public class HospitalCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String hospitalName;

	@Field
	private String hospitalPhoneNumber;

	@Field
	private String hospitalImageUrl;

	@Field
	private String hospitalDescription;

	@Field
	private String hospitalUId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getHospitalPhoneNumber() {
		return hospitalPhoneNumber;
	}

	public void setHospitalPhoneNumber(String hospitalPhoneNumber) {
		this.hospitalPhoneNumber = hospitalPhoneNumber;
	}

	public String getHospitalImageUrl() {
		return hospitalImageUrl;
	}

	public void setHospitalImageUrl(String hospitalImageUrl) {
		this.hospitalImageUrl = hospitalImageUrl;
	}

	public String getHospitalDescription() {
		return hospitalDescription;
	}

	public void setHospitalDescription(String hospitalDescription) {
		this.hospitalDescription = hospitalDescription;
	}

	public String getHospitalUId() {
		return hospitalUId;
	}

	public void setHospitalUId(String hospitalUId) {
		this.hospitalUId = hospitalUId;
	}

	@Override
	public String toString() {
		return "HospitalCollection [id=" + id + ", hospitalName=" + hospitalName + ", hospitalPhoneNumber="
				+ hospitalPhoneNumber + ", hospitalImageUrl=" + hospitalImageUrl + ", hospitalDescription="
				+ hospitalDescription + ", hospitalUId=" + hospitalUId + "]";
	}
}
