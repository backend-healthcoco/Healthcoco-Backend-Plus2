package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DOB {
	private int days;

	private int months;

	private int years;

	private Age age;

	public DOB() {
		super();
	}

	public DOB(int days, int months, int years, Age age) {
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
		this.age = getAge();
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

	public Age getAge() {
		if (this.age == null && this.years != 0 && this.months != 0 && this.days != 0) {
			LocalDate birthdate = new LocalDate(this.years, this.months, this.days);
			LocalDate now = new LocalDate();
			Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
			this.age = new Age();
			this.age.setYears(period.getYears());
			this.age.setMonths(period.getMonths());
			this.age.setDays(period.getDays());
		}
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "DOB [days=" + days + ", months=" + months + ", years=" + years + "]";
	}

}
