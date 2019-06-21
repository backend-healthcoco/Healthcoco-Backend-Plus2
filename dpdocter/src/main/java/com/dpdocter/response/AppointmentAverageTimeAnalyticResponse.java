package com.dpdocter.response;

import java.util.Date;

public class AppointmentAverageTimeAnalyticResponse {

	private Date date;

	private Long averageWaitingTime = 0l;

	private Long averageEngagedTime = 0l;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getAverageWaitingTime() {
		return averageWaitingTime;
	}

	public Long getAverageEngagedTime() {
		return averageEngagedTime;
	}

	public void setAverageEngagedTime(Long averageEngagedTime) {
		this.averageEngagedTime = averageEngagedTime;
	}

	public void setAverageWaitingTime(Long averageWaitingTime) {
		this.averageWaitingTime = averageWaitingTime;
	}

	@Override
	public String toString() {
		return "AppointmentAverageTimeAnalyticResponse [date=" + date + ", averageWaitingTime=" + averageWaitingTime
				+ ", averageEngagedTime=" + averageEngagedTime + ", averageStayTime=" + "]";
	}
}
