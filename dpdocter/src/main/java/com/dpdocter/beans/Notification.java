package com.dpdocter.beans;

public class Notification {

	private String title;
	
	private String text;
	
	private String img;
	
	private String notificationType;
	
	private String XI;
	
	private String RI;
	
	private String PI;
	
	private String DI;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
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
		return "Notification [title=" + title + ", text=" + text + ", img=" + img + ", notificationType="
				+ notificationType + ", XI=" + XI + ", RI=" + RI + ", PI=" + PI + ", DI=" + DI + "]";
	}

}
