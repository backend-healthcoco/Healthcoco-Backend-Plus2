package com.dpdocter.beans;

public class DentalWorkInvoiceJasperResponse {
	private int sNo;
	private String orderDate;
	private String patientName;
	private String material;
	private String teethNo;
	private Integer units=0;
	private double rate;
	private double total;
	public String getTeethNo() {
		return teethNo;
	}

	public void setTeethNo(String teethNo) {
		this.teethNo = teethNo;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "DentalWorkInvoiceJasperResponse [sNo=" + sNo + ", orderDate=" + orderDate + ", patientName="
				+ patientName + ", material=" + material + ", teethNo=" + teethNo + ", units=" + units + ", rate="
				+ rate + ", total=" + total + "]";
	}

}
