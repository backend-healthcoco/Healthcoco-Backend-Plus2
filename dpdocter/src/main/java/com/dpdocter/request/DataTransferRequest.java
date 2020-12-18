package com.dpdocter.request;

import java.util.List;

public class DataTransferRequest {

	private Integer pageNumber;
	private Integer pageCount;
	private String transactionId;
	private List<EntriesDataTransferRequest> entries;
	private KeyMaterialRequestDataFlow keyMaterial;
	
	
	
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
