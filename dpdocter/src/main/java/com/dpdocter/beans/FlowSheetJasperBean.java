package com.dpdocter.beans;

public class FlowSheetJasperBean {
	private Integer no = 0;

	private String date = " ";

	private String examination = " ";

	private String complaint;

	private String advice;
	
	private String monitoringChart =" ";

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getExamination() {
		return examination;
	}

	public void setExamination(String examination) {
		this.examination = examination;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getMonitoringChart() {
		return monitoringChart;
	}

	public void setMonitoringChart(String monitoringChart) {
		this.monitoringChart = monitoringChart;
	}
	
	

}
