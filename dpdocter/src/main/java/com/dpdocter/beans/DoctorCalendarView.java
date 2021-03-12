package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.CalendarType;

public class DoctorCalendarView extends GenericCollection

{

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private CalendarType type;

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
