package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class GenericCodesAndReaction extends GenericCollection {

	private String id;
	
	private GenericCode firstGenericCode;
	
	private GenericCode secondGenericCode;
	
	private String reactionType;
	
	private String explanation;

	private Boolean discarded = false;
	
	public GenericCodesAndReaction() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GenericCode getFirstGenericCode() {
		return firstGenericCode;
	}

	public void setFirstGenericCode(GenericCode firstGenericCode) {
		this.firstGenericCode = firstGenericCode;
	}

	public GenericCode getSecondGenericCode() {
		return secondGenericCode;
	}

	public void setSecondGenericCode(GenericCode secondGenericCode) {
		this.secondGenericCode = secondGenericCode;
	}

	public String getReactionType() {
		return reactionType;
	}

	public void setReactionType(String reactionType) {
		this.reactionType = reactionType;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "GenericCodesAndReaction [id=" + id + ", firstGenericCode=" + firstGenericCode + ", secondGenericCode="
				+ secondGenericCode + ", reactionType=" + reactionType + ", explanation=" + explanation + ", discarded="
				+ discarded + "]";
	}
}
