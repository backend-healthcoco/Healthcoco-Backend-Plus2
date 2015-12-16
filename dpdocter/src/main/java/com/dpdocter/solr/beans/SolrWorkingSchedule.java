package com.dpdocter.solr.beans;

import java.util.List;

import com.dpdocter.beans.WorkingHours;

public class SolrWorkingSchedule {

	private String workingDay;

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
	return "workingSchedule [workingHours=" + workingHours + "]";
    }

}
