package com.dpdocter.response;

import java.util.Date;

public class AppointmentDetailAnalyticResponse {

	private Date date;
	
	private Integer fromTime;
	
	private Long waitedFor;
    
    private Long engagedAt;
    
    private Long checkedInAt;
    
    private Long checkedOutAt;
    
    private String patientName;
    
    private String doctorName;
    
    private String locationId;

    private String hospitalId;

    private String patientId;

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Integer getFromTime() {
		return fromTime;
	}
	public void setFromTime(Integer fromTime) {
		this.fromTime = fromTime;
	}
	public Long getWaitedFor() {
		return waitedFor;
	}
	public void setWaitedFor(Long waitedFor) {
		this.waitedFor = waitedFor;
	}
	public Long getEngagedAt() {
		return engagedAt;
	}
	public void setEngagedAt(Long engagedAt) {
		this.engagedAt = engagedAt;
	}
	public Long getCheckedInAt() {
		return checkedInAt;
	}
	public void setCheckedInAt(Long checkedInAt) {
		this.checkedInAt = checkedInAt;
	}
	public Long getCheckedOutAt() {
		return checkedOutAt;
	}
	public void setCheckedOutAt(Long checkedOutAt) {
		this.checkedOutAt = checkedOutAt;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
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
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	@Override
	public String toString() {
		return "AppointmentDeatilAnalyticResponse [date=" + date + ", fromTime=" + fromTime + ", waitedFor=" + waitedFor
				+ ", engagedAt=" + engagedAt + ", checkedInAt=" + checkedInAt + ", checkedOutAt=" + checkedOutAt
				+ ", patientName=" + patientName + ", doctorName=" + doctorName + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + "]";
	}

}
