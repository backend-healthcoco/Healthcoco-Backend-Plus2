package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.VaccineStatus;

public class MultipleVaccineEditRequest {

	private List<String> ids;
	private VaccineStatus status = VaccineStatus.GIVEN;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public VaccineStatus getStatus() {
		return status;
	}

	public void setStatus(VaccineStatus status) {
		this.status = status;
	}

}
