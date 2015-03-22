package com.dpdocter.beans;


public class Diagram {
private String id;
	
	private String diagramUrl;
	
	private String tags;
	
	private String doctorId;
	
	private String locationId;
	
	private String hospitalId;
	
	private FileDetails diagram;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDiagramUrl() {
		return diagramUrl;
	}

	public void setDiagramUrl(String diagramUrl) {
		this.diagramUrl = diagramUrl;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	


	public FileDetails getDiagram() {
		return diagram;
	}

	public void setDiagram(FileDetails diagram) {
		this.diagram = diagram;
	}
	
	
	

	@Override
	public String toString() {
		return "Diagram [id=" + id + ", diagramUrl=" + diagramUrl + ", tags="
				+ tags + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", diagram="
				+ diagram + "]";
	}
	

}
