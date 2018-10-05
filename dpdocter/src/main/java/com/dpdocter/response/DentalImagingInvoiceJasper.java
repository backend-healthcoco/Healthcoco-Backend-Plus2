package com.dpdocter.response;

public class DentalImagingInvoiceJasper {
	private int sNo;
	private String serviceName;
	private String toothNumber;
	private String quadrant;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getToothNumber() {
		return toothNumber;
	}

	public void setToothNumber(String toothNumber) {
		this.toothNumber = toothNumber;
	}

	public String getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(String quadrant) {
		this.quadrant = quadrant;
	}

}
