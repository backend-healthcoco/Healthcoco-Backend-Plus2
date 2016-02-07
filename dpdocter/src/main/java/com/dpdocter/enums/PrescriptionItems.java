package com.dpdocter.enums;

public enum PrescriptionItems {

    DRUG("DRUG"), DRUGTYPE("DRUGTYPE"), DRUGDIRECTION("DRUGDIRECTION"), DRUGDOSAGE("DRUGDOSAGE"), DRUGDURATIONUNIT("DRUGDURATIONUNIT"), DRUGSTRENGTHUNIT(
	    "DRUGSTRENGTHUNIT"), LABTEST("LABTEST"), DIAGNOSTICTEST("DIAGNOSTICTEST");

    private String item;

    PrescriptionItems(String item) {
	this.item = item;
    }

    public String getItem() {
	return item;
    }

}
