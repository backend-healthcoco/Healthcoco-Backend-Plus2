package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.response.DoctorResponse;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Event extends GenericCollection {

    private String id;

    private AppointmentState state;

    private String subject;

    private String explanation;

    private String locationId;

    private String doctorId;

    private WorkingHours time;

    private Boolean isCalenderBlocked = false;

    private Date fromDate;
    
    private Date toDate;

    private Boolean isAllDayEvent = false;
    
    private Boolean isRescheduled = false;
    
    private List<String> doctorIds;

    private String localPatientName;
    
    private List<DoctorResponse> doctors;
    
    private String hospitalId;
    
    private String patientId;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Boolean getIsCalenderBlocked() {
		return isCalenderBlocked;
	}

	public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
		this.isCalenderBlocked = isCalenderBlocked;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Boolean getIsAllDayEvent() {
		return isAllDayEvent;
	}

	public void setIsAllDayEvent(Boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	public Boolean getIsRescheduled() {
		return isRescheduled;
	}

	public void setIsRescheduled(Boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
	}

	public List<String> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(List<String> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public List<DoctorResponse> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<DoctorResponse> doctors) {
		this.doctors = doctors;
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
		return "Event [id=" + id + ", state=" + state + ", subject=" + subject + ", explanation=" + explanation
				+ ", locationId=" + locationId + ", doctorId=" + doctorId + ", time=" + time + ", isCalenderBlocked="
				+ isCalenderBlocked + ", fromDate=" + fromDate + ", toDate=" + toDate + ", isAllDayEvent="
				+ isAllDayEvent + ", isRescheduled=" + isRescheduled + ", doctorIds=" + doctorIds
				+ ", localPatientName=" + localPatientName + ", doctors=" + doctors + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + "]";
	}

}
