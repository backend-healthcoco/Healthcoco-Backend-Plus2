package com.dpdocter.beans;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.enums.Day;

public class OfferSchedule {
	private Day workingDay;

	@Field(type = FieldType.Object)
	private WorkingHours workingHours;

	public Day getWorkingDay() {
		return workingDay;
	}

	public void setWorkingDay(Day workingDay) {
		this.workingDay = workingDay;
	}

	public WorkingHours getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(WorkingHours workingHours) {
		this.workingHours = workingHours;
	}

}
