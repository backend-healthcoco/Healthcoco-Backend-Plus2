package com.dpdocter.beans;

import java.util.Date;
import java.util.LinkedHashMap;

public class AlignerProgressDetail {
	private String progressId;
	private String planId;
	private Date startDate;
	private Date endDate;
	private Date nextStartDate;
	private Integer wearingAligner;
	private Integer treatmentDays;

	private LinkedHashMap<Integer, AlignerDates> alignerDates;

	public String getProgressId() {
		return progressId;
	}

	public void setProgressId(String progressId) {
		this.progressId = progressId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getNextStartDate() {
		return nextStartDate;
	}

	public void setNextStartDate(Date nextStartDate) {
		this.nextStartDate = nextStartDate;
	}

	public Integer getWearingAligner() {
		return wearingAligner;
	}

	public void setWearingAligner(Integer wearingAligner) {
		this.wearingAligner = wearingAligner;
	}

	public Integer getTreatmentDays() {
		return treatmentDays;
	}

	public void setTreatmentDays(Integer treatmentDays) {
		this.treatmentDays = treatmentDays;
	}

	public LinkedHashMap<Integer, AlignerDates> getAlignerDates() {
		return alignerDates;
	}

	public void setAlignerDates(LinkedHashMap<Integer, AlignerDates> alignerDates) {
		this.alignerDates = alignerDates;
	}

}
