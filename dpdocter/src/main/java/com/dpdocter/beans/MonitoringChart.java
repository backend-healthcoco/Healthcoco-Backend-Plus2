package com.dpdocter.beans;

import java.util.List;

public class MonitoringChart {
	   
	private WorkingHours time;
	
	private String intake;
	
	private String outputDrain;
	
    private String bP;
    
    private String hR;
    
    private String sPO2;
    
    private String  anySpecialEventsAndStatDrugs;
    
	private String diagnosis;	
	private String referanceDone;	
	private String nurseName;

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public String getIntake() {
		return intake;
	}

	public void setIntake(String intake) {
		this.intake = intake;
	}

	public String getOutputDrain() {
		return outputDrain;
	}

	public void setOutputDrain(String outputDrain) {
		this.outputDrain = outputDrain;
	}

	public String getbP() {
		return bP;
	}

	public void setbP(String bP) {
		this.bP = bP;
	}

	public String gethR() {
		return hR;
	}

	public void sethR(String hR) {
		this.hR = hR;
	}

	public String getsPO2() {
		return sPO2;
	}

	public void setsPO2(String sPO2) {
		this.sPO2 = sPO2;
	}

	public String getAnySpecialEventsAndStatDrugs() {
		return anySpecialEventsAndStatDrugs;
	}

	public void setAnySpecialEventsAndStatDrugs(String anySpecialEventsAndStatDrugs) {
		this.anySpecialEventsAndStatDrugs = anySpecialEventsAndStatDrugs;
	}

	
	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getReferanceDone() {
		return referanceDone;
	}

	public void setReferanceDone(String referanceDone) {
		this.referanceDone = referanceDone;
	}

	public String getNurseName() {
		return nurseName;
	}

	public void setNurseName(String nurseName) {
		this.nurseName = nurseName;
	}

	@Override
	public String toString() {
		return "MonitoringChart [time=" + time + ", intake=" + intake + ", outputDrain=" + outputDrain + ", bP=" + bP
				+ ", hR=" + hR + ", sPO2=" + sPO2 + ", anySpecialEventsAndStatDrugs=" + anySpecialEventsAndStatDrugs
				+ "]";
	}
    
    
}
