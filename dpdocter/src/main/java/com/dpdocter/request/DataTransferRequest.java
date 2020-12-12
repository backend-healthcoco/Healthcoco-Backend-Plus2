package com.dpdocter.request;

import java.util.List;

public class DataTransferRequest {

	private String pageNumber;
	private String pageCount;
	private String transactionId;
	private List<EntriesDataTransferRequest> entries;
	private KeyMaterialRequestDataFlow keyMaterial;
	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getPageCount() {
		return pageCount;
	}
	public void setPageCount(String pageCount) {
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
