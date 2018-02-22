package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.response.ImageURLResponse;

public class DentalWorksSample {
	private DentalWork dentalWork;
	private List<DentalToothNumber> dentalToothNumbers;
	private List<DentalStage> dentalStagesForLab;
	private Long etaInDate;
	private Integer etaInHour;
	private Boolean isCompleted = false;
	private Boolean isUrgent = false;
	private String instructions;
	private String occlusalStaining;
	private String ponticDesign;
	private String collarAndMetalDesign;
	private String uniqueWorkId;
	private List<ImageURLResponse> dentalImages;
	private List<DentalWorkCardValue> dentalWorkCardValues;
	private String shade;
	private List<String> material;
	private List<DentalStage> dentalStagesForDoctor;
	private RateCardDentalWorkAssociation rateCardDentalWorkAssociation;
	private String processStatus;

	public DentalWork getDentalWork() {
		return dentalWork;
	}

	public void setDentalWork(DentalWork dentalWork) {
		this.dentalWork = dentalWork;
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

	public String getShade() {
		return shade;
	}

	public void setShade(String shade) {
		this.shade = shade;
	}

	public List<String> getMaterial() {
		return material;
	}

	public void setMaterial(List<String> material) {
		this.material = material;
	}

	public List<DentalStage> getDentalStagesForLab() {
		return dentalStagesForLab;
	}

	public void setDentalStagesForLab(List<DentalStage> dentalStagesForLab) {
		this.dentalStagesForLab = dentalStagesForLab;
	}

	public List<DentalStage> getDentalStagesForDoctor() {
		return dentalStagesForDoctor;
	}

	public void setDentalStagesForDoctor(List<DentalStage> dentalStagesForDoctor) {
		this.dentalStagesForDoctor = dentalStagesForDoctor;
	}

	public RateCardDentalWorkAssociation getRateCardDentalWorkAssociation() {
		return rateCardDentalWorkAssociation;
	}

	public void setRateCardDentalWorkAssociation(RateCardDentalWorkAssociation rateCardDentalWorkAssociation) {
		this.rateCardDentalWorkAssociation = rateCardDentalWorkAssociation;
	}

	public List<ImageURLResponse> getDentalImages() {
		return dentalImages;
	}

	public void setDentalImages(List<ImageURLResponse> dentalImages) {
		this.dentalImages = dentalImages;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	@Override
	public String toString() {
		return "DentalWorksSample [dentalWork=" + dentalWork + ", dentalToothNumbers=" + dentalToothNumbers
				+ ", dentalStagesForLab=" + dentalStagesForLab + ", etaInDate=" + etaInDate + ", etaInHour=" + etaInHour
				+ ", isCompleted=" + isCompleted + ", isUrgent=" + isUrgent + ", instructions=" + instructions
				+ ", occlusalStaining=" + occlusalStaining + ", ponticDesign=" + ponticDesign
				+ ", collarAndMetalDesign=" + collarAndMetalDesign + ", uniqueWorkId=" + uniqueWorkId
				+ ", dentalImages=" + dentalImages + ", dentalWorkCardValues=" + dentalWorkCardValues + ", shade="
				+ shade + ", material=" + material + ", dentalStagesForDoctor=" + dentalStagesForDoctor
				+ ", rateCardDentalWorkAssociation=" + rateCardDentalWorkAssociation + ", processStatus="
				+ processStatus + "]";
	}

}
