package com.dpdocter.beans;

public class NDHMRecordDataRepeat {

	private int frequency;
	private int period;
	private String periodUnit;
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getPeriodUnit() {
		return periodUnit;
	}
	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}
	@Override
	public String toString() {
		return "NDHMRecordDataRepeat [frequency=" + frequency + ", period=" + period + ", periodUnit=" + periodUnit
				+ "]";
	}
}
