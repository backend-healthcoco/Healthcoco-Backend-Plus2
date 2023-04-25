package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RankingCountParametersWithValueInPercentage;
import com.dpdocter.enums.Resource;

@Document(collection = "ranking_count_cl")
public class RankingCountCollection extends GenericCollection {

	@Field
	private ObjectId id;

	@Field
	private ObjectId resourceId;

	@Field
	private String resourceName;

	@Field
	private Resource resourceType;

	@Field
	private List<RankingCountParametersWithValueInPercentage> parameters;

	@Field
	private double totalCountInPercentage = 0.0;

	@Field
	private int rankingCount = 0;

	@Field
	private ObjectId locationId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getResourceId() {
		return resourceId;
	}

	public void setResourceId(ObjectId resourceId) {
		this.resourceId = resourceId;
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

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "RankingCountCollection [id=" + id + ", resourceId=" + resourceId + ", resourceName=" + resourceName
				+ ", resourceType=" + resourceType + ", parameters=" + parameters + ", totalCountInPercentage="
				+ totalCountInPercentage + ", rankingCount=" + rankingCount + ", locationId=" + locationId + "]";
	}
}
