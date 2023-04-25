package com.dpdocter.enums;

public enum ResistanceTrainingProgramType {
	LESS_THAN_ONE_MONTH("LESS_THAN_ONE_MONTH"), SIX_MONTH_TO_ONE_YEAR("SIX_MONTH_TO_ONE_YEAR"),
	ONE_TO_TWO_YEAR("ONE_TO_TWO_YEAR"), TWO_TO_FOUR_YEAR("TWO_TO_FOUR_YEAR"),
	MORE_THAN_FIVE_YEAR("MORE_THAN_FIVE_YEAR");

	private String type;

	public String getType() {
		return type;
	}

	private ResistanceTrainingProgramType(String type) {
		this.type = type;
	}
}
