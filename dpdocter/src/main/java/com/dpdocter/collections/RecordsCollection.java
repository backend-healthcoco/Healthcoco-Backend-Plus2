package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RecordsUrlData;
import com.dpdocter.enums.RecordsState;

@Document(collection = "records_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class RecordsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String uniqueEmrId;

	@Field
	private String recordsUrl;
	
	@Field
	private List<RecordsUrlData> recordsUrls;

	@Field
	private String recordsPath;

	@Field
	private String recordsLabel;

	@Field
	private String recordsType;

	@Field
	private String explanation;

	@Indexed
	private ObjectId patientId;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private Boolean discarded = false;

	@Field
	private Boolean inHistory = false;

	@Field
	private String uploadedByLocation;

	@Field
	private String prescriptionId;

	@Field
	private ObjectId prescribedByDoctorId;

	@Field
	private ObjectId prescribedByLocationId;

	@Field
	private ObjectId prescribedByHospitalId;

	@Field
	private ObjectId diagnosticTestId;

	@Field
	private Boolean shareWithPatient = false;

	@Field
	private Boolean isFeedbackAvailable = false;

	@Field
	private String recordsState = RecordsState.APPROVAL_NOT_REQUIRED.toString();

	@Field
	private List<String> messages;
	
	@Field
	private ObjectId appointmentId;

	@Field
	private Boolean isPatientDiscarded = false;

	public Boolean getShareWithPatient() {
		return shareWithPatient;
	}

	public void setShareWithPatient(Boolean shareWithPatient) {
		this.shareWithPatient = shareWithPatient;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRecordsUrl() {
		return recordsUrl;
	}

	public void setRecordsUrl(String recordsUrl) {
		this.recordsUrl = recordsUrl;
	}

	public String getRecordsPath() {
		return recordsPath;
	}

	public void setRecordsPath(String recordsPath) {
		this.recordsPath = recordsPath;
	}

	public String getRecordsLabel() {
		return recordsLabel;
	}

	public void setRecordsLabel(String recordsLabel) {
		this.recordsLabel = recordsLabel;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getUploadedByLocation() {
		return uploadedByLocation;
	}

	public void setUploadedByLocation(String uploadedByLocation) {
		this.uploadedByLocation = uploadedByLocation;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public ObjectId getPrescribedByDoctorId() {
		return prescribedByDoctorId;
	}

	public void setPrescribedByDoctorId(ObjectId prescribedByDoctorId) {
		this.prescribedByDoctorId = prescribedByDoctorId;
	}

	public ObjectId getPrescribedByLocationId() {
		return prescribedByLocationId;
	}

	public void setPrescribedByLocationId(ObjectId prescribedByLocationId) {
		this.prescribedByLocationId = prescribedByLocationId;
	}

	public ObjectId getPrescribedByHospitalId() {
		return prescribedByHospitalId;
	}

	public void setPrescribedByHospitalId(ObjectId prescribedByHospitalId) {
		this.prescribedByHospitalId = prescribedByHospitalId;
	}

	public ObjectId getDiagnosticTestId() {
		return diagnosticTestId;
	}

	public void setDiagnosticTestId(ObjectId diagnosticTestId) {
		this.diagnosticTestId = diagnosticTestId;
	}

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
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

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public List<RecordsUrlData> getRecordsUrls() {
		return recordsUrls;
	}

	public void setRecordsUrls(List<RecordsUrlData> recordsUrls) {
		this.recordsUrls = recordsUrls;
	}

	public ObjectId getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(ObjectId appointmentId) {
		this.appointmentId = appointmentId;
	}

	@Override
	public String toString() {
		return "RecordsCollection [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", recordsUrl=" + recordsUrl
				+ ", recordsPath=" + recordsPath + ", recordsLabel=" + recordsLabel + ", recordsType=" + recordsType
				+ ", explanation=" + explanation + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", inHistory=" + inHistory + ", uploadedByLocation=" + uploadedByLocation + ", prescriptionId="
				+ prescriptionId + ", prescribedByDoctorId=" + prescribedByDoctorId + ", prescribedByLocationId="
				+ prescribedByLocationId + ", prescribedByHospitalId=" + prescribedByHospitalId + ", diagnosticTestId="
				+ diagnosticTestId + ", isFeedbackAvailable=" + isFeedbackAvailable + ", recordsState=" + recordsState
				+ ", messages=" + messages + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}


}