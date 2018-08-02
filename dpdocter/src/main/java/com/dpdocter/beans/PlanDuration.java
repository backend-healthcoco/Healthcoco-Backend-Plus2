package com.dpdocter.beans;

import com.dpdocter.enums.DurationUnitEnum;

public class PlanDuration {

	private Double value = 0.0;
	private DurationUnitEnum durationUnit = DurationUnitEnum.MONTH;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public DurationUnitEnum getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(DurationUnitEnum durationUnit) {
		this.durationUnit = durationUnit;
	}

}
