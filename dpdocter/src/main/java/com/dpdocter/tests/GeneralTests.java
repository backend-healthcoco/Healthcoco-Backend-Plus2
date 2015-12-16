//package com.dpdocter.tests;
//
//import java.io.IOException;
//
//public class GeneralTests {
//
//    public static void main(String[] args) throws IOException {
//	GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyCKFWg02TFUWOLsvJt0A6PMI_aAfqfLFwI");
//	GeocodingResult[] results = null;
//	try {
//	    results = GeocodingApi.geocode(context, "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
//	System.out.println(results[0].formattedAddress);
//
//    }
//
//}
