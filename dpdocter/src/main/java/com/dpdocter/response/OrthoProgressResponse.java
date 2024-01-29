package com.dpdocter.response;

import com.dpdocter.beans.AlignerProgressDetail;
import com.dpdocter.collections.GenericCollection;

public class OrthoProgressResponse extends GenericCollection {
	private String id;
	
	private String planId;

	private AlignerProgressDetail upperAligner;

	private AlignerProgressDetail lowerAligner;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
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
