package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DiagnosticTestPackage extends GenericCollection {

	private String id;

	private String locationName;
	
	private Boolean isNABLAccredited = false;

	private String packageName;

	private String explanation;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	List<String> testIds;
	
	List<String> testNames;

    private Double diagnosticTestPackageCost = 0.0;

    private Amount diagnosticTestPackageComission;

    private Double diagnosticTestCostPackageForPatient = 0.0;

    private Double totalSavingInPercentage = 0.0;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
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

	public List<String> getTestIds() {
		return testIds;
	}

	public void setTestIds(List<String> testIds) {
		this.testIds = testIds;
	}

	public List<String> getTestNames() {
		return testNames;
	}

	public void setTestNames(List<String> testNames) {
		this.testNames = testNames;
	}

	public Double getDiagnosticTestPackageCost() {
		return diagnosticTestPackageCost;
	}

	public void setDiagnosticTestPackageCost(Double diagnosticTestPackageCost) {
		this.diagnosticTestPackageCost = diagnosticTestPackageCost;
	}

	public Amount getDiagnosticTestPackageComission() {
		return diagnosticTestPackageComission;
	}

	public void setDiagnosticTestPackageComission(Amount diagnosticTestPackageComission) {
		this.diagnosticTestPackageComission = diagnosticTestPackageComission;
	}

	public Double getDiagnosticTestCostPackageForPatient() {
		return diagnosticTestCostPackageForPatient;
	}

	public void setDiagnosticTestCostPackageForPatient(Double diagnosticTestCostPackageForPatient) {
		this.diagnosticTestCostPackageForPatient = diagnosticTestCostPackageForPatient;
	}

	public Double getTotalSavingInPercentage() {
		return totalSavingInPercentage;
	}

	public void setTotalSavingInPercentage(Double totalSavingInPercentage) {
		this.totalSavingInPercentage = totalSavingInPercentage;
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

	@Override
	public String toString() {
		return "DiagnosticTestPackage [id=" + id + ", locationName=" + locationName + ", isNABLAccredited="
				+ isNABLAccredited + ", packageName=" + packageName + ", explanation=" + explanation + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", testIds=" + testIds
				+ ", testNames=" + testNames + ", diagnosticTestPackageCost=" + diagnosticTestPackageCost
				+ ", diagnosticTestPackageComission=" + diagnosticTestPackageComission
				+ ", diagnosticTestCostPackageForPatient=" + diagnosticTestCostPackageForPatient
				+ ", totalSavingInPercentage=" + totalSavingInPercentage + "]";
	}
}
