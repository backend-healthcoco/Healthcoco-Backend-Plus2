package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.FieldAlign;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PrintSettingsText {

	private String text;

	private List<String> fontStyle;

	private String fontColor;

	private String fontSize;

	private Boolean showTreatmentcost = true;

	private FieldAlign instructionAlign = FieldAlign.VERTICAL;

	public Boolean getShowTreatmentcost() {
		return showTreatmentcost;
	}

	public void setShowTreatmentcost(Boolean showTreatmentcost) {
		this.showTreatmentcost = showTreatmentcost;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(List<String> fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	@Override
	public String toString() {
		return "PrintSettingsText [text=" + text + ", fontStyle=" + fontStyle + ", fontColor=" + fontColor
				+ ", fontSize=" + fontSize + "]";
	}

	public FieldAlign getInstructionAlign() {
		return instructionAlign;
	}

	public void setInstructionAlign(FieldAlign instructionAlign) {
		this.instructionAlign = instructionAlign;
	}
}
