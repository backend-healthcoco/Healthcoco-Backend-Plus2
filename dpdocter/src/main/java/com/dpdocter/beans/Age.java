package com.dpdocter.beans;

public class Age {

	private int days;

    private int months;

    private int years;

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

	@Override
	public String toString() {
		return "Age [days=" + days + ", months=" + months + ", years=" + years + "]";
	}
}
