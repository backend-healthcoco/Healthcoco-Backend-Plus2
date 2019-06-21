package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DiagnosticTest;

public class LabSearchResponse {

	private String id;
	
	private String hospitalId;
	
	private String locationName;
	
	private Boolean isNABLAccredited = false;

	List<DiagnosticTest> diagnosticTests;
	
	private Double totalCost = 0.0;

    private Double totalCostForPatient = 0.0;

    private Double totalSavingInPercentage = 0.0;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Boolean getIsNABLAccredited() {
		return isNABLAccredited;
	}

	public void setIsNABLAccredited(Boolean isNABLAccredited) {
		this.isNABLAccredited = isNABLAccredited;
	}

	public List<DiagnosticTest> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<DiagnosticTest> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Double getTotalCostForPatient() {
		return totalCostForPatient;
	}

	public void setTotalCostForPatient(Double totalCostForPatient) {
		this.totalCostForPatient = totalCostForPatient;
	}

	public Double getTotalSavingInPercentage() {
		return totalSavingInPercentage;
	}

	public void setTotalSavingInPercentage(Double totalSavingInPercentage) {
		this.totalSavingInPercentage = totalSavingInPercentage;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "LabSearchResponse [id=" + id + ", hospitalId=" + hospitalId + ", locationName=" + locationName
				+ ", isNABLAccredited=" + isNABLAccredited + ", diagnosticTests=" + diagnosticTests + ", totalCost="
				+ totalCost + ", totalCostForPatient=" + totalCostForPatient + ", totalSavingInPercentage="
				+ totalSavingInPercentage + "]";
	}
}
