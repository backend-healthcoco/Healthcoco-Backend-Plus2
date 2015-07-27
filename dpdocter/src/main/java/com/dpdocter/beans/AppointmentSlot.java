package com.dpdocter.beans;

import com.dpdocter.enums.TimeUnit;

public class AppointmentSlot {
    private float time;

    private TimeUnit timeUnit;

    public float getTime() {
	return time;
    }

    public void setTime(float time) {
	this.time = time;
    }

    public TimeUnit getTimeUnit() {
	return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
	this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
	return "AppointmentSlot [time=" + time + ", timeUnit=" + timeUnit + "]";
    }

}
