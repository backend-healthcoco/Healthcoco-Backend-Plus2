package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class OPDReportsCollection extends GenericCollection{
	
	@Id
	private ObjectId id;
	@Field
	private String serialNo;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId prescriptionId;
	@Field
	private String amountReceived;
	@Field
	private String receiptNo;
	@Field
	private String receiptDate;
	@Field
	private String remarks;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

}
