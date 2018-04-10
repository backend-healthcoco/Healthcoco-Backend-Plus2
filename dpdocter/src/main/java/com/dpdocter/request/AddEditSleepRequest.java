package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.enums.SleepWhen;
import com.dpdocter.beans.Duration2;

public class AddEditSleepRequest {
	
	private Duration2 duration;
	private Date SleepFrom;
	private Date SleepTo;
	private SleepWhen sleepWhen;
	private String Id;
	private String patientId;
	
	public Duration2 getDuration() {
		return duration;
	}
	public void setDuration(Duration2 duration) {
		this.duration = duration;
	}
	public Date getSleepFrom() {
		return SleepFrom;
	}
	public void setSleepFrom(Date sleepFrom) {
		SleepFrom = sleepFrom;
	}
	public Date getSleepTo() {
		return SleepTo;
	}
	public void setSleepTo(Date sleepTo) {
		SleepTo = sleepTo;
	}
	public SleepWhen getSleepWhen() {
		return sleepWhen;
	}
	public void setSleepWhen(SleepWhen sleepWhen) {
		this.sleepWhen = sleepWhen;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	
	@Override
	public String toString() {
		return "AddEditSleepRequest [duration=" + duration + ", SleepFrom=" + SleepFrom + ", SleepTo=" + SleepTo
				+ ", sleepWhen=" + sleepWhen + ", Id=" + Id + ", patientId=" + patientId + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
