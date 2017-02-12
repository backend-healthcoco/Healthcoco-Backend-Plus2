package com.dpdocter.beans;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.enums.Day;

public class LocaleWorkingSchedule {
	private Day workingDay;

	@Field(type = FieldType.Nested)
	private List<LocaleWorkingHours> workingHours;

	public Day getWorkingDay() {
		return workingDay;
	}

	public void setWorkingDay(Day workingDay) {
		this.workingDay = workingDay;
	}

	public List<LocaleWorkingHours> getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(List<LocaleWorkingHours> workingHours) {
		this.workingHours = workingHours;
	}

	@Override
	public String toString() {
		return "WorkingSchedule [workingDay=" + workingDay + ", workingHours=" + workingHours + "]";
	}
}
