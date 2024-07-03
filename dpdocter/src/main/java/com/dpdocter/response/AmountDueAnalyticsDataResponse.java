package com.dpdocter.response;

public class AmountDueAnalyticsDataResponse {

	private Double invoiced = 0.0;

	private Double received = 0.0;

	private String patientName;

	private String pid;

	private String doctorName;

	private Double amountDue = 0.0;
	
	private Double totalDueAmount = 0.0;
	
	private Double totalDiscount = 0.0;

	public Double getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(Double amountDue) {
		this.amountDue = amountDue;
	}

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

	public Double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(Double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	public Double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	@Override
	public String toString() {
		return "AmountDueAnalyticsDataResponse [invoiced=" + invoiced + ", received=" + received + ", patientName="
				+ patientName + ", pid=" + pid + ", doctorName=" + doctorName + "]";
	}
}
