package com.dpdocter.beans;

import java.util.List;

public class PaymentSettlements {

	private String entity;
	
	private Integer count;
	
	private List<PaymentSettlementsItems>items;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<PaymentSettlementsItems> getItems() {
		return items;
	}

	public void setItems(List<PaymentSettlementsItems> items) {
		this.items = items;
	}
	
	
	
}
