package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DoctorConsultation;
import com.dpdocter.beans.WorkingSchedule;

public class DoctorOnlineWorkingTimeRequest {

	private String id;

	private String doctorId;

	private List<WorkingSchedule> onlineWorkingSchedules;

	private List<DoctorConsultation> consultationType;

	private Boolean isOnlineConsultationAvailable;

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

	public List<DoctorConsultation> getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(List<DoctorConsultation> consultationType) {
		this.consultationType = consultationType;
	}

	public Boolean getIsOnlineConsultationAvailable() {
		return isOnlineConsultationAvailable;
	}

	public void setIsOnlineConsultationAvailable(Boolean isOnlineConsultationAvailable) {
		this.isOnlineConsultationAvailable = isOnlineConsultationAvailable;
	}

	@Override
	public String toString() {
		return "DoctorOnlineWorkingTimeRequest [id=" + id + ", doctorId=" + doctorId + ", onlineWorkingSchedules="
				+ onlineWorkingSchedules + ", isOnlineConsultationAvailable=" + isOnlineConsultationAvailable + "]";
	}

}
