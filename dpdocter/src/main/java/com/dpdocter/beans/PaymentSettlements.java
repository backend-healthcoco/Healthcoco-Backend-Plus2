package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class PaymentSettlements extends GenericCollection {

private String entity;
	
	private Integer count;
	
	private List<PaymentSettlementItems>items;

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

	public List<PaymentSettlementItems> getItems() {
		return items;
	}

	public void setItems(List<PaymentSettlementItems> items) {
		this.items = items;
	}
}
