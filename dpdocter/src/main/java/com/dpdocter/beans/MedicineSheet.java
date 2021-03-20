package com.dpdocter.beans;

import java.util.List;

public class MedicineSheet {


	private List<String> nurseName;
	private String drugName;
	private String time;
	private String advice;
	private Boolean isHighRiskMedicine = false;
	
	public List<String> getNurseName() {
		return nurseName;
	}
	public void setNurseName(List<String> nurseName) {
		this.nurseName = nurseName;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public Boolean getIsHighRiskMedicine() {
		return isHighRiskMedicine;
	}
	public void setIsHighRiskMedicine(Boolean isHighRiskMedicine) {
		this.isHighRiskMedicine = isHighRiskMedicine;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	
	
}
