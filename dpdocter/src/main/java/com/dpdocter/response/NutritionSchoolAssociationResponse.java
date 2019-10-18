package com.dpdocter.response;

import com.dpdocter.beans.SchoolBranch;

public class NutritionSchoolAssociationResponse {

	private String id;
	private String branchId;
	private SchoolBranch branch;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public SchoolBranch getBranch() {
		return branch;
	}

	public void setBranch(SchoolBranch branch) {
		this.branch = branch;
	}

}
