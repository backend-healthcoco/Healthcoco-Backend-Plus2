package com.dpdocter.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestWrapperData {
	@JsonProperty("$return_value")
	private ReturnValue returnValue;

	public ReturnValue getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(ReturnValue returnValue) {
		this.returnValue = returnValue;
	}

}
