package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.ReferenceDetail;

public class ReferenceResponse {
	private List<ReferenceDetail> referenceDetails;

	public List<ReferenceDetail> getReferenceDetails() {
		return referenceDetails;
	}

	public void setReferenceDetails(List<ReferenceDetail> referenceDetails) {
		this.referenceDetails = referenceDetails;
	}

	@Override
	public String toString() {
		return "ReferenceResponse [referenceDetails=" + referenceDetails + "]";
	}
}
