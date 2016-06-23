package com.dpdocter.elasticsearch.beans;

import java.util.List;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.Day;

public class ESWorkingSchedule {

    private Day workingDay;

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
	return "{workingDay=" + workingDay + ", workingHours=" + workingHours + "}";
    }
}
