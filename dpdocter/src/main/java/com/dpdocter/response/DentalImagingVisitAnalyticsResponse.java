package com.dpdocter.response;

import com.dpdocter.beans.DentalImagingServiceVisitCount;

public class DentalImagingVisitAnalyticsResponse {

	private Integer patientVisitCount = 0;
	private DentalImagingServiceVisitCount mostVisitedService;
	private DentalImagingServiceVisitCount leastVisitedService;

	public Integer getPatientVisitCount() {
		return patientVisitCount;
	}

	public void setPatientVisitCount(Integer patientVisitCount) {
		this.patientVisitCount = patientVisitCount;
	}

	public DentalImagingServiceVisitCount getMostVisitedService() {
		return mostVisitedService;
	}

	public void setMostVisitedService(DentalImagingServiceVisitCount mostVisitedService) {
		this.mostVisitedService = mostVisitedService;
	}

	public DentalImagingServiceVisitCount getLeastVisitedService() {
		return leastVisitedService;
	}

	public void setLeastVisitedService(DentalImagingServiceVisitCount leastVisitedService) {
		this.leastVisitedService = leastVisitedService;
	}

	@Override
	public String toString() {
		return "DentalImagingVisitAnalyticsResponse [patientVisitCount=" + patientVisitCount + ", mostVisitedService="
				+ mostVisitedService + ", leastVisitedService=" + leastVisitedService + "]";
	}

}
