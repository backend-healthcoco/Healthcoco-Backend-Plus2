package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.enums.Day;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class WorkingSchedule {
    private Day workingDay;

    @Field(type = FieldType.Nested)
    private List<WorkingHours> workingHours;

    public Day getWorkingDay() {
	return workingDay;
    }

    public void setWorkingDay(Day workingDay) {
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
	return "WorkingSchedule [workingDay=" + workingDay + ", workingHours=" + workingHours + "]";
    }
}
