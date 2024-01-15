package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AlignerProgressDetail;

//@Document(collection = "ortho_progress_cl")
public class OrthoProgressCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId planId;

	@Field
	private AlignerProgressDetail upperAligner;

	@Field
	private AlignerProgressDetail lowerAligner;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPlanId() {
		return planId;
	}

	public void setPlanId(ObjectId planId) {
		this.planId = planId;
	}

	public AlignerProgressDetail getUpperAligner() {
		return upperAligner;
	}

	public void setUpperAligner(AlignerProgressDetail upperAligner) {
		this.upperAligner = upperAligner;
	}

	public AlignerProgressDetail getLowerAligner() {
		return lowerAligner;
	}

	public void setLowerAligner(AlignerProgressDetail lowerAligner) {
		this.lowerAligner = lowerAligner;
	}
}
