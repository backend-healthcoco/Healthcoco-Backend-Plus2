package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class RecordsAddRequest {

    private String patientId;

    private String doctorId;

    private String recordsUrl;

    private String explanation;

    private FileDetails fileDetails;

    private String locationId;

    private String hospitalId;

    private String recordsType;

    private String createdBy;

    private String visitId;

    private String prescriptionId;

    private String diagnosticTestId;

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

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public FileDetails getFileDetails() {
	return fileDetails;
    }

    public void setFileDetails(FileDetails fileDetails) {
	this.fileDetails = fileDetails;
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

    public String getRecordsType() {
	return recordsType;
    }

    public void setRecordsType(String recordsType) {
	this.recordsType = recordsType;
    }

    public String getCreatedBy() {
	return createdBy;
    }

    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
    }

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    public String getPrescriptionId() {
	return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
	this.prescriptionId = prescriptionId;
    }

	public String getDiagnosticTestId() {
		return diagnosticTestId;
	}

	public void setDiagnosticTestId(String diagnosticTestId) {
		this.diagnosticTestId = diagnosticTestId;
	}

	public String getRecordsUrl() {
		return recordsUrl;
	}

	public void setRecordsUrl(String recordsUrl) {
		this.recordsUrl = recordsUrl;
	}

	@Override
	public String toString() {
		return "RecordsAddRequest [patientId=" + patientId + ", doctorId=" + doctorId + ", recordsUrl=" + recordsUrl
				+ ", explanation=" + explanation + ", fileDetails=" + fileDetails + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", recordsType=" + recordsType + ", createdBy=" + createdBy
				+ ", visitId=" + visitId + ", prescriptionId=" + prescriptionId + ", diagnosticTestId=" + diagnosticTestId + "]";
	}
}
