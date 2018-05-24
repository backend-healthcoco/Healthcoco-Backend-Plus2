package com.dpdocter.beans;

public class DentalWorkInvoiceJasperResponse {
	private int sNo;
	private String OrderDate;
	private String patientName;
	private String product;
	private String topLeft;
	private String topRight;
	private String downLeft ;
	private String downRight; 
	private int units;
	private double rate;
	private double total;
	public int getsNo() {
		return sNo;
	}
	public void setsNo(int sNo) {
		this.sNo = sNo;
	}
	public String getOrderDate() {
		return OrderDate;
	}
	public void setOrderDate(String orderDate) {
		OrderDate = orderDate;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getTopLeft() {
		return topLeft;
	}
	public void setTopLeft(String topLeft) {
		this.topLeft = topLeft;
	}
	public String getTopRight() {
		return topRight;
	}
	public void setTopRight(String topRight) {
		this.topRight = topRight;
	}
	public String getDownLeft() {
		return downLeft;
	}
	public void setDownLeft(String downLeft) {
		this.downLeft = downLeft;
	}
	public String getDownRight() {
		return downRight;
	}
	public void setDownRight(String downRight) {
		this.downRight = downRight;
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
		return "DentalWorkInvoiceJasperResponse [sNo=" + sNo + ", OrderDate=" + OrderDate + ", patientName="
				+ patientName + ", product=" + product + ", topLeft=" + topLeft + ", topRight=" + topRight
				+ ", downLeft=" + downLeft + ", downRight=" + downRight + ", units=" + units + ", rate=" + rate
				+ ", total=" + total + ", getsNo()=" + getsNo() + ", getOrderDate()=" + getOrderDate()
				+ ", getPatientName()=" + getPatientName() + ", getProduct()=" + getProduct() + ", getTopLeft()="
				+ getTopLeft() + ", getTopRight()=" + getTopRight() + ", getDownLeft()=" + getDownLeft()
				+ ", getDownRight()=" + getDownRight() + ", getUnits()=" + getUnits() + ", getRate()=" + getRate()
				+ ", getTotal()=" + getTotal() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
	

}
