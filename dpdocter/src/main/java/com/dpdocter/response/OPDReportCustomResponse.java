package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.beans.Patient;
import com.dpdocter.collections.GenericCollection;

public class OPDReportCustomResponse extends GenericCollection {

	private String id;
	private String patientId;
	private String prescriptionId;
	private OPDPrescriptionResponse prescription;
	private String amountReceived;
	private String receiptNo;
	private Date receiptDate;
	private String remarks;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String doctorName;
	private Patient patient;
	private String locationName;

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

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public OPDPrescriptionResponse getPrescription() {
		return prescription;
	}

	public void setPrescription(OPDPrescriptionResponse prescription) {
		this.prescription = prescription;
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

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
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

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
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

	@Override
	public String toString() {
		return "OPDReportCustomResponse [id=" + id + ", patientId=" + patientId + ", prescriptionId=" + prescriptionId
				+ ", prescription=" + prescription + ", amountReceived=" + amountReceived + ", receiptNo=" + receiptNo
				+ ", receiptDate=" + receiptDate + ", remarks=" + remarks + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", doctorName=" + doctorName + ", patient=" + patient
				+ ", locationName=" + locationName + ", hospitalName=" + hospitalName + "]";
	}

}
