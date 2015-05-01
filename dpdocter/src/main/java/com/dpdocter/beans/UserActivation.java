package com.dpdocter.beans;

public class UserActivation {

	private boolean isActivated;

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	@Override
	public String toString() {
		return "UserActivation [isActivated=" + isActivated + "]";
	}

}
