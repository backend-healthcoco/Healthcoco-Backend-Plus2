package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ipd_reports_cl")
public class IPDReportsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private String admissionTime;
	@Field
	private Long dateOfAdmission;
	@Field
	private Integer timeOfAdmission;
	@Field
	private String doctorIncharge;
	@Field
	private String diagnosis;
	@Field
	private String natureOfProfessionalServiceRendered;
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
	@Field
	private Boolean discarded=false;
	@Field
	private Boolean isPatientDiscarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getAdmissionTime() {
		return admissionTime;
	}

	public void setAdmissionTime(String admissionTime) {
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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "IPDReportsCollection [id=" + id + ", patientId=" + patientId + ", admissionTime=" + admissionTime
				+ ", dateOfAdmission=" + dateOfAdmission + ", timeOfAdmission=" + timeOfAdmission + ", doctorIncharge="
				+ doctorIncharge + ", diagnosis=" + diagnosis + ", natureOfProfessionalServiceRendered="
				+ natureOfProfessionalServiceRendered + ", amountReceived=" + amountReceived + ", receiptNo="
				+ receiptNo + ", receiptDate=" + receiptDate + ", remarks=" + remarks + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}
