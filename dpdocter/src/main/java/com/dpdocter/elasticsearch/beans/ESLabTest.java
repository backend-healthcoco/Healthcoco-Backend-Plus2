package com.dpdocter.elasticsearch.beans;

public class ESLabTest {

	private String id;

	private String testName;

	private int cost = 0;

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

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "ESLabTest [id=" + id + ", testName=" + testName + ", cost=" + cost + "]";
	}
}
