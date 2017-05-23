package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.TestSampleType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DiagnosticTest extends GenericCollection {

	private String id;

	private String testName;

	private String explanation;

	private String sampleType;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String code;

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

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	@Override
	public String toString() {
		return "DiagnosticTest [id=" + id + ", testName=" + testName + ", explanation=" + explanation + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", code=" + code + "]";
	}

}
