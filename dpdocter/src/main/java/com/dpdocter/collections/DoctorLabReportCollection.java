package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RecordsFile;

@Document(collection = "doctor_lab_report_cl")
public class DoctorLabReportCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String uniqueReportId;
	@Field
	private List<RecordsFile> recordsFiles;
	@Field
	private String recordsLabel;
	@Field
	private String explanation;
	@Field
	private String patientName;
	@Field
	private String mobileNumber;
	@Field
	private Boolean shareWithPatient = false;
	@Field
	private Boolean shareWithDoctor = false;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId uploadedByDoctorId;
	@Field
	private ObjectId uploadedByLocationId;
	@Field
	private ObjectId uploadedByHospitalId;
	@Field
	private Boolean discarded = false;
	@Field
	private String doctorName;
	@Field
	private String doctorMobileNumber;
	@Field
	private Boolean isPatientDiscarded = false;
	
	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDoctorMobileNumber() {
		return doctorMobileNumber;
	}

	public void setDoctorMobileNumber(String doctorMobileNumber) {
		this.doctorMobileNumber = doctorMobileNumber;
	}

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

	public String getUniqueReportId() {
		return uniqueReportId;
	}

	public void setUniqueReportId(String uniqueReportId) {
		this.uniqueReportId = uniqueReportId;
	}

	public List<RecordsFile> getRecordsFiles() {
		return recordsFiles;
	}

	public void setRecordsFiles(List<RecordsFile> recordsFiles) {
		this.recordsFiles = recordsFiles;
	}

	public String getRecordsLabel() {
		return recordsLabel;
	}

	public void setRecordsLabel(String recordsLabel) {
		this.recordsLabel = recordsLabel;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Boolean getShareWithPatient() {
		return shareWithPatient;
	}

	public void setShareWithPatient(Boolean shareWithPatient) {
		this.shareWithPatient = shareWithPatient;
	}

	public Boolean getShareWithDoctor() {
		return shareWithDoctor;
	}

	public void setShareWithDoctor(Boolean shareWithDoctor) {
		this.shareWithDoctor = shareWithDoctor;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public ObjectId getUploadedByDoctorId() {
		return uploadedByDoctorId;
	}

	public void setUploadedByDoctorId(ObjectId uploadedByDoctorId) {
		this.uploadedByDoctorId = uploadedByDoctorId;
	}

	public ObjectId getUploadedByLocationId() {
		return uploadedByLocationId;
	}

	public void setUploadedByLocationId(ObjectId uploadedByLocationId) {
		this.uploadedByLocationId = uploadedByLocationId;
	}

	public ObjectId getUploadedByHospitalId() {
		return uploadedByHospitalId;
	}

	public void setUploadedByHospitalId(ObjectId uploadedByHospitalId) {
		this.uploadedByHospitalId = uploadedByHospitalId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DoctorLabReportCollection [id=" + id + ", uniqueReportId=" + uniqueReportId + ", recordsFiles="
				+ recordsFiles + ", recordsLabel=" + recordsLabel + ", explanation=" + explanation + ", patientName="
				+ patientName + ", mobileNumber=" + mobileNumber + ", shareWithPatient=" + shareWithPatient
				+ ", shareWithDoctor=" + shareWithDoctor + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", uploadedByDoctorId="
				+ uploadedByDoctorId + ", uploadedByLocationId=" + uploadedByLocationId + ", uploadedByHospitalId="
				+ uploadedByHospitalId + ", discarded=" + discarded + ", doctorName=" + doctorName
				+ ", doctorMobileNumber=" + doctorMobileNumber + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
