package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PaymentSettlementItems;

@Document(collection = "payment_settlement_cl")
public class PaymentSettlementCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private String entity;
	
	@Field
	private Integer count;
	
	@Field
	private List<PaymentSettlementItems>items;

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
