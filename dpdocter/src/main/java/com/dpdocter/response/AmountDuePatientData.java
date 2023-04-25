package com.dpdocter.response;

public class AmountDuePatientData {

	private String patientName;

	private String pid;

	private Double amountDue;

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

	public Double getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(Double amountDue) {
		this.amountDue = amountDue;
	}

	@Override
	public String toString() {
		return "AmountDuePatientData [patientName=" + patientName + ", pid=" + pid + ", amountDue=" + amountDue + "]";
	}
}
