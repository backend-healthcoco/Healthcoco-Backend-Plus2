package com.dpdocter.beans;

import java.util.List;

public class OPDReports {

	private String id;
	private String serialNo;
	private String patientId;
	private Patient patient;
	private String prescriptionId;
	private List<String> drugName;
	private List<String> drugId;
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

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
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

	public List<String> getDrugName() {
		return drugName;
	}

	public void setDrugName(List<String> drugName) {
		this.drugName = drugName;
	}

	public List<String> getDrugId() {
		return drugId;
	}

	public void setDrugId(List<String> drugId) {
		this.drugId = drugId;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public String toString() {
		return "OPDReports [id=" + id + ", serialNo=" + serialNo + ", patientId=" + patientId + ", prescriptionId="
				+ prescriptionId + ", amountReceived=" + amountReceived + ", receiptNo=" + receiptNo + ", receiptDate="
				+ receiptDate + ", remarks=" + remarks + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + "]";
	}

}
