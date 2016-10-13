package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IPDReports extends GenericCollection {

	private String id;
	private String patientId;
	private Patient patient;
	private Long admissionTime;
	private Long dateOfAdmission;
	private Integer timeOfAdmission;
	private String doctorIncharge;
	private String diagnosis;
	private String natureOfProfessionalServiceRendered;
	private String amountReceived;
	private String receiptNo;
	private Long receiptDate;
	private String remarks;
	private String doctorId;
	private String doctorName;
	private String locationId;
	private String locationName;
	private String hospitalId;
	private String hospitalName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Long getAdmissionTime() {
		return admissionTime;
	}

	public void setAdmissionTime(Long admissionTime) {
		this.admissionTime = admissionTime;
	}

	public String getDoctorIncharge() {
		return doctorIncharge;
	}

	public void setDoctorIncharge(String doctorIncharge) {
		this.doctorIncharge = doctorIncharge;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getNatureOfProfessionalServiceRendered() {
		return natureOfProfessionalServiceRendered;
	}

	public void setNatureOfProfessionalServiceRendered(String natureOfProfessionalServiceRendered) {
		this.natureOfProfessionalServiceRendered = natureOfProfessionalServiceRendered;
	}

	public String getAmountReceived() {
		return amountReceived;
	}

	public void setAmountReceived(String amountReceived) {
		this.amountReceived = amountReceived;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Long getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Long receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
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

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public Long getDateOfAdmission() {
		return dateOfAdmission;
	}

	public void setDateOfAdmission(Long dateOfAdmission) {
		this.dateOfAdmission = dateOfAdmission;
	}

	public Integer getTimeOfAdmission() {
		return timeOfAdmission;
	}

	public void setTimeOfAdmission(Integer timeOfAdmission) {
		this.timeOfAdmission = timeOfAdmission;
	}

	@Override
	public String toString() {
		return "IPDReports [id=" + id + ", patientId=" + patientId + ", patient=" + patient + ", admissionTime="
				+ admissionTime + ", doctorIncharge=" + doctorIncharge + ", diagnosis=" + diagnosis
				+ ", natureOfProfessionalServiceRendered=" + natureOfProfessionalServiceRendered + ", amountReceived="
				+ amountReceived + ", receiptNo=" + receiptNo + ", receiptDate=" + receiptDate + ", remarks=" + remarks
				+ ", doctorId=" + doctorId + ", doctorName=" + doctorName + ", locationId=" + locationId
				+ ", locationName=" + locationName + ", hospitalId=" + hospitalId + ", hospitalName=" + hospitalName
				+ "]";
	}

}
