package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DiagnosticTest;

@Document(collection = "rate_card_test_association_cl")
public class RateCardTestAssociationCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId diagnosticTestId;
	@Field
	private ObjectId rateCardId;
	@Field
	private Integer turnaroundTime;
	@Field
	private Double cost;
	@Field
	private String category;
	@Field
	private ObjectId labId;
	@Field
	private DiagnosticTest diagnosticTest;
	@Field
	private Boolean isAvailable;
	@Field
	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getDiagnosticTestId() {
		return diagnosticTestId;
	}

	public void setDiagnosticTestId(ObjectId diagnosticTestId) {
		this.diagnosticTestId = diagnosticTestId;
	}

	public ObjectId getRateCardId() {
		return rateCardId;
	}

	public void setRateCardId(ObjectId rateCardId) {
		this.rateCardId = rateCardId;
	}

	public Integer getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(Integer turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ObjectId getLabId() {
		return labId;
	}

	public void setLabId(ObjectId labId) {
		this.labId = labId;
	}

	public DiagnosticTest getDiagnosticTest() {
		return diagnosticTest;
	}

	public void setDiagnosticTest(DiagnosticTest diagnosticTest) {
		this.diagnosticTest = diagnosticTest;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	@Override
	public String toString() {
		return "RateCardTestAssociationCollection [id=" + id + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", diagnosticTestId=" + diagnosticTestId + ", rateCardId=" + rateCardId
				+ ", turnaroundTime=" + turnaroundTime + ", cost=" + cost + ", category=" + category + ", labId="
				+ labId + "]";
	}

}
