package com.dpdocter.request;

import java.util.List;

public class PatientGroupAddEditRequest {

    private String patientId;

    private List<String> groupIds;

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public List<String> getGroupIds() {
	return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
	this.groupIds = groupIds;
    }

    @Override
    public String toString() {
	return "PatientGroupAddEditRequest [patientId=" + patientId + ", groupIds=" + groupIds + "]";
    }

}
