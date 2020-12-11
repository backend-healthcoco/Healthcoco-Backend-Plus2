package com.dpdocter.request;

public class HiRequestNdhmDataFlow {

	private ConsentDataFlowRequest consent;
	private DateRangeDataFlow dateRange;
	private String dataPushUrl;
	private KeyMaterialRequestDataFlow keyMaterial;
	public ConsentDataFlowRequest getConsent() {
		return consent;
	}
	public void setConsent(ConsentDataFlowRequest consent) {
		this.consent = consent;
	}
	public DateRangeDataFlow getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRangeDataFlow dateRange) {
		this.dateRange = dateRange;
	}
	public String getDataPushUrl() {
		return dataPushUrl;
	}
	public void setDataPushUrl(String dataPushUrl) {
		this.dataPushUrl = dataPushUrl;
	}
	public KeyMaterialRequestDataFlow getKeyMaterial() {
		return keyMaterial;
	}
	public void setKeyMaterial(KeyMaterialRequestDataFlow keyMaterial) {
		this.keyMaterial = keyMaterial;
	}
	
	
}
