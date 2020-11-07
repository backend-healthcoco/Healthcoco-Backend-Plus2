package com.dpdocter.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.NdhmMobileOtp;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.services.NDHMservices;
import com.dpdocter.webservices.LoginApi;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NDHMserviceImpl implements NDHMservices{

	private static Logger logger = LogManager.getLogger(NDHMserviceImpl.class.getName());
	
	@Value(value = "${ndhm.clientId}")
	private String NDHM_CLIENTID;
	
	@Value(value = "${ndhm.clientSecret}")
	private String NDHM_CLIENT_SECRET;
	
	
	
	public NdhmOauthResponse session() {
		NdhmOauthResponse response=null;
		try {
			String url="https://dev.ndhm.gov.in/gateway/v0.5/sessions";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("clientId",NDHM_CLIENTID);
			orderRequest.put("clientSecret",  NDHM_CLIENT_SECRET);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			
			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type","application/json");
		//	con.setRequestProperty("Authorization", "Basic " +  authStringEnc);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:"+orderRequest.toString());
			  wr.flush();
	            wr.close();
	            con.disconnect();
	            InputStream in=con.getInputStream();
	        //    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				System.out.println(con.getErrorStream());
				/* response = new StringBuffer(); */
				StringBuffer output = new StringBuffer();
				int c = 0;
				while ((c=in.read()) !=-1) {

					output.append((char) c);
					
				}
				System.out.println("response:"+output.toString());
				  ObjectMapper mapper = new ObjectMapper();
			
			 response = mapper.readValue(output.toString(),NdhmOauthResponse.class);
			System.out.println("response"+output.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}
	
	
	@Override
	public String generateOtp(String mobileNumber) {
		String response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/generateOtp";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("mobileNumber",mobileNumber);
			

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			
			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Accept-Language","en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:"+orderRequest.toString());
			  wr.flush();
	            wr.close();
	            con.disconnect();
	            InputStream in=con.getInputStream();
	        //    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				System.out.println(con.getErrorStream());
				/* response = new StringBuffer(); */
				StringBuffer output = new StringBuffer();
				int c = 0;
				while ((c=in.read()) !=-1) {

					output.append((char) c);
					
				}
				System.out.println("response:"+output.toString());
				  //ObjectMapper mapper = new ObjectMapper();
			
			 response =output.toString();// mapper.readValue(output.toString(),Strin.class);
			System.out.println("response"+output.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}
	
	
	@Override
	public String verifyOtp(String otp,String txnId) {
		String response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/verifyOtp";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("otp",otp);
			orderRequest.put("txnId",  txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			
			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Accept-Language","en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:"+orderRequest.toString());
			  wr.flush();
	            wr.close();
	            con.disconnect();
	            InputStream in=con.getInputStream();
	        //    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				System.out.println(con.getErrorStream());
				/* response = new StringBuffer(); */
				StringBuffer output = new StringBuffer();
				int c = 0;
				while ((c=in.read()) !=-1) {

					output.append((char) c);
					
				}
				System.out.println("response:"+output.toString());
				  //ObjectMapper mapper = new ObjectMapper();
			
			 response =output.toString();// mapper.readValue(output.toString(),Strin.class);
			System.out.println("response"+output.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}
	
	

	
}
