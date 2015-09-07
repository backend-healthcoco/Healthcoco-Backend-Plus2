package com.dpdocter.tests;

import com.dpdocter.request.ClinicalNotesAddRequest;

public class GeneralTests {
    public static void main(String args[]) {
	ClinicalNotesAddRequest request = new ClinicalNotesAddRequest();

	System.out.println(Converter.ObjectToJSON(request));
    }
}
