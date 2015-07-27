package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.ReferenceDetail;

public class ReferenceResponse {
    private List<ReferenceDetail> referenceDetails;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    public List<ReferenceDetail> getReferenceDetails() {
	return referenceDetails;
    }

    public void setReferenceDetails(List<ReferenceDetail> referenceDetails) {
	this.referenceDetails = referenceDetails;
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

    @Override
    public String toString() {
	return "ReferenceResponse [referenceDetails=" + referenceDetails + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + "]";
    }

}
