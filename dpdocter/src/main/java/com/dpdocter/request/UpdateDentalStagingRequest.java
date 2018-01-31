package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DentalStage;

public class UpdateDentalStagingRequest {

	String requestId;
	String uniqueWorkId;
	List<DentalStage> dentalStages;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getUniqueWorkId() {
		return uniqueWorkId;
	}

	public void setUniqueWorkId(String uniqueWorkId) {
		this.uniqueWorkId = uniqueWorkId;
	}

	public List<DentalStage> getDentalStages() {
		return dentalStages;
	}

	public void setDentalStages(List<DentalStage> dentalStages) {
		this.dentalStages = dentalStages;
	}

}
