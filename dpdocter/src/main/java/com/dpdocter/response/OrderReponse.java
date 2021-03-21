package com.dpdocter.response;

import java.util.List;

public class OrderReponse {

	private String id;
	
	private String entity;
	
	private Integer amount;
	
	private String currency;
	
	private String receipt;
	
	private Integer amount_paid;
	
	private Integer amount_due;
	
	private String status;
	
	private Long created_at;
	
	private String offer_id;
	
	private Integer attempts;
	
	private List<String>notes;

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

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public Integer getAmount_paid() {
		return amount_paid;
	}

	public void setAmount_paid(Integer amount_paid) {
		this.amount_paid = amount_paid;
	}

	public Long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Long created_at) {
		this.created_at = created_at;
	}

	public Integer getAmount_due() {
		return amount_due;
	}

	public void setAmount_due(Integer amount_due) {
		this.amount_due = amount_due;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOffer_id() {
		return offer_id;
	}

	public void setOffer_id(String offer_id) {
		this.offer_id = offer_id;
	}

	public Integer getAttempts() {
		return attempts;
	}

	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	
	

}
