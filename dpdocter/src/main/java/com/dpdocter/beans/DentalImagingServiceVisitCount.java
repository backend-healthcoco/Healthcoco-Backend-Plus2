package com.dpdocter.beans;

public class DentalImagingServiceVisitCount {

	private String serviceName;
	private Integer count = 0;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
