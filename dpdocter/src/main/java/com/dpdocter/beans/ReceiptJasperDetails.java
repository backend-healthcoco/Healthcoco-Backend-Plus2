package com.dpdocter.beans;

public class ReceiptJasperDetails {

	private String date;
	
	private String procedure;
	
	private String total;
	
	private String paid;
	
	private String balance;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "ReceiptJasperDetails [date=" + date + ", procedure=" + procedure
				+ ", total=" + total + ", paid=" + paid + ", balance=" + balance + "]";
	}
}
