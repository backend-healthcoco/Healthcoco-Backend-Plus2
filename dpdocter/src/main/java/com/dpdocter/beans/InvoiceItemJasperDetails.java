package com.dpdocter.beans;

import java.util.Date;

public class InvoiceItemJasperDetails {
	private int no;

	private String serviceName;

	private String quantity;

	private String cost;

	private String tax;

	private String discount;

	private String status;

	private String total;

	

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "InvoiceItemJasperDetails [no=" + no + ", serviceName=" + serviceName + ", quantity=" + quantity
				+ ", cost=" + cost + ", tax=" + tax + ", discount=" + discount + ", status=" + status + ", total="
				+ total + "]";
	}

	
	
	
	

}
