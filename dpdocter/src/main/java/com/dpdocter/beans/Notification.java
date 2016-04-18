package com.dpdocter.beans;

public class Notification {

	private String notificationType;
	
	private String text;

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

	@Override
	public String toString() {
		return "Notification [notificationType=" + notificationType + ", text=" + text + "]";
	}
	
}
