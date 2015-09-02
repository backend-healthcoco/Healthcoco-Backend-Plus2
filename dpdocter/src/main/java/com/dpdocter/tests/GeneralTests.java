package com.dpdocter.tests;

import java.util.Date;

import com.dpdocter.collections.GroupCollection;

public class GeneralTests {
    public static void main(String args[]) {
	GroupCollection group = new GroupCollection();

	group.setDescription("Group Description");
	group.setDoctorId("D12345");
	group.setHospitalId("H12345");
	group.setLocationId("L12345");
	group.setName("Group");
	group.setCreatedTime(new Date());

	System.out.println(Converter.ObjectToJSON(group));
    }
}
