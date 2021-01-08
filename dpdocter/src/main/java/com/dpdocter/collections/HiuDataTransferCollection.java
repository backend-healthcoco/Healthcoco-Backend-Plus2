package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.request.EntriesDataTransferRequest;
import com.dpdocter.request.KeyMaterialRequestDataFlow;

@Document(collection = "hiu_data_transfer_cl")
public class HiuDataTransferCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private Integer pageNumber;
	@Field
	private Integer pageCount;
	@Field
	private String transactionId;
	@Field
	private List<EntriesDataTransferRequest> entries;
	@Field
	private KeyMaterialRequestDataFlow keyMaterial;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getPageCount() {
		return pageCount;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public List<EntriesDataTransferRequest> getEntries() {
		return entries;
	}
	public void setEntries(List<EntriesDataTransferRequest> entries) {
		this.entries = entries;
	}
	public KeyMaterialRequestDataFlow getKeyMaterial() {
		return keyMaterial;
	}
	public void setKeyMaterial(KeyMaterialRequestDataFlow keyMaterial) {
		this.keyMaterial = keyMaterial;
	}
	
	
}
