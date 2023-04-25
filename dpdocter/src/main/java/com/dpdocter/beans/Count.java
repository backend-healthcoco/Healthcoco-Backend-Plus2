package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.CountFor;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Count {
	private CountFor countFor;

	private int value;

	public CountFor getCountFor() {
		return countFor;
	}

	public void setCountFor(CountFor countFor) {
		this.countFor = countFor;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Count [countFor=" + countFor + ", value=" + value + "]";
	}

}
