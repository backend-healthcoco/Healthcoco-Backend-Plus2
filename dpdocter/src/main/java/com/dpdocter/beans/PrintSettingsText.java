package com.dpdocter.beans;

public class PrintSettingsText {

    private String text;

    private String fontStyle;

    private String fontColor;

    private String fontSize;

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getFontStyle() {
	return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
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
	return "PrintSettingsText [text=" + text + ", fontStyle=" + fontStyle + ", fontColor=" + fontColor + ", fontSize=" + fontSize + "]";
    }
}
