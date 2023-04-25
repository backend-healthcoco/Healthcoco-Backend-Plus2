package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.collections.GenericCollection;

public class PrescriptionLookupResponse extends GenericCollection {

	private String id;

	private String uniqueEmrId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<TestAndRecordData> diagnosticTests;

	private String patientId;

	private String firstName;

	private String mobileNumber;

	private String doctorName;

	private String locationName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public List<TestAndRecordData> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<TestAndRecordData> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	@Override
	public String toString() {
		return "PrescriptionLookupResponse [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", diagnosticTests=" + diagnosticTests
				+ ", patientId=" + patientId + ", firstName=" + firstName + ", mobileNumber=" + mobileNumber
				+ ", doctorName=" + doctorName + ", locationName=" + locationName + "]";
	}
}
