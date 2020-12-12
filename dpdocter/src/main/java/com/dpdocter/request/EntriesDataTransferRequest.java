package com.dpdocter.request;

public class EntriesDataTransferRequest {

	private String link="https://data-from.net/sa2321afaf12e13";
	private String content;
	private String media="application/fhir+json";
	private String checksum;
	private String careContextReference;
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public String getCareContextReference() {
		return careContextReference;
	}
	public void setCareContextReference(String careContextReference) {
		this.careContextReference = careContextReference;
	}

	
}

