package com.dpdocter.enums;

public enum DateFilter {

	DAY("DAY"), MONTH("MONTH"), YEAR("YEAR"), WEEK("WEEK"), TODAY("TODAY"), FUTURE("FUTURE"), PAST("PAST");

	private String filter;

	private DateFilter(String filter) {
		this.filter = filter;
	}

	public String getFilter() {
		return filter;
	}
}
