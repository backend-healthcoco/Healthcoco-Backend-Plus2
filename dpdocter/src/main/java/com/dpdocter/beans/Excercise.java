package com.dpdocter.beans;

import com.dpdocter.enums.ExcerciseType;

public class Excercise {
	private ExcerciseType type;
	private Integer minPerDay = 0;
	private Integer timePerWeek = 0;

	public ExcerciseType getType() {
		return type;
	}

	public void setType(ExcerciseType type) {
		this.type = type;
	}

	public Integer getMinPerDay() {
		return minPerDay;
	}

	public void setMinPerDay(Integer minPerDay) {
		this.minPerDay = minPerDay;
	}

	public Integer getTimePerWeek() {
		return timePerWeek;
	}

	public void setTimePerWeek(Integer timePerWeek) {
		this.timePerWeek = timePerWeek;
	}

}
