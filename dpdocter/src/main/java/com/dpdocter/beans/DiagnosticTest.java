package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DiagnosticTest extends GenericCollection {

	private String id;

	private String testName;

	private String explanation;

	private String specimen;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String code;

	private long count;

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

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "DiagnosticTest [id=" + id + ", testName=" + testName + ", explanation=" + explanation + ", specimen="
				+ specimen + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", code=" + code + ", count=" + count + "]";
	}


}
