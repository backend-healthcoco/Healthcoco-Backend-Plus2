package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DentalStage;

public class UpdateDentalStagingRequest {

	private String requestId;
	private String uniqueWorkId;
	private List<DentalStageRequest> dentalStages;
	private String status;
	private String processStatus;
	private Boolean isCompleted;
	private Boolean isTrailChanged = false;

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

	public List<DentalStageRequest> getDentalStages() {
		return dentalStages;
	}

	public void setDentalStages(List<DentalStageRequest> dentalStages) {
		this.dentalStages = dentalStages;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Boolean getIsTrailChanged() {
		return isTrailChanged;
	}

	public void setIsTrailChanged(Boolean isTrailChanged) {
		this.isTrailChanged = isTrailChanged;
	}
	
	

}
