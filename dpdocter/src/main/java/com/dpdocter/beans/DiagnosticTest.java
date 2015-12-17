package com.dpdocter.beans;

public class DiagnosticTest {

    private String id;

    private String testName;

    private String description;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getTestName() {
	return testName;
    }

    public void setTestName(String testName) {
	this.testName = testName;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "DiagnosticTest [id=" + id + ", testName=" + testName + ", description=" + description + "]";
    }
}
