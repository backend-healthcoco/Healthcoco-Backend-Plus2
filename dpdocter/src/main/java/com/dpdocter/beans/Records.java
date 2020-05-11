package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.RecordsState;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Records extends GenericCollection {
    private String id;

    private String uniqueEmrId;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String patientId;
    
    private String appointmentId;

    private String recordsUrl;

    private String recordsLabel;

    private String recordsType;

    private String explanation;

    private Boolean inHistory = false;

    private Boolean discarded = false;

    private String uploadedByLocation;

    private String visitId;

    private String prescriptionId;

    private String prescribedByDoctorId;

    private String prescribedByLocationId;

    private String prescribedByHospitalId;

    private String diagnosticTestId;

    private Boolean isFeedbackAvailable = false;

    private String recordsState = RecordsState.APPROVAL_NOT_REQUIRED.toString();
    
    private Boolean shareWithPatient = false;
    
    
    public Boolean getShareWithPatient() {
		return shareWithPatient;
	}

	public void setShareWithPatient(Boolean shareWithPatient) {
		this.shareWithPatient = shareWithPatient;
	}

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

	public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
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

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
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

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getRecordsState() {
		return recordsState;
	}

	public void setRecordsState(String recordsState) {
		this.recordsState = recordsState;
	}
	
	

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	@Override
	public String toString() {
		return "Records [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", recordsUrl=" + recordsUrl
				+ ", recordsLabel=" + recordsLabel + ", recordsType=" + recordsType + ", explanation=" + explanation
				+ ", inHistory=" + inHistory + ", discarded=" + discarded + ", uploadedByLocation=" + uploadedByLocation
				+ ", visitId=" + visitId + ", prescriptionId=" + prescriptionId + ", prescribedByDoctorId="
				+ prescribedByDoctorId + ", prescribedByLocationId=" + prescribedByLocationId
				+ ", prescribedByHospitalId=" + prescribedByHospitalId + ", diagnosticTestId=" + diagnosticTestId
				+ ", isFeedbackAvailable=" + isFeedbackAvailable + ", recordsState=" + recordsState + "]";
	}
}