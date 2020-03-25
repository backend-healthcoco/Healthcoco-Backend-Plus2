package com.dpdocter.beans;


import java.util.List;

import common.util.web.JacksonUtil;

public class FlowSheet {

	private Long date;

	private String pulse;

	private String temperature;

	private String breathing;

	private String systolic;

	private String diastolic;

	private String height;

	private String weight;

	private String spo2;

	private String bmi;

	private String bsa;

	private String complaint;

	private String advice;
	
	private String iBP;
	
	private String cVP;
	
	private String fiO2;
	
	private String venhlatorMode;
	
	private String urineOutput;
	
	private String feeding;
	
	private String otherVitals;
	
	

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getPulse() {
		return pulse;
	}

	public void setPulse(String pulse) {
		this.pulse = pulse;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getBreathing() {
		return breathing;
	}

	public void setBreathing(String breathing) {
		this.breathing = breathing;
	}

	public String getSystolic() {
		return systolic;
	}

	public void setSystolic(String systolic) {
		this.systolic = systolic;
	}

	public String getDiastolic() {
		return diastolic;
	}

	public void setDiastolic(String diastolic) {
		this.diastolic = diastolic;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getSpo2() {
		return spo2;
	}

	public void setSpo2(String spo2) {
		this.spo2 = spo2;
	}

	public String getBmi() {
		return bmi;
	}

	public void setBmi(String bmi) {
		this.bmi = bmi;
	}

	public String getBsa() {
		return bsa;
	}

	public void setBsa(String bsa) {
		this.bsa = bsa;
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
	
	

	public String getiBP() {
		return iBP;
	}

	public void setiBP(String iBP) {
		this.iBP = iBP;
	}

	public String getcVP() {
		return cVP;
	}

	public void setcVP(String cVP) {
		this.cVP = cVP;
	}

	public String getFiO2() {
		return fiO2;
	}

	public void setFiO2(String fiO2) {
		this.fiO2 = fiO2;
	}

	public String getVenhlatorMode() {
		return venhlatorMode;
	}

	public void setVenhlatorMode(String venhlatorMode) {
		this.venhlatorMode = venhlatorMode;
	}

	
	

	public String getUrineOutput() {
		return urineOutput;
	}

	public void setUrineOutput(String urineOutput) {
		this.urineOutput = urineOutput;
	}

	public String getFeeding() {
		return feeding;
	}

	public void setFeeding(String feeding) {
		this.feeding = feeding;
	}

	public String getOtherVitals() {
		return otherVitals;
	}

	public void setOtherVitals(String otherVitals) {
		this.otherVitals = otherVitals;
	}

	

	@Override
	public String toString() {
		return "FlowSheet [date=" + date + ", pulse=" + pulse + ", temperature=" + temperature + ", breathing="
				+ breathing + ", systolic=" + systolic + ", diastolic=" + diastolic + ", height=" + height + ", weight="
				+ weight + ", spo2=" + spo2 + ", bmi=" + bmi + ", bsa=" + bsa + ", complaint=" + complaint + ", advice="
				+ advice + "]";
	}

	public static void main(String[] args) {
		System.out.println(JacksonUtil.obj2Json(new FlowSheet()));
	}
}
