package com.dpdocter.enums;

public enum SpecialityTypeEnum {

	GENERAL("GENERAL"), OPHTHALMOLOGIST("OPHTHALMOLOGIST"), PEDIATRICIAN("PEDIATRICIAN"),
	GYNAECOLOGIST("GYNAECOLOGIST"), EMPTY("EMPTY");

	private String specialityType;

	private SpecialityTypeEnum(String specialityType) {
		this.specialityType = specialityType;
	}

	public String getSpecialityType() {
		return specialityType;
	}

}
