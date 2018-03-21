package com.dpdocter.response;

import java.util.List;

public class CalenderResponseForJasper {

	private List<String> doctorId;

	private List<CalenderResponse> calenderResponse;

	public List<String> getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(List<String> doctorId) {
		this.doctorId = doctorId;
	}

	public List<CalenderResponse> getCalenderResponse() {
		return calenderResponse;
	}

	public void setCalenderResponse(List<CalenderResponse> calenderResponse) {
		this.calenderResponse = calenderResponse;
	}

}
