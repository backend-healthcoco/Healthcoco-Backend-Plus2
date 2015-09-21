package com.dpdocter.beans;

public class FooterSetup {

	private Boolean customFooter;
	
	private Boolean showSignature;
	
	private String bottomText;

	public Boolean getCustomFooter() {
		return customFooter;
	}

	public void setCustomFooter(Boolean customFooter) {
		this.customFooter = customFooter;
	}

	public Boolean getShowSignature() {
		return showSignature;
	}

	public void setShowSignature(Boolean showSignature) {
		this.showSignature = showSignature;
	}

	public String getBottomText() {
		return bottomText;
	}

	public void setBottomText(String bottomText) {
		this.bottomText = bottomText;
	}

	@Override
	public String toString() {
		return "FooterSetup [customFooter=" + customFooter + ", showSignature=" + showSignature + ", bottomText="
				+ bottomText + "]";
	}
}
