package com.dpdocter.beans;

public class IPDReports {

	private String id;
	private String serialNo;
	private String patientId;
	private Patient patient;
	private Long admissionTime;
	private String doctorIncharge;
	private String diagnosis;
	private String natureOfProfessionalServiceRendered;
	private String amountReceived;
	private String receiptNo;
	private Long receiptDate;
	private String remarks;
	private String doctorId;
	private String locationId;
	private String hospitalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
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

	@Override
	public String toString() {
		return "IPDReports [id=" + id + ", serialNo=" + serialNo + ", patientId=" + patientId + ", patient=" + patient
				+ ", admissionTime=" + admissionTime + ", doctorIncharge=" + doctorIncharge + ", diagnosis=" + diagnosis
				+ ", natureOfProfessionalServiceRendered=" + natureOfProfessionalServiceRendered + ", amountReceived="
				+ amountReceived + ", receiptNo=" + receiptNo + ", receiptDate=" + receiptDate + ", remarks=" + remarks
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}

}
