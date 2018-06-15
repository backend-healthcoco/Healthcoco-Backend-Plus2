package com.dpdocter.response;

public class DentalImagingVisitAnalyticsResponse {

	private String mostVisitedServiceName;
	private String leastVisitedServiceName;
	private Integer patientVisitCount = 0;
	private Integer mostVisitedServiceCount = 0;
	private Integer leastVisitedIntegerCount = 0;

	public String getMostVisitedServiceName() {
		return mostVisitedServiceName;
	}

	public void setMostVisitedServiceName(String mostVisitedServiceName) {
		this.mostVisitedServiceName = mostVisitedServiceName;
	}

	public String getLeastVisitedServiceName() {
		return leastVisitedServiceName;
	}

	public void setLeastVisitedServiceName(String leastVisitedServiceName) {
		this.leastVisitedServiceName = leastVisitedServiceName;
	}

	public Integer getPatientVisitCount() {
		return patientVisitCount;
	}

	public void setPatientVisitCount(Integer patientVisitCount) {
		this.patientVisitCount = patientVisitCount;
	}

	public Integer getMostVisitedServiceCount() {
		return mostVisitedServiceCount;
	}

	public void setMostVisitedServiceCount(Integer mostVisitedServiceCount) {
		this.mostVisitedServiceCount = mostVisitedServiceCount;
	}

	public Integer getLeastVisitedIntegerCount() {
		return leastVisitedIntegerCount;
	}

	public void setLeastVisitedIntegerCount(Integer leastVisitedIntegerCount) {
		this.leastVisitedIntegerCount = leastVisitedIntegerCount;
	}

	@Override
	public String toString() {
		return "DentalImagingVisitAnalyticsResponse [mostVisitedServiceName=" + mostVisitedServiceName
				+ ", leastVisitedServiceName=" + leastVisitedServiceName + ", patientVisitCount=" + patientVisitCount
				+ ", mostVisitedServiceCount=" + mostVisitedServiceCount + ", leastVisitedIntegerCount="
				+ leastVisitedIntegerCount + "]";
	}

}
