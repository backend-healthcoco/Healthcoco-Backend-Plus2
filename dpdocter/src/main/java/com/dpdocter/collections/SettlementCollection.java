package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.response.PayLoad;

public class SettlementCollection extends GenericCollection {

	private ObjectId id;
	
	private String entity;
	
	private String account_id;
	
	private String event;
	
	private List<String> contains;
	
	private PayLoad payload;
	
	private Long created_at;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public List<String> getContains() {
		return contains;
	}

	public void setContains(List<String> contains) {
		this.contains = contains;
	}

	public PayLoad getPayload() {
		return payload;
	}

	public void setPayload(PayLoad payload) {
		this.payload = payload;
	}

	public Long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Long created_at) {
		this.created_at = created_at;
	}
	
	

}
