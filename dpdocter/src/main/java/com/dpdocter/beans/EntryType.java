package com.dpdocter.beans;

import java.util.List;

public class EntryType {

	private List<EntryCoding> coding; 
	
	private String text;

	public List<EntryCoding> getCoding() {
		return coding;
	}

	public void setCoding(List<EntryCoding> coding) {
		this.coding = coding;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
