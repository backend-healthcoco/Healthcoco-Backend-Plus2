package com.dpdocter.response;

import java.util.Date;

public class AppointmentAnalyticGroupWiseResponse {

	private Date date;

	private Long count;

	private String groupName;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "AppointmentCountAnalyticResponse [date=" + date + ", count=" + count + ", groupName=" + groupName + "]";
	}

}
