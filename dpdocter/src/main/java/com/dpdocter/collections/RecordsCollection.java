package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "records_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class RecordsCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String uniqueEmrId;

    @Field
    private String recordsUrl;

    @Field
    private String recordsPath;

    @Field
    private String recordsLabel;

    @Field
    private String recordsType;

    @Field
    private String explanation;

    @Indexed
    private String patientId;

    @Indexed
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Boolean discarded = false;

    @Field
    private Boolean inHistory = false;

    @Field
    private String uploadedByLocation;

    @Field
    private String prescriptionId;

    @Field
    private String prescribedByDoctorId;

    @Field
    private String prescribedByLocationId;

    @Field
    private String prescribedByHospitalId;

    @Field
    private String diagnosticTestId;

    @Field
    private Boolean isFeedbackAvailable = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
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

	@Override
	public String toString() {
		return "RecordsCollection [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", recordsUrl=" + recordsUrl
				+ ", recordsPath=" + recordsPath + ", recordsLabel=" + recordsLabel + ", recordsType=" + recordsType
				+ ", explanation=" + explanation + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", inHistory=" + inHistory + ", uploadedByLocation=" + uploadedByLocation + ", prescriptionId="
				+ prescriptionId + ", prescribedByDoctorId=" + prescribedByDoctorId + ", prescribedByLocationId="
				+ prescribedByLocationId + ", prescribedByHospitalId=" + prescribedByHospitalId + ", diagnosticTestId=" + diagnosticTestId
				+ ", isFeedbackAvailable=" + isFeedbackAvailable + "]";
	}

}
