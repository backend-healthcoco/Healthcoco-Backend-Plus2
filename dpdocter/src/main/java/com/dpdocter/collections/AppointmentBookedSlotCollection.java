package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;

@Document(collection = "appointment_booked_slot_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class AppointmentBookedSlotCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String appointmentId;

    @Indexed
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private WorkingHours time;

    @Indexed
    private Date fromDate;

    @Indexed
    private Date toDate;

    @Field
    private Boolean isAllDayEvent = false;
    
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

    public WorkingHours getTime() {
	return time;
    }

    public void setTime(WorkingHours time) {
	this.time = time;
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

	@Override
	public String toString() {
		return "AppointmentBookedSlotCollection [id=" + id + ", appointmentId=" + appointmentId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", time=" + time
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", isAllDayEvent=" + isAllDayEvent + "]";
	}
}
