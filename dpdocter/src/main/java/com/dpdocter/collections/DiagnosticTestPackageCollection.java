package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Amount;

@Document(collection = "diagnostic_test_package_cl")
public class DiagnosticTestPackageCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String packageName;

	@Field
	private String explanation;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private Boolean discarded = false;

	@Field
	List<ObjectId> testIds;

	@Field
    private Double diagnosticTestPackageCost = 0.0;

    @Field
    private Amount diagnosticTestPackageComission;

    @Field
    private Double diagnosticTestCostPackageForPatient = 0.0;
    
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public List<ObjectId> getTestIds() {
		return testIds;
	}

	public void setTestIds(List<ObjectId> testIds) {
		this.testIds = testIds;
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

	@Override
	public String toString() {
		return "DiagnosticTestPackageCollection [id=" + id + ", packageName=" + packageName + ", explanation="
				+ explanation + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", testIds=" + testIds + ", diagnosticTestPackageCost=" + diagnosticTestPackageCost
				+ ", diagnosticTestPackageComission=" + diagnosticTestPackageComission
				+ ", diagnosticTestCostPackageForPatient=" + diagnosticTestCostPackageForPatient + "]";
	}

}
