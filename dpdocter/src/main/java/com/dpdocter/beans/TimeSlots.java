package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.Day;

public class TimeSlots {

    private Day day;

    private List<Slot> slots;

    public TimeSlots() {
    }

    public TimeSlots(Day day, List<Slot> slots) {
	this.day = day;
	this.slots = slots;
    }

    public Day getDay() {
	return day;
    }

    public void setDay(Day day) {
	this.day = day;
    }

    public List<Slot> getSlots() {
	return slots;
    }

    public void setSlots(List<Slot> slots) {
	this.slots = slots;
    }

    @Override
    public String toString() {
	return "TimeSlots [day=" + day + ", slots=" + slots + "]";
    }

}
