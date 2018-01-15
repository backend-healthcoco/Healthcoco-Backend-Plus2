package com.dpdocter.beans;

import java.util.List;

public class DentalWorksSample {
	private DentalWork dentalWork;
	private List<String> toothNumbers;
	private List<DentalStage> dentalStages;
	private Long etaInDate;
	private Integer etaInHour;
	private Boolean isCompleted = false;
	private Boolean isUrgent = false;
	private String instructions;
	private String occlusalStaining;
	private String ponticDesign;
	private String collarAndMetalDesign;
	private String uniqueWorkId;
	private List<DentalWorkCardValue> dentalWorkCardValues;

	public DentalWork getDentalWork() {
		return dentalWork;
	}

	public void setDentalWork(DentalWork dentalWork) {
		this.dentalWork = dentalWork;
	}

	public List<String> getToothNumbers() {
		return toothNumbers;
	}

	public void setToothNumbers(List<String> toothNumbers) {
		this.toothNumbers = toothNumbers;
	}

	public Long getEtaInDate() {
		return etaInDate;
	}

	public void setEtaInDate(Long etaInDate) {
		this.etaInDate = etaInDate;
	}

	public Integer getEtaInHour() {
		return etaInHour;
	}

	public void setEtaInHour(Integer etaInHour) {
		this.etaInHour = etaInHour;
	}

	public List<DentalStage> getDentalStages() {
		return dentalStages;
	}

	public void setDentalStages(List<DentalStage> dentalStages) {
		this.dentalStages = dentalStages;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getOcclusalStaining() {
		return occlusalStaining;
	}

	public void setOcclusalStaining(String occlusalStaining) {
		this.occlusalStaining = occlusalStaining;
	}

	public String getPonticDesign() {
		return ponticDesign;
	}

	public void setPonticDesign(String ponticDesign) {
		this.ponticDesign = ponticDesign;
	}

	public String getCollarAndMetalDesign() {
		return collarAndMetalDesign;
	}

	public void setCollarAndMetalDesign(String collarAndMetalDesign) {
		this.collarAndMetalDesign = collarAndMetalDesign;
	}

	public String getUniqueWorkId() {
		return uniqueWorkId;
	}

	public void setUniqueWorkId(String uniqueWorkId) {
		this.uniqueWorkId = uniqueWorkId;
	}

	public List<DentalWorkCardValue> getDentalWorkCardValues() {
		return dentalWorkCardValues;
	}

	public void setDentalWorkCardValues(List<DentalWorkCardValue> dentalWorkCardValues) {
		this.dentalWorkCardValues = dentalWorkCardValues;
	}

	@Override
	public String toString() {
		return "DentalWorksSample [dentalWork=" + dentalWork + ", toothNumbers=" + toothNumbers + ", dentalStages="
				+ dentalStages + ", etaInDate=" + etaInDate + ", etaInHour=" + etaInHour + ", isCompleted="
				+ isCompleted + ", isUrgent=" + isUrgent + ", instructions=" + instructions + ", occlusalStaining="
				+ occlusalStaining + ", ponticDesign=" + ponticDesign + ", collarAndMetalDesign=" + collarAndMetalDesign
				+ "]";
	}

}
