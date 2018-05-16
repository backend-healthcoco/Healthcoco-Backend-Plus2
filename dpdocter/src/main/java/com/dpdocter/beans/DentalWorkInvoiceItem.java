package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.request.DentalStageRequest;

public class DentalWorkInvoiceItem {

	private DentalWork dentalWork;
	private List<DentalToothNumber> dentalToothNumbers;
	private List<DentalStageRequest> dentalStagesForLab;
	private Long etaInDate;
	private Integer etaInHour;
	private List<DentalWorkCardValue> dentalWorkCardValues;
	private List<DentalStageRequest> dentalStagesForDoctor;

	public DentalWork getDentalWork() {
		return dentalWork;
	}

	public void setDentalWork(DentalWork dentalWork) {
		this.dentalWork = dentalWork;
	}

	public List<DentalToothNumber> getDentalToothNumbers() {
		return dentalToothNumbers;
	}

	public void setDentalToothNumbers(List<DentalToothNumber> dentalToothNumbers) {
		this.dentalToothNumbers = dentalToothNumbers;
	}

	public List<DentalStageRequest> getDentalStagesForLab() {
		return dentalStagesForLab;
	}

	public void setDentalStagesForLab(List<DentalStageRequest> dentalStagesForLab) {
		this.dentalStagesForLab = dentalStagesForLab;
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

	public List<DentalWorkCardValue> getDentalWorkCardValues() {
		return dentalWorkCardValues;
	}

	public void setDentalWorkCardValues(List<DentalWorkCardValue> dentalWorkCardValues) {
		this.dentalWorkCardValues = dentalWorkCardValues;
	}

	public List<DentalStageRequest> getDentalStagesForDoctor() {
		return dentalStagesForDoctor;
	}

	public void setDentalStagesForDoctor(List<DentalStageRequest> dentalStagesForDoctor) {
		this.dentalStagesForDoctor = dentalStagesForDoctor;
	}

}
