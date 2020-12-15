package com.dpdocter.beans;

public class NDHMRecordDataCoding {
	
	private String system;
	private String code;
	private String display;
	
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	@Override
	public String toString() {
		return "NDHMRecordDataCoding [system=" + system + ", code=" + code + ", display=" + display
				+ "]";
	}
}
