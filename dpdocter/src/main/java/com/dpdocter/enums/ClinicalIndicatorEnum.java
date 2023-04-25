package com.dpdocter.enums;

public enum ClinicalIndicatorEnum {
	RCT_FAILURE("RCT_FAILURE"), DRY_SOCKET("DRY_SOCKET"), DENTURE_FAILURE("DENTURE_FAILURE"),
	FRACTURE_OF_FILLING("FRACTURE_OF_FILLING"), INSTRUMENT_BROKEN_DURING_RCT("INSTRUMENT_BROKEN_DURING_RCT"),
	ONLAY_FAILURE("ONLAY_FAILURE"), RPD_FAILURE("RPD_FAILURE");

	private String type;

	private ClinicalIndicatorEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
