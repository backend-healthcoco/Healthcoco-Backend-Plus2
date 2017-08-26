package com.dpdocter.request;

public class AddEditLabTestPickupMobileRequest {

	private String id;
	private String status;
	private AddEditLabTestSampleMobileRequest labTestSamples;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AddEditLabTestSampleMobileRequest getLabTestSamples() {
		return labTestSamples;
	}

	public void setLabTestSamples(AddEditLabTestSampleMobileRequest labTestSamples) {
		this.labTestSamples = labTestSamples;
	}

	@Override
	public String toString() {
		return "AddEditLabTestPickupMobileRequest [id=" + id + ", status=" + status + ", labTestSamples="
				+ labTestSamples + "]";
	}

}
