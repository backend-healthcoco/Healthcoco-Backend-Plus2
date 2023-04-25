package com.dpdocter.response;

public class PharmacyWithRankingDetailResponse {

	private String localeId;

	private String resourceName;

	private Double genericMedicineCount = 0.0;

	private Double requestCount = 0.0;

	private Double responseCount = 0.0;

	private Double totalCount = 0.0;

	private Double noOfLikes = 0.0;

	private int rankingCount = 0;

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Double getGenericMedicineCount() {
		return genericMedicineCount;
	}

	public void setGenericMedicineCount(Double genericMedicineCount) {
		this.genericMedicineCount = genericMedicineCount;
	}

	public Double getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(Double requestCount) {
		this.requestCount = requestCount;
	}

	public Double getResponseCount() {
		return responseCount;
	}

	public void setResponseCount(Double responseCount) {
		this.responseCount = responseCount;
	}

	public Double getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Double totalCount) {
		this.totalCount = totalCount;
	}

	public Double getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Double noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public int getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(int rankingCount) {
		this.rankingCount = rankingCount;
	}

	@Override
	public String toString() {
		return "PharmacyWithRankingDetailResponse [localeId=" + localeId + ", resourceName=" + resourceName
				+ ", genericMedicineCount=" + genericMedicineCount + ", requestCount=" + requestCount
				+ ", responseCount=" + responseCount + ", totalCount=" + totalCount + ", noOfLikes=" + noOfLikes
				+ ", rankingCount=" + rankingCount + "]";
	}
}
