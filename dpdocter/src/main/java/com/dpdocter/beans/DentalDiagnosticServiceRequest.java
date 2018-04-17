package com.dpdocter.beans;

import java.util.List;

public class DentalDiagnosticServiceRequest {

	private String type;
	private String serviceName;
	private List<String> toothNumber;
	private String CBCTQuadrant;
	private String CBCTArch;
	private DentalDiagnosticService dentalDiagnosticService;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<String> getToothNumber() {
		return toothNumber;
	}

	public void setToothNumber(List<String> toothNumber) {
		this.toothNumber = toothNumber;
	}

	public String getCBCTQuadrant() {
		return CBCTQuadrant;
	}

	public void setCBCTQuadrant(String cBCTQuadrant) {
		CBCTQuadrant = cBCTQuadrant;
	}

	public String getCBCTArch() {
		return CBCTArch;
	}

	public void setCBCTArch(String cBCTArch) {
		CBCTArch = cBCTArch;
	}

	public DentalDiagnosticService getDentalDiagnosticService() {
		return dentalDiagnosticService;
	}

	public void setDentalDiagnosticService(DentalDiagnosticService dentalDiagnosticService) {
		this.dentalDiagnosticService = dentalDiagnosticService;
	}

}
