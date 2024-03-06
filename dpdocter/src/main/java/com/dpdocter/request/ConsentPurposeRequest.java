package com.dpdocter.request;

public class ConsentPurposeRequest {
    private String text;
    private String code;
    private String refUri;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRefUri() {
		return refUri;
	}
	public void setRefUri(String refUri) {
		this.refUri = refUri;
	}
    
}
