package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Amount;

@Document(collection = "diagnostic_test_cl")
public class DiagnosticTestCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String testName;

	@Field
	private String explanation;

	@Field
	private String specimen;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private Boolean discarded = false;

	@Field
	private String diagnosticTestCode;

    @Field
    private Double diagnosticTestCost = 0.0;

    @Field
    private Amount diagnosticTestComission;

    @Field
    private Double diagnosticTestCostForPatient = 0.0;

    @Field
    private long rankingCount = 0;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	public String getDiagnosticTestCode() {
		return diagnosticTestCode;
	}

	public void setDiagnosticTestCode(String diagnosticTestCode) {
		this.diagnosticTestCode = diagnosticTestCode;
	}

	public Double getDiagnosticTestCost() {
		return diagnosticTestCost;
	}

	public void setDiagnosticTestCost(Double diagnosticTestCost) {
		this.diagnosticTestCost = diagnosticTestCost;
	}

	public Amount getDiagnosticTestComission() {
		return diagnosticTestComission;
	}

	public void setDiagnosticTestComission(Amount diagnosticTestComission) {
		this.diagnosticTestComission = diagnosticTestComission;
	}

	public Double getDiagnosticTestCostForPatient() {
		return diagnosticTestCostForPatient;
	}

	public void setDiagnosticTestCostForPatient(Double diagnosticTestCostForPatient) {
		this.diagnosticTestCostForPatient = diagnosticTestCostForPatient;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	@Override
	public String toString() {
		return "DiagnosticTestCollection [id=" + id + ", testName=" + testName + ", explanation=" + explanation
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", specimen=" + specimen + ", diagnosticTestCode=" + diagnosticTestCode + ", diagnosticTestCost="
				+ diagnosticTestCost + ", diagnosticTestComission=" + diagnosticTestComission
				+ ", diagnosticTestCostForPatient=" + diagnosticTestCostForPatient + ", rankingCount=" + rankingCount
				+ "]";
	}
}
