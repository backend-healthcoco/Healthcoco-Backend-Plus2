package com.dpdocter.beans;

public class OnLinkConfirm {

	private String id;
	
	private String referenceNumber;
	
	private String display;
	
	private CareContext careContexts;
	
	private NdhmErrorObject error;
	
	private FetchResponse resp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public CareContext getCareContexts() {
		return careContexts;
	}

	public void setCareContexts(CareContext careContexts) {
		this.careContexts = careContexts;
	}

	public NdhmErrorObject getError() {
		return error;
	}

	public void setError(NdhmErrorObject error) {
		this.error = error;
	}

	public FetchResponse getResp() {
		return resp;
	}

	public void setResp(FetchResponse resp) {
		this.resp = resp;
	}
	
	
}
