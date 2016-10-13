package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FooterSetup {

    private Boolean customFooter  = true;

    private Boolean showSignature  = true;

    private List<PrintSettingsText> bottomText;

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

    @Override
    public String toString() {
	return "FooterSetup [customFooter=" + customFooter + ", showSignature=" + showSignature + ", bottomText=" + bottomText + "]";
    }
}
