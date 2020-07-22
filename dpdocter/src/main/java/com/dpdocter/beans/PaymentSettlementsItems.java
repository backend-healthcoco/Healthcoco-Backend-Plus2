package com.dpdocter.beans;

public class PaymentSettlementsItems {

	private String id;
	
	private String entity;
	
	
	
	private Integer amount;
	
	private String status;
	
	private Integer fees;
	
	private Integer tax;
	
	
	
	private Integer created_at;
	
	
	
	private String utr;
	
	
	
	
	

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	

	

	public Integer getTax() {
		return tax;
	}

	public void setTax(Integer tax) {
		this.tax = tax;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getFees() {
		return fees;
	}

	public void setFees(Integer fees) {
		this.fees = fees;
	}

	public Integer getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Integer created_at) {
		this.created_at = created_at;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

		
	
	



	
	


}
