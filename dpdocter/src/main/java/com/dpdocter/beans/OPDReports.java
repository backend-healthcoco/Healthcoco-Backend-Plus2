package com.dpdocter.beans;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OPDReports extends GenericCollection {

	private String id;
	private String patientId;
	private Patient patient;
	private String prescriptionId;
	private Prescription prescription;
	private String amountReceived;
	private String receiptNo;
	private Date receiptDate;
	private String natureOfProfessionalServiceRendered;
	private String remarks;
	private String doctorId;
	private String doctorName;
	private String locationId;
	private String locationName;
	private String hospitalId;
	private String hospitalName;
	private String visitId;
	private String treatmentId;

	public OPDReports() {
		super();
	}

	public OPDReports(String patientId, String prescriptionId, String doctorId, String locationId, String hospitalId,
			Date createdTime) {
		super();
		this.patientId = patientId;
		this.prescriptionId = prescriptionId;
		this.doctorId = doctorId;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
		super.setCreatedTime(createdTime);
	}

	public OPDReports(String patientId, String prescriptionId, String doctorId, String locationId, String hospitalId,
			String visitId, Date createdTime) {
		super();
		this.patientId = patientId;
		this.prescriptionId = prescriptionId;
		this.doctorId = doctorId;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
		this.visitId = visitId;
		super.setCreatedTime(createdTime);
	}

	public OPDReports(String patientId, String prescriptionId, String doctorId, String locationId, String hospitalId,
			Date createdTime, Date updatedTime) {
		super();
		this.patientId = patientId;
		this.prescriptionId = prescriptionId;
		this.doctorId = doctorId;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
		super.setCreatedTime(createdTime);
		super.setUpdatedTime(updatedTime);
	}

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

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
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

	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	public String getNatureOfProfessionalServiceRendered() {
		return natureOfProfessionalServiceRendered;
	}

	public void setNatureOfProfessionalServiceRendered(String natureOfProfessionalServiceRendered) {
		this.natureOfProfessionalServiceRendered = natureOfProfessionalServiceRendered;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public String getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(String treatmentId) {
		this.treatmentId = treatmentId;
	}

	@Override
	public String toString() {
		return "OPDReports [id=" + id + ", patientId=" + patientId + ", patient=" + patient + ", prescriptionId="
				+ prescriptionId + ", prescription=" + prescription + ", amountReceived=" + amountReceived
				+ ", receiptNo=" + receiptNo + ", receiptDate=" + receiptDate + ", natureOfProfessionalServiceRendered="
				+ natureOfProfessionalServiceRendered + ", remarks=" + remarks + ", doctorId=" + doctorId
				+ ", doctorName=" + doctorName + ", locationId=" + locationId + ", locationName=" + locationName
				+ ", hospitalId=" + hospitalId + ", hospitalName=" + hospitalName + ", visitId=" + visitId + "]";
	}

}
