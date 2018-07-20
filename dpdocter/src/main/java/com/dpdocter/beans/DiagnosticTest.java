package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DiagnosticTest extends GenericCollection {

	private String id;

	private String testName;

	private String explanation;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String specimen;

	private String diagnosticTestCode;

	private Double diagnosticTestCost = 0.0;

	private Amount diagnosticTestComission;

	private Double diagnosticTestCostForPatient = 0.0;

	private long rankingCount = 0;

	private long count = 0;

	private Integer turnaroundTime;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

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

	public Integer getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(Integer turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	@Override
	public String toString() {
		return "DiagnosticTest [id=" + id + ", testName=" + testName + ", explanation=" + explanation + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", specimen=" + specimen
				+ ", diagnosticTestCode=" + diagnosticTestCode + ", diagnosticTestCost=" + diagnosticTestCost
				+ ", diagnosticTestComission=" + diagnosticTestComission + ", diagnosticTestCostForPatient="
				+ diagnosticTestCostForPatient + ", rankingCount=" + rankingCount + ", turnaroundTime=" + turnaroundTime
				+ "]";
	}
}
