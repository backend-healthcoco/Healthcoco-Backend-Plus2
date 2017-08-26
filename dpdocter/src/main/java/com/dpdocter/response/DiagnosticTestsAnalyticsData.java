package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DiagnosticTest;

public class DiagnosticTestsAnalyticsData {

	private List<DiagnosticTest> tests;

	private Date date;

	public List<DiagnosticTest> getTests() {
		return tests;
	}

	public void setTests(List<DiagnosticTest> tests) {
		this.tests = tests;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "DiagnosticTestsAnalyticsData [tests=" + tests + ", date=" + date + "]";
	}
}
