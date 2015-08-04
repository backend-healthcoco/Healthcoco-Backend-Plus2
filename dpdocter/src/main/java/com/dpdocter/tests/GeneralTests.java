package com.dpdocter.tests;

import com.dpdocter.request.LoginRequest;

public class GeneralTests {
    public static void main(String args[]) {
	LoginRequest request = new LoginRequest();

	request.setPassword("DRPassword123");
	request.setUsername("varunk2006@gmail.com");

	System.out.println(Converter.ObjectToJSON(request));
    }
}
