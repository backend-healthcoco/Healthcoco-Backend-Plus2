package com.dpdocter.response;

import java.util.List;

public class CalenderResponseForJasper {

	private String doctorId;

	private List<CalenderResponse> calenderResponse;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<CalenderResponse> getCalenderResponse() {
		return calenderResponse;
	}

	public void setCalenderResponse(List<CalenderResponse> calenderResponse) {
		this.calenderResponse = calenderResponse;
	}

}