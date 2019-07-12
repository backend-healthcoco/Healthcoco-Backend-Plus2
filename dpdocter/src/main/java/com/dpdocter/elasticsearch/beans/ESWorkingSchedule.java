package com.dpdocter.elasticsearch.beans;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.WorkingHours;

public class ESWorkingSchedule {

	@Field(type = FieldType.Text)
    private String workingDay;

    @Field(type = FieldType.Nested)
    private List<WorkingHours> workingHours;

    public String getWorkingDay() {
	return workingDay;
    }

    public void setWorkingDay(String workingDay) {
	this.workingDay = workingDay;
    }

    public List<WorkingHours> getWorkingHours() {
	return workingHours;
    }

    public void setWorkingHours(List<WorkingHours> workingHours) {
	this.workingHours = workingHours;
    }

	@Override
	public String toString() {
		return "ESWorkingSchedule [workingDay=" + workingDay + ", workingHours=" + workingHours + "]";
	}
}
