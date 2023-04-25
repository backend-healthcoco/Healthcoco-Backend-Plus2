package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.WorkingSchedule;

public class DoctorVisitingTimeAddEditRequest {

	private String id;

	private String doctorId;

	private String locationId;

	private List<WorkingSchedule> workingSchedules;

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

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public List<WorkingSchedule> getWorkingSchedules() {
		return workingSchedules;
	}

	public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
		this.workingSchedules = workingSchedules;
	}

	@Override
	public String toString() {
		return "DoctorVisitingTimeAddEditRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", workingSchedules=" + workingSchedules + "]";
	}
}
