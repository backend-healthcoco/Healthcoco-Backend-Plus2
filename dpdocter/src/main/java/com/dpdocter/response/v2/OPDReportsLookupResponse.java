package com.dpdocter.response.v2;

import java.util.Date;

import com.dpdocter.beans.Patient;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.UserCollection;

public class OPDReportsLookupResponse extends GenericCollection {

	private String id;
	private String patientId;
	private Patient patient;
	private String prescriptionId;
	private PrescriptionCollection prescriptionCollection;
	private String amountReceived;
	private String receiptNo;
	private Date receiptDate;
	private String remarks;
	private String doctorId;
	private UserCollection doctor;
	private String locationId;
	private LocationCollection location;
	private String hospitalId;
	private HospitalCollection hospital;

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

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public PrescriptionCollection getPrescriptionCollection() {
		return prescriptionCollection;
	}

	public void setPrescriptionCollection(PrescriptionCollection prescriptionCollection) {
		this.prescriptionCollection = prescriptionCollection;
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

	public UserCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(UserCollection doctor) {
		this.doctor = doctor;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public LocationCollection getLocation() {
		return location;
	}

	public void setLocation(LocationCollection location) {
		this.location = location;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public HospitalCollection getHospital() {
		return hospital;
	}

	public void setHospital(HospitalCollection hospital) {
		this.hospital = hospital;
	}

	@Override
	public String toString() {
		return "OPDReportsLookupResponse [id=" + id + ", patientId=" + patientId + ", patient=" + patient
				+ ", prescriptionId=" + prescriptionId + ", prescriptionCollection=" + prescriptionCollection
				+ ", amountReceived=" + amountReceived + ", receiptNo=" + receiptNo + ", receiptDate=" + receiptDate
				+ ", remarks=" + remarks + ", doctorId=" + doctorId + ", doctor=" + doctor + ", locationId="
				+ locationId + ", location=" + location + ", hospitalId=" + hospitalId + ", hospital=" + hospital + "]";
	}
}
