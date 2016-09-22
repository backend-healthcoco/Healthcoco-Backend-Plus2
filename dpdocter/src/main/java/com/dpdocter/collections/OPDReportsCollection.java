package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "opd_report_cl")
public class OPDReportsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String serialNo;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId prescriptionId;
	@Field
	private String amountReceived;
	@Field
	private String receiptNo;
	@Field
	private String receiptDate;
	@Field
	private String remarks;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ObjectId getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(ObjectId prescriptionId) {
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

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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

	@Override
	public String toString() {
		return "OPDReportsCollection [id=" + id + ", serialNo=" + serialNo + ", patientId=" + patientId
				+ ", prescriptionId=" + prescriptionId + ", amountReceived=" + amountReceived + ", receiptNo="
				+ receiptNo + ", receiptDate=" + receiptDate + ", remarks=" + remarks + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}

}
