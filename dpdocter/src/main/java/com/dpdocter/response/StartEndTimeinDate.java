package com.dpdocter.response;

import java.util.Date;

public class StartEndTimeinDate {

	private Date startDate;
	private Date endDate;

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

	@Override
	public String toString() {
		return "StartEndTimeinDate [startDate=" + startDate + ", endDate=" + endDate + "]";
	}

}
