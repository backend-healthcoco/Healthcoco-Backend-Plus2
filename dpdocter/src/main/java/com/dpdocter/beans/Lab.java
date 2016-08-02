package com.dpdocter.beans;

import java.util.List;

public class Lab {

    private String id;

    private Hospital hospital;

    private Location location;

    private List<Doctor> doctors;

    private List<LabTest> labTests;

    private Integer noOfLabTest = 0;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Hospital getHospital() {
	return hospital;
    }

    public void setHospital(Hospital hospital) {
	this.hospital = hospital;
    }

    public Location getLocation() {
	return location;
    }

    public void setLocation(Location location) {
	this.location = location;
    }

    public List<Doctor> getDoctors() {
	return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
	this.doctors = doctors;
    }

    public List<LabTest> getLabTests() {
	return labTests;
    }

    public void setLabTests(List<LabTest> labTests) {
	this.labTests = labTests;
    }

	public Integer getNoOfLabTest() {
		return noOfLabTest;
	}

	public void setNoOfLabTest(Integer noOfLabTest) {
		this.noOfLabTest = noOfLabTest;
	}

	@Override
	public String toString() {
		return "Lab [id=" + id + ", hospital=" + hospital + ", location=" + location + ", doctors=" + doctors
				+ ", labTests=" + labTests + ", noOfLabTest=" + noOfLabTest + "]";
	}
}
