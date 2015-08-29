package com.dpdocter.beans;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class DOB {
    private int days;

    private int months;

    private int years;

    private int age;

    public DOB() {
	super();
    }

    public DOB(int days, int months, int years, int age) {
	super();
	this.days = days;
	this.months = months;
	this.years = years;
	this.age = age;
    }

    public DOB(int days, int months, int years) {
    	super();
    	this.days = days;
    	this.months = months;
    	this.years = years;
	}

	public int getDays() {
	return days;
    }

    public void setDays(int days) {
	this.days = days;
    }

    public int getMonths() {
	return months;
    }

    public void setMonths(int months) {
	this.months = months;
    }

    public int getYears() {
	return years;
    }

    public void setYears(int years) {
	this.years = years;
    }

    public int getAge() {
	if (this.age == 0) {
	    LocalDate birthdate = new LocalDate(this.years, this.months, this.days);
	    LocalDate now = new LocalDate();
	    Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
	    this.age = period.getYears();
	}
	return age;
    }

    public void setAge(int age) {
	this.age = age;
    }

    @Override
    public String toString() {
	return "DOB [days=" + days + ", months=" + months + ", years=" + years + "]";
    }

}
