package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.Amount;

@Document(indexName = "diagnostictests_in", type = "diagnostictests")
public class ESDiagnosticTestDocument {

	@Id
	private String id;

	@Field(type = FieldType.String)
	private String testName;

	@Field(type = FieldType.String)
	private String explanation;

	@Field(type = FieldType.String)
	private String locationId;

	@Field(type = FieldType.String)
	private String hospitalId;

	@Field(type = FieldType.String)
	private String specimen;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.String)
	private String diagnosticTestCode;

	@Field(type = FieldType.Double)
	private Double diagnosticTestCost = 0.0;

	@Field(type = FieldType.Nested)
	private Amount diagnosticTestComission;

	@Field(type = FieldType.Double)
	private Double diagnosticTestCostForPatient = 0.0;

    @Field(type = FieldType.Long)
    private long rankingCount = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
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
		return "ESDiagnosticTestDocument [id=" + id + ", testName=" + testName + ", explanation=" + explanation
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", updatedTime=" + updatedTime + ", specimen=" + specimen + ", diagnosticTestCode="
				+ diagnosticTestCode + ", diagnosticTestCost=" + diagnosticTestCost + ", diagnosticTestComission="
				+ diagnosticTestComission + ", diagnosticTestCostForPatient=" + diagnosticTestCostForPatient
				+ ", rankingCount=" + rankingCount + "]";
	}
}
