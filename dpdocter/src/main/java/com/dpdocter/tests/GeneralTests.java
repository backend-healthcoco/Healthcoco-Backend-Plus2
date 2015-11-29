package com.dpdocter.tests;

import com.dpdocter.beans.Landmark;

public class GeneralTests {
    public static void main(String args[]) {
	Landmark landmark = new Landmark();

	landmark.setCityId("565ac97127360c89a4c99d86");
	landmark.setDescription("Test Landmark");
	landmark.setLandmark("Panchsheel");

	System.out.println(Converter.ObjectToJSON(landmark));
    }
}
