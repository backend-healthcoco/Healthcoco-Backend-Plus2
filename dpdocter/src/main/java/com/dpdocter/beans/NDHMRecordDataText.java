package com.dpdocter.beans;

public class NDHMRecordDataText {

	private String status;
	private String div;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDiv() {
		return div;
	}
	public void setDiv(String div) {
		this.div = div;
	}
	@Override
	public String toString() {
		return "NDHMRecordDataText [status=" + status + ", div=" + div + "]";
	}
}
