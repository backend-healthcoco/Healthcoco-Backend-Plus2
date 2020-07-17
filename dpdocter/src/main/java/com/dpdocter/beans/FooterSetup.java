package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FooterSetup {

	private Boolean customFooter = true;

	private Boolean showSignature = true;

	private Boolean showPoweredBy = true;

	private Boolean showBottomSignText = true;

	private String bottomSignText = "";

	private Boolean showImageFooter = false;

	private String footerImageUrl = "";

	private Integer footerHeight = 0;

	private String signatureUrl = "";

	private Boolean showSignatureBox = true;

	public Boolean getShowBottomSignText() {
		return showBottomSignText;
	}

	public void setShowBottomSignText(Boolean showBottomSignText) {
		this.showBottomSignText = showBottomSignText;
	}

	public String getBottomSignText() {
		return bottomSignText;
	}

	public void setBottomSignText(String bottomSignText) {
		this.bottomSignText = bottomSignText;
	}

	private List<PrintSettingsText> bottomText;

	public Boolean getShowPoweredBy() {
		return showPoweredBy;
	}

	public void setShowPoweredBy(Boolean showPoweredBy) {
		this.showPoweredBy = showPoweredBy;
	}

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

	public List<PrintSettingsText> getBottomText() {
		return bottomText;
	}

	public void setBottomText(List<PrintSettingsText> bottomText) {
		this.bottomText = bottomText;
	}

	public Boolean getShowImageFooter() {
		return showImageFooter;
	}

	public void setShowImageFooter(Boolean showImageFooter) {
		this.showImageFooter = showImageFooter;
	}

	public String getFooterImageUrl() {
		return footerImageUrl;
	}

	public void setFooterImageUrl(String footerImageUrl) {
		this.footerImageUrl = footerImageUrl;
	}

	public Integer getFooterHeight() {
		return footerHeight;
	}

	public void setFooterHeight(Integer footerHeight) {
		this.footerHeight = footerHeight;
	}

	public String getSignatureUrl() {
		return signatureUrl;
	}

	public void setSignatureUrl(String signatureUrl) {
		this.signatureUrl = signatureUrl;
	}

	public Boolean getShowSignatureBox() {
		return showSignatureBox;
	}

	public void setShowSignatureBox(Boolean showSignatureBox) {
		this.showSignatureBox = showSignatureBox;
	}

	@Override
	public String toString() {
		return "FooterSetup [customFooter=" + customFooter + ", showSignature=" + showSignature + ", showPoweredBy="
				+ showPoweredBy + ", showBottomSignText=" + showBottomSignText + ", bottomSignText=" + bottomSignText
				+ ", showImageFooter=" + showImageFooter + ", footerImageUrl=" + footerImageUrl + ", footerHeight="
				+ footerHeight + ", signatureUrl=" + signatureUrl + ", showSignatureBox=" + showSignatureBox
				+ ", bottomText=" + bottomText + "]";
	}

}
