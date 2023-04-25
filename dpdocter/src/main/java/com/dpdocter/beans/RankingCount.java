package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.Resource;

public class RankingCount {

	private String id;

	private String resourceId;

	private String resourceName;

	private Resource resourceType;

	private List<RankingCountParametersWithValueInPercentage> parameters;

	private double totalCountInPercentage = 0.0;

	private int rankingCount = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Resource getResourceType() {
		return resourceType;
	}

	public void setResourceType(Resource resourceType) {
		this.resourceType = resourceType;
	}

	public List<RankingCountParametersWithValueInPercentage> getParameters() {
		return parameters;
	}

	public void setParameters(List<RankingCountParametersWithValueInPercentage> parameters) {
		this.parameters = parameters;
	}

	public double getTotalCountInPercentage() {
		return totalCountInPercentage;
	}

	public void setTotalCountInPercentage(double totalCountInPercentage) {
		this.totalCountInPercentage = totalCountInPercentage;
	}

	public int getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(int rankingCount) {
		this.rankingCount = rankingCount;
	}

	@Override
	public String toString() {
		return "RankingCount [id=" + id + ", resourceId=" + resourceId + ", resourceName=" + resourceName
				+ ", resourceType=" + resourceType + ", parameters=" + parameters + ", totalCountInPercentage="
				+ totalCountInPercentage + ", rankingCount=" + rankingCount + "]";
	}
}
