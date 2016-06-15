package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;

@Document(collection = "appointment_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class AppointmentCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String subject;

    @Field
    private String explanation;

    @Indexed
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String appointmentId;

    @Field
    private WorkingHours time;

    @Indexed
    private String patientId;

    @Field
    private AppointmentState state = AppointmentState.NEW;

    @Field
    private AppointmentType type = AppointmentType.APPOINTMENT;

    @Field
    private Boolean isRescheduled = false;

    @Field
    private Date fromDate;

    @Field
    private Date toDate;
    
    @Field
    private Boolean isCalenderBlocked = false;

    @Field
    private Boolean isFeedbackAvailable = false;

    @Field
    private Boolean isAllDayEvent = false;
    
    @Field
    private String cancelledBy;
    
    @Field
    private Boolean notifyPatientBySms;

    @Field
    private Boolean notifyPatientByEmail;

    @Field
    private Boolean notifyDoctorBySms;

    @Field
    private Boolean notifyDoctorByEmail;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

	public String getAppointmentId() {
	return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
	this.appointmentId = appointmentId;
    }

    public WorkingHours getTime() {
	return time;
    }

    public void setTime(WorkingHours time) {
	this.time = time;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public AppointmentState getState() {
	return state;
    }

    public void setState(AppointmentState state) {
	this.state = state;
    }

    public AppointmentType getType() {
	return type;
    }

    public void setType(AppointmentType type) {
	this.type = type;
    }

    public Boolean getIsRescheduled() {
	return isRescheduled;
    }

    public void setIsRescheduled(Boolean isRescheduled) {
	this.isRescheduled = isRescheduled;
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
		return "AppointmentCollection [id=" + id + ", subject=" + subject + ", explanation=" + explanation
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", patientId=" + patientId + ", state="
				+ state + ", type=" + type + ", isRescheduled=" + isRescheduled + ", fromDate=" + fromDate + ", toDate="
				+ toDate + ", isCalenderBlocked=" + isCalenderBlocked + ", isFeedbackAvailable=" + isFeedbackAvailable
				+ ", isAllDayEvent=" + isAllDayEvent + ", cancelledBy=" + cancelledBy + ", notifyPatientBySms="
				+ notifyPatientBySms + ", notifyPatientByEmail=" + notifyPatientByEmail + ", notifyDoctorBySms="
				+ notifyDoctorBySms + ", notifyDoctorByEmail=" + notifyDoctorByEmail + "]";
	}
}
