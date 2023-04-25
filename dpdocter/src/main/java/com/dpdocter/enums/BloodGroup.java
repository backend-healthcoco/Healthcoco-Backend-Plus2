package com.dpdocter.enums;

public enum BloodGroup {

	A_NEG("A-"), A_POS("A+"), A_ONE_NEG("A1-"), A_ONE_POS("A1+"), A_ONE_B_NEG("A1B-"), A_ONE_B_POS("A1B+"),
	A_TWO_NEG("A2-"), A_TWO_POS("A2+"), A_TWO_B_NEG("A2B-"), A_TWO_B_POS("A2B+"), A_B_NEG("AB-"), A_B_POS("AB+"),
	B_NEG(" B-"), B_POS("B+"), B_ONE_POS("B1+"), O_NEG("O-"), O_POS("O+");

	private String group;

	public String getGroup() {
		return group;
	}

	private BloodGroup(String group) {
		this.group = group;
	}

}
