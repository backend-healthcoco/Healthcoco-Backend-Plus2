package com.dpdocter.tests;

import com.dpdocter.request.LoginRequest;

public class GeneralTests {

    public static void main(String[] args) {
	LoginRequest request = new LoginRequest();

	request.setPassword("12345");
	request.setUsername("WILLIAM@gmail.com");

	System.out.println(Converter.ObjectToJSON(request));
    }

}
