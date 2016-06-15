package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;

public class Appointment extends GenericCollection {

    private String id;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private WorkingHours time;

    private PatientCard patient;

    private AppointmentState state;

    private Boolean isRescheduled = false;

    private Date fromDate;

    private Date toDate;

    private String appointmentId;

    private String subject;

    private String explanation;

    private AppointmentType type;

    private Boolean isCalenderBlocked = false;

    private Boolean isFeedbackAvailable = false;

    private Boolean isAllDayEvent = false;
    
    private String doctorName;

    private String locationName;

    private String clinicAddress;
    
    private String clinicNumber;

    private Double latitude;

    private Double longitude;

    private String cancelledBy;
    
    private Boolean notifyPatientBySms;

    private Boolean notifyPatientByEmail;

    private Boolean notifyDoctorBySms;

    private Boolean notifyDoctorByEmail;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public WorkingHours getTime() {
	return time;
    }

    public void setTime(WorkingHours time) {
	this.time = time;
    }

    public PatientCard getPatient() {
	return patient;
    }

    public void setPatient(PatientCard patient) {
	this.patient = patient;
    }

    public AppointmentState getState() {
	return state;
    }

    public void setState(AppointmentState state) {
	this.state = state;
    }

    public Boolean getIsRescheduled() {
		return isRescheduled;
	}

	public void setIsRescheduled(Boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
	}

	public String getAppointmentId() {
	return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
	this.appointmentId = appointmentId;
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

	public AppointmentType getType() {
	return type;
    }

    public void setType(AppointmentType type) {
	this.type = type;
    }

    public Boolean getIsCalenderBlocked() {
	return isCalenderBlocked;
    }

    public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
	this.isCalenderBlocked = isCalenderBlocked;
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

    public Boolean getIsFeedbackAvailable() {
	return isFeedbackAvailable;
    }

    public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
	this.isFeedbackAvailable = isFeedbackAvailable;
    }

    public String getDoctorName() {
	return doctorName;
    }

    public void setDoctorName(String doctorName) {
	this.doctorName = doctorName;
    }

    public String getLocationName() {
	return locationName;
    }

    public void setLocationName(String locationName) {
	this.locationName = locationName;
    }

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public Boolean getNotifyPatientBySms() {
		return notifyPatientBySms;
	}

	public void setNotifyPatientBySms(Boolean notifyPatientBySms) {
		this.notifyPatientBySms = notifyPatientBySms;
	}

	public Boolean getNotifyPatientByEmail() {
		return notifyPatientByEmail;
	}

	public void setNotifyPatientByEmail(Boolean notifyPatientByEmail) {
		this.notifyPatientByEmail = notifyPatientByEmail;
	}

	public Boolean getNotifyDoctorBySms() {
		return notifyDoctorBySms;
	}

	public void setNotifyDoctorBySms(Boolean notifyDoctorBySms) {
		this.notifyDoctorBySms = notifyDoctorBySms;
	}

	public Boolean getNotifyDoctorByEmail() {
		return notifyDoctorByEmail;
	}

	public void setNotifyDoctorByEmail(Boolean notifyDoctorByEmail) {
		this.notifyDoctorByEmail = notifyDoctorByEmail;
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", time=" + time + ", patient=" + patient + ", state=" + state + ", isRescheduled="
				+ isRescheduled + ", fromDate=" + fromDate + ", toDate=" + toDate + ", appointmentId=" + appointmentId
				+ ", subject=" + subject + ", explanation=" + explanation + ", type=" + type + ", isCalenderBlocked="
				+ isCalenderBlocked + ", isFeedbackAvailable=" + isFeedbackAvailable + ", isAllDayEvent="
				+ isAllDayEvent + ", doctorName=" + doctorName + ", locationName=" + locationName + ", clinicAddress="
				+ clinicAddress + ", clinicNumber=" + clinicNumber + ", latitude=" + latitude + ", longitude="
				+ longitude + ", cancelledBy=" + cancelledBy + ", notifyPatientBySms=" + notifyPatientBySms
				+ ", notifyPatientByEmail=" + notifyPatientByEmail + ", notifyDoctorBySms=" + notifyDoctorBySms
				+ ", notifyDoctorByEmail=" + notifyDoctorByEmail + "]";
	}
}
