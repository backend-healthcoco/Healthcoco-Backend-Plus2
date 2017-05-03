package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.PatientVisit;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.collections.GenericCollection;

public class RecordsLookupResponse extends GenericCollection {

	public List<RecordsFile> getFiles() {
		return files;
	}

	private String id;

	private String uniqueEmrId;

	private List<RecordsFile> files;

	private String recordsType;

	private String explanation;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded;

	private Boolean inHistory;

	private String uploadedByLocation;

	private String prescriptionId;

	private String prescribedByDoctorId;

	private String prescribedByLocationId;

	private String prescribedByHospitalId;

	private String diagnosticTestId;

	private Boolean isFeedbackAvailable;

	private String recordsState;

	private PatientVisit patientVisit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getRecordsType() {
		return recordsType;
	}

	public void setRecordsType(String recordsType) {
		this.recordsType = recordsType;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	public String getUploadedByLocation() {
		return uploadedByLocation;
	}

	public void setUploadedByLocation(String uploadedByLocation) {
		this.uploadedByLocation = uploadedByLocation;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public String getPrescribedByDoctorId() {
		return prescribedByDoctorId;
	}

	public void setPrescribedByDoctorId(String prescribedByDoctorId) {
		this.prescribedByDoctorId = prescribedByDoctorId;
	}

	public String getPrescribedByLocationId() {
		return prescribedByLocationId;
	}

	public void setPrescribedByLocationId(String prescribedByLocationId) {
		this.prescribedByLocationId = prescribedByLocationId;
	}

	public String getPrescribedByHospitalId() {
		return prescribedByHospitalId;
	}

	public void setPrescribedByHospitalId(String prescribedByHospitalId) {
		this.prescribedByHospitalId = prescribedByHospitalId;
	}

	public String getDiagnosticTestId() {
		return diagnosticTestId;
	}

	public void setDiagnosticTestId(String diagnosticTestId) {
		this.diagnosticTestId = diagnosticTestId;
	}

	public Boolean getIsFeedbackAvailable() {
		return isFeedbackAvailable;
	}

	public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
		this.isFeedbackAvailable = isFeedbackAvailable;
	}

	public String getRecordsState() {
		return recordsState;
	}

	public void setRecordsState(String recordsState) {
		this.recordsState = recordsState;
	}

	public PatientVisit getPatientVisit() {
		return patientVisit;
	}

	public void setPatientVisit(PatientVisit patientVisit) {
		this.patientVisit = patientVisit;
	}

	public List<RecordsFile> getFiless() {
		return files;
	}

	public void setFiles(List<RecordsFile> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "RecordsLookupResponse [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", files=" + files
				+ ", recordsType=" + recordsType + ", explanation=" + explanation + ", patientId=" + patientId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + ", inHistory=" + inHistory + ", uploadedByLocation=" + uploadedByLocation
				+ ", prescriptionId=" + prescriptionId + ", prescribedByDoctorId=" + prescribedByDoctorId
				+ ", prescribedByLocationId=" + prescribedByLocationId + ", prescribedByHospitalId="
				+ prescribedByHospitalId + ", diagnosticTestId=" + diagnosticTestId + ", isFeedbackAvailable="
				+ isFeedbackAvailable + ", recordsState=" + recordsState + ", patientVisit=" + patientVisit + "]";
	}

}
