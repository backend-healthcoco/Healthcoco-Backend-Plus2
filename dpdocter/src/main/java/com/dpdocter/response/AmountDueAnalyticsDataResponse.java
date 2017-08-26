package com.dpdocter.response;

public class AmountDueAnalyticsDataResponse {

	private Double invoiced;
	
	private Double received;
	
	private String patientName;
	
	private String pid;
	
	private String doctorName;

	public Double getInvoiced() {
		return invoiced;
	}

	public void setInvoiced(Double invoiced) {
		this.invoiced = invoiced;
	}

	public Double getReceived() {
		return received;
	}

	public void setReceived(Double received) {
		this.received = received;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	@Override
	public String toString() {
		return "AmountDueAnalyticsDataResponse [invoiced=" + invoiced + ", received=" + received + ", patientName="
				+ patientName + ", pid=" + pid + ", doctorName=" + doctorName + "]";
	}
}
