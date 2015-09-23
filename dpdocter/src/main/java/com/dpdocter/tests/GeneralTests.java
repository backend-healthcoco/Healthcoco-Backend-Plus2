package com.dpdocter.tests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeneralTests {

    public static void main(String args[]) {

	String strUrl = "http://dndsms.resellergrow.com/api/postsms.php";
	String xmlData = "data=<MESSAGE><AUTHKEY>93114AV2rXJuxL56001692</AUTHKEY><ROUTE>4</ROUTE><SMS TEXT='Hello' FROM='HTCOCO'>"
		+ "<ADDRESS TO='919970729799'></ADDRESS></SMS></MESSAGE>";

	String output = hitUrl(strUrl, xmlData);
	System.out.println("Output is: " + output);

    }

    public static String hitUrl(String urlToHit, String param) {
	try {
	    URL url = new URL(urlToHit);
	    HttpURLConnection http = (HttpURLConnection) url.openConnection();
	    http.setDoOutput(true);
	    http.setDoInput(true);
	    http.setRequestMethod("POST");

	    DataOutputStream wr = new DataOutputStream(http.getOutputStream());
	    wr.writeBytes(param);
	    wr.flush();
	    wr.close();
	    http.disconnect();

	    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
	    String inputLine;
	    if ((inputLine = in.readLine()) != null) {
		in.close();
		return inputLine;
	    } else {
		in.close();
		return "-1";
	    }

	} catch (Exception e) {
	    System.out.println("Exception Caught..!!!");
	    e.printStackTrace();
	    return "-2";
	}
    }
}