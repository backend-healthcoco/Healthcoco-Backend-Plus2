package com.dpdocter.request;

public class RecordsAddRequestMultipart {

	private String id;
	
    private String patientId;

    private String doctorId;

    private String explanation;

    private String locationId;

    private String hospitalId;

    private String recordsType;

    private String visitId;

    private String prescriptionId;

    private String testId;

    private String recordsUrl;
    
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

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getRecordsUrl() {
		return recordsUrl;
	}

	public void setRecordsUrl(String recordsUrl) {
		this.recordsUrl = recordsUrl;
	}

	@Override
	public String toString() {
		return "RecordsAddRequestMultipart [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", explanation=" + explanation + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", recordsType=" + recordsType + ", visitId=" + visitId + ", prescriptionId=" + prescriptionId
				+ ", testId=" + testId + ", recordsUrl=" + recordsUrl + "]";
	}
}
