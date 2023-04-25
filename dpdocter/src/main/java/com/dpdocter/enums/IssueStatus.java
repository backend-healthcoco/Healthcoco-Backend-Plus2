package com.dpdocter.enums;

public enum IssueStatus {

	OPEN("OPEN"), INPROGRESS("INPROGRESS"), COMPLETED("COMPLETED"), REOPEN("REOPEN"), ALL("ALL");

	private String status;

	public String getStatus() {
		return status;
	}

	private IssueStatus(String status) {
		this.status = status;
	}
}
