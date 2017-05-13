package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order_drug_cl")
public class OrderDrugCollection extends GenericCollection {

	private ObjectId id;

	private ObjectId localeId;

	private ObjectId patientId;

	private String uniqueRequestId;

	private String uniqueResponseId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getLocaleId() {
		return localeId;
	}

	public void setLocaleId(ObjectId localeId) {
		this.localeId = localeId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	@Override
	public String toString() {
		return "OrderDrugCollection [id=" + id + ", localeId=" + localeId + ", patientId=" + patientId
				+ ", uniqueRequestId=" + uniqueRequestId + ", uniqueResponseId=" + uniqueResponseId + "]";
	}

}
