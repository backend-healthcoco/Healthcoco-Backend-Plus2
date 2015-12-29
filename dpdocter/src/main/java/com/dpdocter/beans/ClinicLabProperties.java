package com.dpdocter.beans;

public class ClinicLabProperties {

	private String id;

	private Boolean isLab = false;
    
    private Boolean isOnlineReportsAvailable = false;
    
    private Boolean isNABLAccredited = false;
    
    private Boolean isHomeServiceAvailable = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getIsLab() {
		return isLab;
	}

	public void setIsLab(Boolean isLab) {
		this.isLab = isLab;
	}

	public Boolean getIsOnlineReportsAvailable() {
		return isOnlineReportsAvailable;
	}

	public void setIsOnlineReportsAvailable(Boolean isOnlineReportsAvailable) {
		this.isOnlineReportsAvailable = isOnlineReportsAvailable;
	}

	public Boolean getIsNABLAccredited() {
		return isNABLAccredited;
	}

	public void setIsNABLAccredited(Boolean isNABLAccredited) {
		this.isNABLAccredited = isNABLAccredited;
	}

	public Boolean getIsHomeServiceAvailable() {
		return isHomeServiceAvailable;
	}

	public void setIsHomeServiceAvailable(Boolean isHomeServiceAvailable) {
		this.isHomeServiceAvailable = isHomeServiceAvailable;
	}

	@Override
	public String toString() {
		return "ClinicLabProperties [id=" + id + ", isLab=" + isLab + ", isOnlineReportsAvailable="
				+ isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited + ", isHomeServiceAvailable="
				+ isHomeServiceAvailable + "]";
	}
}
