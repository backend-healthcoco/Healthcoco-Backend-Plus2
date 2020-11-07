package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.type.TypeFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Districts;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmMobileOtp;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.services.NDHMservices;
import com.dpdocter.webservices.LoginApi;
import com.fasterxml.jackson.databind.JavaType;
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
			orderRequest.put("mobile",mobileNumber);
			

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			
			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Accept-Language","en-US");
		//	con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
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
	
	@Override
	public Boolean resendOtp(String txnId) {
		Boolean response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/resendOtp";
			JSONObject orderRequest = new JSONObject();
			
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
			//con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			
			System.out.println("Bearer"+oauth.getAccessToken());
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
			
			 response =Boolean.parseBoolean(output.toString());// mapper.readValue(output.toString(),Strin.class);
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
	public HealthIdResponse createHealthId(HealthIdRequest request) {
		HealthIdResponse response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/createHealthId";
			JSONObject orderRequest = new JSONObject();
			
			orderRequest.put("address", request.getAddress());
			orderRequest.put("dayOfBirth", request.getDayOfBirth());
			orderRequest.put("districtCode", request.getDistrictCode());
			orderRequest.put("email", request.getEmail());
			orderRequest.put("firstName", request.getFirstName());
			orderRequest.put("pincode", request.getPincode());
			orderRequest.put("gender", request.getGender());
			orderRequest.put("healthId", request.getHealthId());
			orderRequest.put("lastName", request.getLastName());
			orderRequest.put("middleName", request.getMiddleName());
			orderRequest.put("monthOfBirth", request.getMonthOfBirth());
			orderRequest.put("name", request.getName());
			orderRequest.put("password", request.getPassword());
			orderRequest.put("restrictions", request.getRestrictions());
			orderRequest.put("profilePhoto", request.getProfilePhoto());
			orderRequest.put("stateCode", request.getStateCode());
			orderRequest.put("subdistrictCode", request.getSubdistrictCode());
			orderRequest.put("token", request.getToken());
			orderRequest.put("townCode", request.getTownCode());
			orderRequest.put("txnId", request.getTxnId());
			orderRequest.put("villageCode", request.getVillageCode());
			orderRequest.put("wardCode", request.getWardCode());
			orderRequest.put("yearOfBirth", request.getYearOfBirth());
			
			
			

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			
			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Accept-Language","en-US");
			//con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			
			System.out.println("Bearer"+oauth.getAccessToken());
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
			
			 response = mapper.readValue(output.toString(),HealthIdResponse.class);
			 
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
	public List<NDHMStates> getListforStates() {
		List<NDHMStates>response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/ha/lgd/states";
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("otp",otp);
//			orderRequest.put("txnId",  txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			
			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");

			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Accept-Language","en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */
			StringBuffer respons = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {

				respons.append(inputLine);

			}
			in.close();			
				System.out.println("response:"+respons.toString());
				  ObjectMapper mapper = new ObjectMapper();
			String output=respons.toString();
				 
			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, NDHMStates.class);
			response = mapper.readValue(output,type);
				
				 System.out.println("response"+respons.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}
	
	@Override
	public List<Districts> getListforDistricts(String statecode) {
		List<Districts> response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/ha/lgd/districts?stateCode="+statecode;
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("otp",otp);
//			orderRequest.put("txnId",  txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			
			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");

			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Accept-Language","en-US");
			con.setRequestProperty("Authorization", "Bearer" + oauth.getAccessToken());
			
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */
			StringBuffer respons = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {

				respons.append(inputLine);

			}
			in.close();			
				System.out.println("response:"+respons.toString());
				  ObjectMapper mapper = new ObjectMapper();
			String output=respons.toString();
				 
			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Districts.class);
					response = mapper.readValue(output,type);
				 System.out.println("response"+respons.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}
	
	
	@Override
	public String existsByHealthId(String healthId) {
		String response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/search/existsByHealthId";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("healthId",healthId);
			

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
	public HealthIdSearch searchByHealthId(String healthId) {
		HealthIdSearch response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/search/searchByHealthId";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("healthId",healthId);
			

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
				  ObjectMapper mapper = new ObjectMapper();
			
			 response = mapper.readValue(output.toString(),HealthIdSearch.class);
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
	public HealthIdSearch searchBymobileNumber(HealthIdSearchRequest request) {
		HealthIdSearch response=null;
		try {
			
			NdhmOauthResponse oauth=session();
			
			String url="https://healthidsbx.ndhm.gov.in/api/v1/search/searchByMobile";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("gender",request.getGender());
			orderRequest.put("mobile",request.getMobile());
			orderRequest.put("name",request.getName());
			orderRequest.put("yearOfBirth",request.getYearOfBirth());
			

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
				  ObjectMapper mapper = new ObjectMapper();
			
			 response = mapper.readValue(output.toString(),HealthIdSearch.class);
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
