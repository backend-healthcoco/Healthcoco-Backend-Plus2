package com.dpdocter.request;

import java.util.LinkedHashMap;

import com.dpdocter.beans.AlignerDates;

public class OrthoEditProgressDatesRequest {
	private String progressId;
	private LinkedHashMap<Integer, AlignerDates> alignerDates;
	public String getProgressId() {
		return progressId;
	}
	public void setProgressId(String progressId) {
		this.progressId = progressId;
	}
	public LinkedHashMap<Integer, AlignerDates> getAlignerDates() {
		return alignerDates;
	}
	public void setAlignerDates(LinkedHashMap<Integer, AlignerDates> alignerDates) {
		this.alignerDates = alignerDates;
	}

}
