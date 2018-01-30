package com.dpdocter.request;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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

    private String diagnosticTestId;

    private String recordsUrl;
    
    private Boolean registerPatient = false;
    
    private String firstName;
	
	private String mobileNumber;
	
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

	public Boolean getRegisterPatient() {
		return registerPatient;
	}

	public void setRegisterPatient(Boolean registerPatient) {
		this.registerPatient = registerPatient;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "RecordsAddRequestMultipart [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", explanation=" + explanation + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", recordsType=" + recordsType + ", visitId=" + visitId + ", prescriptionId=" + prescriptionId
				+ ", diagnosticTestId=" + diagnosticTestId + ", recordsUrl=" + recordsUrl + ", registerPatient="
				+ registerPatient + ", firstName=" + firstName + ", mobileNumber=" + mobileNumber + "]";
	}
}
