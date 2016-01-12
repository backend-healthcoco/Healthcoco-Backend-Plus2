package com.dpdocter.request;

import java.util.List;

public class PatientGroupAddEditRequest {

    private String patientId;

    private String doctorId;

    private String locationId;

    private String hospitaId;

    private List<String> groupIds;

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

	public String getHospitaId() {
		return hospitaId;
	}

	public void setHospitaId(String hospitaId) {
		this.hospitaId = hospitaId;
	}

	public List<String> getGroupIds() {
	return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
	this.groupIds = groupIds;
    }

	@Override
	public String toString() {
		return "PatientGroupAddEditRequest [patientId=" + patientId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitaId=" + hospitaId + ", groupIds=" + groupIds + "]";
	}
}
