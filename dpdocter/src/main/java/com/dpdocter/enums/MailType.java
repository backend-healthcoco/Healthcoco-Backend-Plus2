package com.dpdocter.enums;

public enum MailType {
	CLINICAL_NOTE("CLINICAL_NOTE"), REPORT("REPORT"), PRESCRIPTION("PRESCRIPTION");

	private String mailType;

	private MailType(String mailType) {
		this.mailType = mailType;
	}

	public String getMailType() {
		return mailType;
	}

}
