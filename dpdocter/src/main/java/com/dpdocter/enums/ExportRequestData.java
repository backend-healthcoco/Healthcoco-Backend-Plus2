package com.dpdocter.enums;

public enum ExportRequestData {
    CONTACTS("CONTACTS"), APPOINTMENTS("APPOINTMENTS"), CLINICAL_NOTES("CLINICAL_NOTES"), PRESCRIPTIONS("PRESCRIPTIONS");

    private String dataType;

    ExportRequestData(String dataType) {
	this.dataType = dataType;
    }

    public String getDataType() {
	return dataType;
    }

}
