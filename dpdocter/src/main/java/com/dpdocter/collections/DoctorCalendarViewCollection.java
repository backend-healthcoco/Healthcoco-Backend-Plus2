package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.CalendarType;

@Document(collection = "doctor_calendar_view_cl")
public class DoctorCalendarViewCollection extends GenericCollection{
	@Id
	private ObjectId id;
	@Field
	private String doctorId;
	@Field
	private String locationId;
	@Field
	private String hospitalId;
	@Field
	private CalendarType type;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
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
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public CalendarType getType() {
		return type;
	}
	public void setType(CalendarType type) {
		this.type = type;
	}
	
	
}
