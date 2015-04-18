package com.dpdocter.tests;

import com.dpdocter.request.PrescriptionGetRequest;

public class GeneralTests {
	public static void main(String args[]) {
		System.out.println(Converter.ObjectToJSON(new PrescriptionGetRequest()));
	}
}
