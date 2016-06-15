package com.dpdocter.beans;

public class RecordsDescription {
    private String id;

    private String explanation;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "RecordsDescription [id=" + id + ", explanation=" + explanation + "]";
	}
}
