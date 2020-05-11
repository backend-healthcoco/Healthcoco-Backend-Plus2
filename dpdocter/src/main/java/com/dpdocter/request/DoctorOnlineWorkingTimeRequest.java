package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.WorkingSchedule;

public class DoctorOnlineWorkingTimeRequest {

	private String id;

    private String doctorId;
    
    private String locationId;

    private List<WorkingSchedule> onlineWorkingSchedules;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<WorkingSchedule> getOnlineWorkingSchedules() {
		return onlineWorkingSchedules;
	}

	public void setOnlineWorkingSchedules(List<WorkingSchedule> onlineWorkingSchedules) {
		this.onlineWorkingSchedules = onlineWorkingSchedules;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
    
    
    
    
    
}
