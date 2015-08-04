package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Notes extends GenericCollection {

    private String id;

    private String note;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private boolean inHistory = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getNote() {
	return note;
    }

    public void setNote(String note) {
	this.note = note;
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

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    @Override
    public String toString() {
	return "Notes [id=" + id + ", note=" + note + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", inHistory="
		+ inHistory + "]";
    }

}
