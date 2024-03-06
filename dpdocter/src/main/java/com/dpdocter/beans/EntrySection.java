package com.dpdocter.beans;

import java.util.List;

public class EntrySection {

	private String title;
	
	private EntrySectionCode code;
	
	private List<SectionEntry> entry;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public EntrySectionCode getCode() {
		return code;
	}

	public void setCode(EntrySectionCode code) {
		this.code = code;
	}

	public List<SectionEntry> getEntry() {
		return entry;
	}

	public void setEntry(List<SectionEntry> entry) {
		this.entry = entry;
	}
	
	
	
}
