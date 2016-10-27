package com.dpdocter.beans;

public class PatientTreatmentJasperDetails {

	private int no;

	private String treatmentServiceName;

	private String status;

	private String quantity;

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getTreatmentServiceName() {
		return treatmentServiceName;
	}

	public void setTreatmentServiceName(String treatmentServiceName) {
		this.treatmentServiceName = treatmentServiceName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "PatientTreatmentJasperDetails [no=" + no + ", treatmentServiceName=" + treatmentServiceName
				+ ", status=" + status + ", quantity=" + quantity + "]";
	}

}
