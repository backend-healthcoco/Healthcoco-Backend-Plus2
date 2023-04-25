package com.dpdocter.response;

import java.util.List;

public class SMSResponse {

	private List<DoctorSMSResponse> doctors;

	private String totalMsgLeft;

	public List<DoctorSMSResponse> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<DoctorSMSResponse> doctors) {
		this.doctors = doctors;
	}

	public String getTotalMsgLeft() {
		return totalMsgLeft;
	}

	public void setTotalMsgLeft(String totalMsgLeft) {
		this.totalMsgLeft = totalMsgLeft;
	}

	@Override
	public String toString() {
		return "SMSResponse [doctors=" + doctors + ", totalMsgLeft=" + totalMsgLeft + "]";
	}
}
