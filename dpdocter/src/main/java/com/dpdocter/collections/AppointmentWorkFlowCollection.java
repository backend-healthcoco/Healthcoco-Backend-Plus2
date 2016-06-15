package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentState;

@Document(collection = "appointment_work_flow_cl")
public class AppointmentWorkFlowCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String appointmentId;

    @Field
    private WorkingHours time;

    @Field
    private AppointmentState state = AppointmentState.NEW;

    @Field
    private Date date;

    @Field
    private String cancelledBy;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public AppointmentState getState() {
	return state;
    }

    public void setState(AppointmentState state) {
	this.state = state;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	@Override
	public String toString() {
		return "AppointmentWorkFlowCollection [id=" + id + ", appointmentId=" + appointmentId + ", time=" + time
				+ ", state=" + state + ", date=" + date + ", cancelledBy=" + cancelledBy + "]";
	}
}
