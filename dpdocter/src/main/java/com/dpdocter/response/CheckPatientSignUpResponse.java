package com.dpdocter.response;

import java.util.Arrays;

public class CheckPatientSignUpResponse {

	private char[] password;

	private char[] salt;

	private boolean signedUp = false;

	public CheckPatientSignUpResponse() {
	}

	public CheckPatientSignUpResponse(char[] password, char[] salt, boolean signedUp) {
		this.password = password;
		this.salt = salt;
		this.signedUp = signedUp;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public char[] getSalt() {
		return salt;
	}

	public void setSalt(char[] salt) {
		this.salt = salt;
	}

	public boolean isSignedUp() {
		return signedUp;
	}

	public void setSignedUp(boolean signedUp) {
		this.signedUp = signedUp;
	}

	@Override
	public String toString() {
		return "CheckPatientSignUpResponse [password=" + Arrays.toString(password) + ", salt=" + Arrays.toString(salt)
				+ ", signedUp=" + signedUp + "]";
	}
}
