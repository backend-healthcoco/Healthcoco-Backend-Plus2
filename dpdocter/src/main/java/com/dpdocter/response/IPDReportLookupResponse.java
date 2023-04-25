package com.dpdocter.response;

import com.dpdocter.beans.Patient;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;

public class IPDReportLookupResponse extends GenericCollection {

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
	private UserCollection doctor;
	private String locationId;
	private LocationCollection location;
	private String hospitalId;
	private HospitalCollection hospital;
	private Boolean discarded = false;

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

	public Long getAdmissionTime() {
		return admissionTime;
	}

	public void setAdmissionTime(Long admissionTime) {
		this.admissionTime = admissionTime;
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
		return "IPDReportLookupResponse [id=" + id + ", patientId=" + patientId + ", patient=" + patient
				+ ", admissionTime=" + admissionTime + ", dateOfAdmission=" + dateOfAdmission + ", timeOfAdmission="
				+ timeOfAdmission + ", doctorIncharge=" + doctorIncharge + ", diagnosis=" + diagnosis
				+ ", natureOfProfessionalServiceRendered=" + natureOfProfessionalServiceRendered + ", amountReceived="
				+ amountReceived + ", receiptNo=" + receiptNo + ", receiptDate=" + receiptDate + ", remarks=" + remarks
				+ ", doctorId=" + doctorId + ", doctor=" + doctor + ", locationId=" + locationId + ", location="
				+ location + ", hospitalId=" + hospitalId + ", hospital=" + hospital + "]";
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
}
