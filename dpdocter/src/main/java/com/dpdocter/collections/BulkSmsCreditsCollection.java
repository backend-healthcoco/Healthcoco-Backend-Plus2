package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "bulk_sms_credit_cl")
public class BulkSmsCreditsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private long creditBalance;
	@Field
	private long creditSpent;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	
	public long getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(long creditBalance) {
		this.creditBalance = creditBalance;
	}

	public long getCreditSpent() {
		return creditSpent;
	}

	public void setCreditSpent(long creditSpent) {
		this.creditSpent = creditSpent;
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
	
	
}
