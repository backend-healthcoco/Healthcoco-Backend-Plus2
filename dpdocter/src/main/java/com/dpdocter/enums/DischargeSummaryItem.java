package com.dpdocter.enums;

public enum DischargeSummaryItem {

	BABY_NOTES("BABY_NOTES"), OPERATION_NOTES("OPERATION_NOTES"), LABOUR_NOTES("LABOUR_NOTES");
	private String type;

	public String getType() {
		return type;
	}

	private DischargeSummaryItem(String type) {
		this.type = type;
	}
}
