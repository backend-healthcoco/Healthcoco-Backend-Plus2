package com.dpdocter.enums;

public enum VitalSignPermissions {

	PULSE("PULSE"), BREATHING("BREATHING"), BLOODPRESSURE("BLOODPRESSURE"), HEIGHT("HEIGHT"), TEMPERATURE("TEMPERATURE"), WEIGHT(
			"WEIGHT"), SPO2("SPO2") , BMI("BMI") , BSA("BSA"),IBP("IBP"),CVP("CVP"),FIO2("FIO2"),VENHLATORMODE("VENHLATORMODE"),URINEOUTPUT("URINEOUTPUT"),FEEDING("FEEDING"),OTHERVITALSIGNS("OTHERVITALSIGNS");

	private String permission;

	public String getPermission() {
		return permission;
	}

	private VitalSignPermissions(String permission) {
		this.permission = permission;
	}

}
