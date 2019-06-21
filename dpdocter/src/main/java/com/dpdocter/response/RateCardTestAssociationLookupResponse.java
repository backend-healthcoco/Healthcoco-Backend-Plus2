package com.dpdocter.response;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.collections.GenericCollection;

/**
 * @author healthcoco
 *
 */
public class RateCardTestAssociationLookupResponse extends GenericCollection {

	private String id;
	private String locationId;
	private String hospitalId;
	private String diagnosticTestId;
	private String rateCardId;
	private Integer turnaroundTime;
	private Double cost;
	private String category;
	private String labId;
	private DiagnosticTest diagnosticTest;
	private Boolean isAvailable;
	private Boolean discarded = false;

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDiagnosticTestId() {
		return diagnosticTestId;
	}

	public void setDiagnosticTestId(String diagnosticTestId) {
		this.diagnosticTestId = diagnosticTestId;
	}

	public String getRateCardId() {
		return rateCardId;
	}

	public void setRateCardId(String rateCardId) {
		this.rateCardId = rateCardId;
	}

	public Integer getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(Integer turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLabId() {
		return labId;
	}

	public void setLabId(String labId) {
		this.labId = labId;
	}

	public DiagnosticTest getDiagnosticTest() {
		return diagnosticTest;
	}

	public void setDiagnosticTest(DiagnosticTest diagnosticTest) {
		this.diagnosticTest = diagnosticTest;
	}

	@Override
	public String toString() {
		return "RateCardTestAssociationLookupResponse [id=" + id + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", diagnosticTestId=" + diagnosticTestId + ", rateCardId=" + rateCardId
				+ ", turnaroundTime=" + turnaroundTime + ", cost=" + cost + ", category=" + category + ", labId="
				+ labId + ", diagnosticTest=" + diagnosticTest + "]";
	}

}