package com.dpdocter.request;

public class VerifyUnlockPatientRequest {

	public static enum FlagEnum {
		VERIFY("VERIFY"), UNLOCK("UNLOCK");

		private String flag;

		private FlagEnum(String flag) {
			this.flag = flag;
		}

		public String getFlag() {
			return flag;
		}

	}

	private String mobileNumber;

	private String name;

	private FlagEnum verifyOrUnlock;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FlagEnum getVerifyOrUnlock() {
		return verifyOrUnlock;
	}

	public void setVerifyOrUnlock(FlagEnum verifyOrUnlock) {
		this.verifyOrUnlock = verifyOrUnlock;
	}

	@Override
	public String toString() {
		return "VerifyUnlockPatientRequest [mobileNumber=" + mobileNumber + ", name=" + name + ", verifyOrUnlock="
				+ verifyOrUnlock + "]";
	}

}
