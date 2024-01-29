package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.enums.TypeOfAligner;

public class OrthoEditProgressDatesRequest {
	private String progressId;
	private TypeOfAligner typeOfAligner;
	private Integer alignerNo;

	private Date endDate;

	public String getProgressId() {
		return progressId;
	}

	public void setProgressId(String progressId) {
		this.progressId = progressId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public TypeOfAligner getTypeOfAligner() {
		return typeOfAligner;
	}

	public void setTypeOfAligner(TypeOfAligner typeOfAligner) {
		this.typeOfAligner = typeOfAligner;
	}

	public Integer getAlignerNo() {
		return alignerNo;
	}

	public void setAlignerNo(Integer alignerNo) {
		this.alignerNo = alignerNo;
	}

}
