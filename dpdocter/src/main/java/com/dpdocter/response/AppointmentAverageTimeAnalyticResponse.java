package com.dpdocter.response;

import java.util.Date;

public class AppointmentAverageTimeAnalyticResponse {

	private Date date;
	
	private Long averageWaitingTime;
	
	private Long averageEngagedTime;
	
	private Long averageStayTime;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getAverageWaitingTime() {
		return averageWaitingTime;
	}

	public void setAverageWaitingTime(Long averageWaitingTime) {
		this.averageWaitingTime = averageWaitingTime;
		this.averageStayTime = averageWaitingTime + this.averageEngagedTime;
	}

	public Long getAverageEngagedTime() {
		return averageEngagedTime;
	}

	public void setAverageEngagedTime(Long averageEngagedTime) {
		this.averageEngagedTime = averageEngagedTime;
		this.averageStayTime = averageWaitingTime + this.averageEngagedTime;
	}

	public Long getAverageStayTime() {
		return averageStayTime;
	}

	public void setAverageStayTime(Long averageStayTime) {
		this.averageStayTime = averageStayTime;
	}

	@Override
	public String toString() {
		return "AppointmentAverageTimeAnalyticResponse [date=" + date + ", averageWaitingTime=" + averageWaitingTime
				+ ", averageEngagedTime=" + averageEngagedTime + ", averageStayTime=" + averageStayTime + "]";
	}
}
