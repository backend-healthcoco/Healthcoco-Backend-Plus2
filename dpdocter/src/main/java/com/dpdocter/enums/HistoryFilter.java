package com.dpdocter.enums;

public enum HistoryFilter {
	CLINICAL_NOTES("CLINICAL_NOTES"), REPORTS("REPORTS"), PRESCRIPTIONS("PRESCRIPTIONS"), ALL("ALL");

	private String filter;

	HistoryFilter(String filter) {
		this.filter = filter;
	}

	public String getFilter() {
		return filter;
	}

}
