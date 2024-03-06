package com.dpdocter.beans;

import java.util.List;

public class NDHMRecordDataDosageInstruction {

	private String text;
	List<NDHMRecordDataCode> additionalInstruction;
	private NDHMRecordDataTiming timing;
	private NDHMRecordDataCode route;
	private NDHMRecordDataCode method;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<NDHMRecordDataCode> getAdditionalInstruction() {
		return additionalInstruction;
	}
	public void setAdditionalInstruction(List<NDHMRecordDataCode> additionalInstruction) {
		this.additionalInstruction = additionalInstruction;
	}
	public NDHMRecordDataTiming getTiming() {
		return timing;
	}
	public void setTiming(NDHMRecordDataTiming timing) {
		this.timing = timing;
	}
	public NDHMRecordDataCode getRoute() {
		return route;
	}
	public void setRoute(NDHMRecordDataCode route) {
		this.route = route;
	}
	public NDHMRecordDataCode getMethod() {
		return method;
	}
	public void setMethod(NDHMRecordDataCode method) {
		this.method = method;
	}
	@Override
	public String toString() {
		return "NDHMRecordDataDosageInstruction [text=" + text + ", additionalInstruction=" + additionalInstruction
				+ ", timing=" + timing + ", route=" + route + ", method=" + method + "]";
	}

}
