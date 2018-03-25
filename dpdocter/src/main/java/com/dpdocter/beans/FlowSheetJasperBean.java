package com.dpdocter.beans;

public class FlowSheetJasperBean {
	private Integer no = 0;

	private String date = " ";

	private String pulseWeightAndBsa;

	private String tempHeightBreathAndSystDiast;

	private String bpBmiAndSpo;

	private String complaint;

	private String advice = " ";

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

	public String getPulseWeightAndBsa() {
		return pulseWeightAndBsa;
	}

	public void setPulseWeightAndBsa(String pulseWeightAndBsa) {
		this.pulseWeightAndBsa = pulseWeightAndBsa;
	}

	public String getTempHeightBreathAndSystDiast() {
		return tempHeightBreathAndSystDiast;
	}

	public void setTempHeightBreathAndSystDiast(String tempHeightBreathAndSystDiast) {
		this.tempHeightBreathAndSystDiast = tempHeightBreathAndSystDiast;
	}

	public String getBpBmiAndSpo() {
		return bpBmiAndSpo;
	}

	public void setBpBmiAndSpo(String bpBmiAndSpo) {
		this.bpBmiAndSpo = bpBmiAndSpo;
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

}
