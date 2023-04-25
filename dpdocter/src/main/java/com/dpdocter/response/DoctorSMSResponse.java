package com.dpdocter.response;

public class DoctorSMSResponse {

	private String doctorId;

	private String doctorName;

	private String msgSentCount;

	private String msgLeftCount;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getMsgSentCount() {
		return msgSentCount;
	}

	public void setMsgSentCount(String msgSentCount) {
		this.msgSentCount = msgSentCount;
	}

	public String getMsgLeftCount() {
		return msgLeftCount;
	}

	public void setMsgLeftCount(String msgLeftCount) {
		this.msgLeftCount = msgLeftCount;
	}

	@Override
	public String toString() {
		return "DoctorSMSResponse [doctorId=" + doctorId + ", doctorName=" + doctorName + ", msgSentCount="
				+ msgSentCount + ", msgLeftCount=" + msgLeftCount + "]";
	}
}
