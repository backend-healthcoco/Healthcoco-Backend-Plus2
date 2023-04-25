package com.dpdocter.enums;

public enum PrintFilter {
	ALL("ALL"), PAGESETUP("PAGESETUP"), HEADERSETUP("HEADERSETUP"), FOOTERSETUP("FOOTERSETUP");

	private String filter;

	public String getFilter() {
		return filter;
	}

	private PrintFilter(String filter) {
		this.filter = filter;
	}

}
