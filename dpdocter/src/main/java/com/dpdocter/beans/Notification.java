package com.dpdocter.beans;

public class Notification {

	private String title;
	
	private String text;
	
	private String img;
	
	private String notificationType;
	
	private String xi;
	
	private String ri;
	
	private String pi;
	
	private String di;

	private String ai;

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

	public String getXi() {
		return xi;
	}

	public void setXi(String xi) {
		this.xi = xi;
	}

	public String getRi() {
		return ri;
	}

	public void setRi(String ri) {
		this.ri = ri;
	}

	public String getPi() {
		return pi;
	}

	public void setPi(String pi) {
		this.pi = pi;
	}

	public String getDi() {
		return di;
	}

	public void setDi(String di) {
		this.di = di;
	}

	public String getAi() {
		return ai;
	}

	public void setAi(String ai) {
		this.ai = ai;
	}

	@Override
	public String toString() {
		return "Notification [title=" + title + ", text=" + text + ", img=" + img + ", notificationType="
				+ notificationType + ", xi=" + xi + ", ri=" + ri + ", pi=" + pi + ", di=" + di + ", ai=" + ai + "]";
	}
}
