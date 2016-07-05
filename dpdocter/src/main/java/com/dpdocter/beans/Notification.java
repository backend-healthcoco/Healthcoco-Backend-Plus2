package com.dpdocter.beans;

public class Notification {

	private String type;
	
	private String text;
	
	private String img;
	
	private String componentType;
	
	private String XI;
	
	private String RI;
	
	private String PI;
	
	private String DI;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getXI() {
		return XI;
	}

	public void setXI(String xI) {
		XI = xI;
	}

	public String getRI() {
		return RI;
	}

	public void setRI(String rI) {
		RI = rI;
	}

	public String getPI() {
		return PI;
	}

	public void setPI(String pI) {
		PI = pI;
	}

	public String getDI() {
		return DI;
	}

	public void setDI(String dI) {
		DI = dI;
	}

	@Override
	public String toString() {
		return "Notification [type=" + type + ", text=" + text + ", img=" + img + ", componentType=" + componentType
				+ ", XI=" + XI + ", RI=" + RI + ", PI=" + PI + ", DI=" + DI + "]";
	}
}
