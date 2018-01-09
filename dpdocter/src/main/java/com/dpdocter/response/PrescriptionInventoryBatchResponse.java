package com.dpdocter.response;

public class PrescriptionInventoryBatchResponse {

	private String id;
	private String batchName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	@Override
	public String toString() {
		return "PrescriptionInventoryBatchResponse [id=" + id + ", batchName=" + batchName + "]";
	}

}
