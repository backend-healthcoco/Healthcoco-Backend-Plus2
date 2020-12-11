package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AuthConfirmRequest;
import com.dpdocter.beans.CareContextDiscoverRequest;
import com.dpdocter.beans.CareContextRequest;
import com.dpdocter.beans.Districts;
import com.dpdocter.beans.FetchModesRequest;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.LinkConfirm;
import com.dpdocter.collections.LinkConfirmCollection;
import com.dpdocter.collections.NdhmNotifyCollection;
import com.dpdocter.beans.LinkInitCollection;
import com.dpdocter.beans.LinkRequest;
import com.dpdocter.beans.MobileTokenRequest;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.beans.NdhmOtp;
import com.dpdocter.beans.NdhmOtpStatus;
import com.dpdocter.beans.NdhmStatus;
import com.dpdocter.beans.NotifyRequest;
import com.dpdocter.beans.OnAuthConfirmCollection;
import com.dpdocter.beans.OnAuthConfirmRequest;
import com.dpdocter.beans.OnAuthInitRequest;
import com.dpdocter.beans.OnCareContext;
import com.dpdocter.beans.OnDiscoverRequest;
import com.dpdocter.beans.OnFetchModesRequest;
import com.dpdocter.beans.OnLinkConfirm;
import com.dpdocter.beans.OnLinkRequest;
import com.dpdocter.beans.OnNotifyRequest;
import com.dpdocter.collections.CareContextDiscoverCollection;
import com.dpdocter.collections.OnAuthInitCollection;
import com.dpdocter.collections.OnCareContextCollection;
import com.dpdocter.collections.OnFetchModeCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CareContextDiscoverRepository;
import com.dpdocter.repository.LinkConfirmRepository;
import com.dpdocter.repository.LinkInitRepository;
import com.dpdocter.repository.NdhmNotifyRepository;
import com.dpdocter.repository.OnAuthConfirmRepository;
import com.dpdocter.repository.OnAuthInitRepository;
import com.dpdocter.repository.OnCareContextRepository;
import com.dpdocter.repository.OnFetchModeRepository;
import com.dpdocter.request.CreateAadhaarRequest;
import com.dpdocter.request.CreateProfileRequest;
import com.dpdocter.response.GetCardProfileResponse;
import com.dpdocter.services.NDHMservices;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.util.web.Response;

@Service
public class NDHMserviceImpl implements NDHMservices {

	private static Logger logger = LogManager.getLogger(NDHMserviceImpl.class.getName());
	
	
	@Value(value = "${ndhm.clientId}")
	private String NDHM_CLIENTID;

	@Value(value = "${ndhm.clientSecret}")
	private String NDHM_CLIENT_SECRET;
	
	@Autowired
	private OnFetchModeRepository onFetchModeRepository;
	
	@Autowired
	private OnAuthInitRepository onAuthInitRepository;
	
	@Autowired
	private OnAuthConfirmRepository onAuthConfirmRepository;
	
	
	@Autowired
	private OnCareContextRepository onCareContextRepository;
	
	
	@Autowired
	private CareContextDiscoverRepository careContextDiscoverRepository;
	
	
	@Autowired
	private LinkInitRepository linkInitRepository;
	
	@Autowired
	private LinkConfirmRepository linkConfirmRepository;
	
	@Autowired
	private NdhmNotifyRepository ndhmNotifyRepository;
	
	

	public NdhmOauthResponse session() {
		NdhmOauthResponse response = null;
		try {
			String url = "https://dev.ndhm.gov.in/gateway/v0.5/sessions";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("clientId", NDHM_CLIENTID);
			orderRequest.put("clientSecret", NDHM_CLIENT_SECRET);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			// con.setRequestProperty("Authorization", "Basic " + authStringEnc);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			ObjectMapper mapper = new ObjectMapper();

			response = mapper.readValue(output.toString(), NdhmOauthResponse.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public NdhmOtp generateOtp(String mobileNumber) {
		NdhmOtp response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/generateOtp";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("mobile", mobileNumber);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			ObjectMapper mapper = new ObjectMapper();

			//response = output.toString();//
			 response= mapper.readValue(output.toString(),NdhmOtp.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public NdhmOtp verifyOtp(String otp, String txnId) {
		NdhmOtp response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/verifyOtp";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("otp", otp);
			orderRequest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();

			//response = output.toString();//
			response=mapper.readValue(output.toString(),NdhmOtp.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public NdhmOtpStatus resendOtp(String txnId) {
		NdhmOtpStatus response = new NdhmOtpStatus();
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/resendOtp";
			JSONObject orderRequest = new JSONObject();

			orderRequest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());

			System.out.println("Bearer " + oauth.getAccessToken());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();
			 int responseCode=con.getResponseCode(); 
			 if(responseCode ==200)
			 {
				 response.setStatus(true);
			 }
			
			//response = mapper.readValue(output.toString(),NdhmOtpStatus.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public HealthIdResponse createHealthId(HealthIdRequest request) {
		HealthIdResponse response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/createHealthId";
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
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());

			System.out.println("Bearer" + oauth.getAccessToken());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			
			String outputString = output.toString();
			outputString = outputString.replaceFirst("new", "isNew");
			System.out.println("outputString:" + outputString);
			ObjectMapper mapper = new ObjectMapper();

			response = mapper.readValue(outputString, HealthIdResponse.class);

			System.out.println("output" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public List<NDHMStates> getListforStates() {
		List<NDHMStates> response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/ha/lgd/states";
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

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
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
			System.out.println("response:" + respons.toString());
			ObjectMapper mapper = new ObjectMapper();
			String output = respons.toString();

			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, NDHMStates.class);
			response = mapper.readValue(output, type);

			System.out.println("response" + respons.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public List<Districts> getListforDistricts(String statecode) {
		List<Districts> response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/ha/lgd/districts?stateCode=" + statecode;
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

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
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
			System.out.println("response:" + respons.toString());
			ObjectMapper mapper = new ObjectMapper();
			String output = respons.toString();

			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Districts.class);
			response = mapper.readValue(output, type);
			System.out.println("response" + respons.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public NdhmStatus existsByHealthId(String healthId) {
		NdhmStatus response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/search/existsByHealthId";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("healthId", healthId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			int responseCode = con.getResponseCode();
//			if (responseCode == 200)
//				response = true;
//			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();

			// response =output.toString();//
			response= mapper.readValue(output.toString(),NdhmStatus.class);
			// System.out.println("response"+output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public HealthIdSearch searchByHealthId(String healthId) {
		HealthIdSearch response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/search/searchByHealthId";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("healthId", healthId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			ObjectMapper mapper = new ObjectMapper();

			response = mapper.readValue(output.toString(), HealthIdSearch.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public HealthIdSearch searchBymobileNumber(HealthIdSearchRequest request) {
		HealthIdSearch response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/search/searchByMobile";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("gender", request.getGender());
			orderRequest.put("mobile", request.getMobile());
			orderRequest.put("name", request.getName());
			orderRequest.put("yearOfBirth", request.getYearOfBirth());

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			ObjectMapper mapper = new ObjectMapper();

			response = mapper.readValue(output.toString(), HealthIdSearch.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	// auth
	@Override
	public NdhmOtp sendAuthPassword(String healthId, String password) {
		NdhmOtp response = null;

		try {
			NdhmOauthResponse oauth = session();

			ObjectMapper mapper = new ObjectMapper();
			JSONObject requestBody = new JSONObject();
			requestBody.put("healthId", healthId);
			requestBody.put("password", password);

			System.out.println(requestBody);
			String url = "https://healthidsbx.ndhm.gov.in/api/v1/auth/authPassword";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestBody.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();

			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			response= mapper.readValue(output.toString(),NdhmOtp.class);

//			response = output.toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public NdhmOtp sendAuthWithMobile(String healthid) {
		NdhmOtp response = null;

		try {
			NdhmOauthResponse oauth = session();

			ObjectMapper mapper = new ObjectMapper();
			JSONObject requestBody = new JSONObject();
			requestBody.put("healthid", healthid);

			System.out.println(requestBody);
			String url = "https://healthidsbx.ndhm.gov.in/api/v1/auth/authWithMobile";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestBody.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();

			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());

//			response = output.toString();
			 response= mapper.readValue(output.toString(),NdhmOtp.class);


		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public NdhmOtp sendAuthWithMobileToken(MobileTokenRequest request) {
		NdhmOtp response = null;

		try {
			NdhmOauthResponse oauth = session();

			ObjectMapper mapper = new ObjectMapper();
			String url = "https://healthidsbx.ndhm.gov.in/api/v1/auth/authWithMobileToken";
//				String authStr = keyId + ":" + secret;
//				String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(request.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());

//				OrderReponse list = mapper.readValue(output.toString(), ass);

//			response = output.toString();
			response= mapper.readValue(output.toString(),NdhmOtp.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public NdhmOtp sendAuthInit(String healthId, String authMethod) {
		NdhmOtp response = null;

		try {
			NdhmOauthResponse oauth = session();

			ObjectMapper mapper = new ObjectMapper();
			JSONObject requestBody = new JSONObject();
			requestBody.put("healthid", healthId);
			requestBody.put("authMethod", authMethod);

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/auth/init";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestBody.toString());
			System.out.println("request:" + requestBody.toString());

			wr.flush();
			wr.close();
			con.disconnect();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			System.out.println(output);
			while ((inputLine = in.readLine()) != null) {

				output.append(inputLine);
				System.out.println("response:" + output.toString());
			}

//				OrderReponse list = mapper.readValue(output.toString(), ass);

//			response = output.toString();
			response= mapper.readValue(output.toString(),NdhmOtp.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public NdhmOtp confirmWithMobileOTP(String otp, String txnId) {
		NdhmOtp response = null;

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/auth/confirmWithMobileOTP";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("otp", otp);
			orderRequest.put("txnId", txnId);

			System.out.println(orderRequest.toString());
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			
			
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();

			String inputLine;
			System.out.println(con.getErrorStream());
			System.out.println("in" + in.toString());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			 ObjectMapper mapper = new ObjectMapper();

			System.out.println("response:" + output.toString());
			response= mapper.readValue(output.toString(),NdhmOtp.class);
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public NdhmOtp confirmWithAadhaarOtp(String otp, String txnId) {
		NdhmOtp response = null;

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/auth/confirmWithAadhaarOtp";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("otp", otp);
			orderRequest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();

			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();
//			response = output.toString();
			response= mapper.readValue(output.toString(),NdhmOtp.class);

//			response = output.toString();// mapper.readValue(output.toString(),Strin.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	// aadh
	@Override
	public NdhmOtp aadharGenerateOtp(String aadhaar) {
		NdhmOtp response = null;

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token " + oauth.getAccessToken());

			ObjectMapper mapper = new ObjectMapper();
			JSONObject requestBody = new JSONObject();
			requestBody.put("aadhaar", aadhaar);
			System.out.println("request:" + requestBody);

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/aadhaar/generateOtp";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestBody.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();

			String inputLine;
			System.out.println(con.getErrorStream());
			System.out.println("in" + in.toString());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());

//			response.setData(output.toString());
			response= mapper.readValue(output.toString(),NdhmOtp.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
//				throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> aadharGenerateMobileOtp(String mobile, String txnId) {

		Response<Object> response = new Response<Object>();

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/aadhaar/generateMobileOTP";
					//"https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/generateMobileOTP";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("mobile", mobile);
			orderRequest.put("txnId", txnId);
			System.out.println("Orderrequest:" + orderRequest.toString());

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();
//			response.setData(output.toString());
			 response.setData( mapper.readValue(output.toString(),NdhmOtp.class));


//			response = output.toString();// mapper.readValue(output.toString(),Strin.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> aadharVerifyOtp(String otp, String restrictions, String txnId) {

		Response<Object> response = new Response<Object>();

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/aadhaar/verifyOTP";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("otp", otp);
			orderRequest.put("restrictions", restrictions);
			orderRequest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();
//			response.setData(output.toString());
			 response.setData(mapper.readValue(output.toString(),NdhmOtp.class));


//			response = output.toString();// mapper.readValue(output.toString(),Strin.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> aadharVerifyMobileOtp(String otp, String txnId) {
		Response<Object> response = new Response<Object>();

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/aadhaar/verifyMobileOTP";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("otp", otp);
			orderRequest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();

			System.out.println("hed" + con.getHeaderFields());
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();
//			response.setData(output.toString());
			 response.setData(mapper.readValue(output.toString(),NdhmOtp.class));


//				response = output.toString();// mapper.readValue(output.toString(),Strin.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> createHealthIdWithAadhaarOtp(CreateAadhaarRequest request) {
		Response<Object> response = new Response<Object>();
		JSONObject orderRequest = new JSONObject();

		
		orderRequest.put("email", request.getEmail());
		orderRequest.put("firstName", request.getFirstName());
		orderRequest.put("lastName", request.getLastName());
		orderRequest.put("middleName", request.getMiddleName());
		orderRequest.put("mobile", request.getMobile());
		orderRequest.put("otp", request.getOtp());
		orderRequest.put("password", request.getPassword());
		orderRequest.put("restrictions", request.getRestrictions());
		orderRequest.put("profilePhoto", request.getProfilePhoto());
		orderRequest.put("txnId", request.getTxnId());
		orderRequest.put("username", request.getUsername());

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/aadhaar/createHealthIdWithAadhaarOtp";
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("email", otp);
//			orderRequest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("X-HIP-ID", NDHM_CLIENTID);
			//con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();

			System.out.println("hed" + con.getHeaderFields());
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			String respons=output.toString();
			respons = respons.replaceFirst("new", "isNew");
			System.out.println("outputString:" + respons);
		
			 ObjectMapper mapper = new ObjectMapper();
				response.setData(mapper.readValue(respons,GetCardProfileResponse.class));

//			response.setData(output.toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> resendAadhaarOtp(String txnId) {
		Response<Object> response = new Response<Object>();

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/registration/aadhaar/resendAadhaarOtp";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();
//			response.setData(output.toString());
				response.setData(mapper.readValue(output.toString(),GetCardProfileResponse.class));


		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseEntity<byte[]> profileGetCard(String authToken) {
		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/account/getCard";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-Token", "Bearer " + authToken);

			System.out.println("responseCode......"+con.getResponseCode()+"...."+con.getInputStream());			
			String disposition = con.getHeaderField("Content-Disposition");
            String fileName = "";
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length());
                }	
            }
            
            byte[] output = con.getInputStream().readAllBytes();

    	    HttpHeaders responseHeaders = new HttpHeaders();
    	    responseHeaders.set("charset", "utf-8");
    	    responseHeaders.setContentType(org.springframework.http.MediaType.valueOf("application/pdf"));
    	    responseHeaders.setContentLength(output.length);
    	    responseHeaders.set("Content-disposition", "attachment; filename="+fileName);

    	    return new ResponseEntity<byte[]>(output, responseHeaders, HttpStatus.OK);

//					    return ResponseEntity
//					            .ok()
//					            .header("Content-Disposition",
//					                    "attachment; filename="+fileName)
//					            .contentLength(con.getContentLength())
//					            .contentType(
//					                    MediaType.APPLICATION_OCTET_STREAM)
//					            .body(new InputStreamResource(con.getInputStream()));
//			String inputLine;
//			/* response = new StringBuffer(); */
//			StringBuffer respons = new StringBuffer();
//			while ((inputLine = in.readLine()) != null) {
//
//				respons.append(inputLine);
//
//			}
//		//	in.close();
//			System.out.println("response:" + respons.toString());
//			ObjectMapper mapper = new ObjectMapper();
//			String output = respons.toString();
//
//			
//			 // always check HTTP response code first
//	        if (responseCode == HttpURLConnection.HTTP_OK) {
//	            String fileName = "";
//	            String disposition = con.getHeaderField("Content-Disposition");
//	            String contentType = con.getContentType();
//	            int contentLength = con.getContentLength();
//	 
//	            if (disposition != null) {
//	                // extracts file name from header field
//	                int index = disposition.indexOf("filename=");
//	                if (index > 0) {
//	                    fileName = disposition.substring(index + 10,
//	                            disposition.length() - 1);
//	                }
//	            } else {
//	                // extracts file name from URL
//	                fileName = url.substring(url.lastIndexOf("/") + 1,
//	                		url.length());
//	            }
//	 
//	            System.out.println("Content-Type = " + contentType);
//	            System.out.println("Content-Disposition = " + disposition);
//	            System.out.println("Content-Length = " + contentLength);
//	            System.out.println("fileName = " + fileName);
//	 
//	            String saveDir = "Downloads";
//	            fileName="idCard.pdf";
//	            // opens input stream from the HTTP connection
//	            InputStream inputStream = con.getInputStream();
//	            String saveFilePath = saveDir + File.separator + fileName;
//	            System.out.println("bytes read"+inputStream);
//	            // opens an output stream to save into file
//	            FileOutputStream outputStream = new FileOutputStream("/home/ubuntu/idcard.pdf");
//	 
//	            int bytesRead = -1;
//	            byte[] buffer = new byte[BUFFER_SIZE];
//	            while ((bytesRead = inputStream.read(buffer)) != -1) {
//	            	 System.out.println("bytes read"+bytesRead);
//	            	outputStream.write(buffer, 0, bytesRead);
//	               
//	            }
//	 
////	            outputStream.close();
////	            inputStream.close();
//	 
//	            System.out.println("File downloaded");
//	        } else {
//	            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
//	        }
//	        
//	        
////			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, GetCardProfileResponse.class);
////			response = mapper.readValue(output,type);
//
//			System.out.println("response" + respons.toString());
//
////			response.setData(respons.toString());
//			//output.replaceFirst("new", "isNew");
//			//response.setData(mapper.readValue(output.toString(),GetCardProfileResponse.class));
//response.setData(respons);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		//return response;
	}

	@Override
	public Response<Object> profileGetPngCard(String authToken) {
		Response<Object> response = new Response<Object>();

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/account/getPngCard";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-Token", "Bearer " + authToken);

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */
			StringBuffer respons = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {

				respons.append(inputLine);

			}
			in.close();
			System.out.println("response:" + respons.toString());
			ObjectMapper mapper = new ObjectMapper();
			String output = respons.toString();

//						JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, GetCardProfileResponse.class);
//						response = mapper.readValue(output,type);

			System.out.println("response" + respons.toString());

//			response.setData(respons.toString());
			response.setData(mapper.readValue(output.toString(),GetCardProfileResponse.class));


		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getProfileDetail(String authToken) {
		Response<Object> response = new Response<Object>();

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/account/profile";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-Token", "Bearer " + authToken);

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */
			StringBuffer respons = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {

				respons.append(inputLine);

			}
			in.close();
			System.out.println("response:" + respons.toString());
			ObjectMapper mapper = new ObjectMapper();
			String output = respons.toString();

//						JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, GetCardProfileResponse.class);
//						response = mapper.readValue(output,type);

			System.out.println("response" + respons.toString());
			output = output.replaceFirst("new", "isNew");
			System.out.println("outputString:" + output);
//			response.setData(respons.toString());
			response.setData(mapper.readValue(output.toString(),GetCardProfileResponse.class));


		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> createProfile(CreateProfileRequest request, String authToken) {
		Response<Object> response = new Response<Object>();

		JSONObject orderRequest = new JSONObject();

		orderRequest.put("address", request.getAddress());
		orderRequest.put("districtCode", request.getDistrictCode());
		orderRequest.put("email", request.getEmail());
		orderRequest.put("firstName", request.getFirstName());
		orderRequest.put("pincode", request.getPincode());
		orderRequest.put("healthId", request.getHealthId());
		orderRequest.put("lastName", request.getLastName());
		orderRequest.put("middleName", request.getMiddleName());
		orderRequest.put("password", request.getPassword());
		orderRequest.put("profilePhoto", request.getProfilePhoto());
		orderRequest.put("stateCode", request.getStateCode());
		orderRequest.put("subdistrictCode", request.getSubdistrictCode());
		orderRequest.put("townCode", request.getTownCode());
		orderRequest.put("villageCode", request.getVillageCode());
		orderRequest.put("wardCode", request.getWardCode());
		
		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/account/profile";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-Token", "Bearer " + authToken);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			 ObjectMapper mapper = new ObjectMapper();
//			response.setData(output.toString());
			response.setData(mapper.readValue(output.toString(),GetCardProfileResponse.class));


		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> DeleteProfileDetail(String authToken) {
		Response<Object> response = new Response<Object>();

		try {

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://healthidsbx.ndhm.gov.in/api/v1/account/profile";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("DELETE");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-Token", "Bearer " + authToken);
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(orderRequest.toString());
//			System.out.println("Orderrequest:" + orderRequest.toString());
//			wr.flush();
//			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			// ObjectMapper mapper = new ObjectMapper();
			response.setData(output.toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean fetchModes(FetchModesRequest request) {
		Boolean response=false;
		try {
			System.out.println("requestId"+request.getRequestId());
			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();	
			
			orderRequest1.put("id", request.getQuery().getId());
			orderRequest1.put("purpose", request.getQuery().getPurpose());
			orderRequest1.put("requester", orderRequest2);
			
				
			orderRequest2.put("id", request.getQuery().getRequester().getId());
			orderRequest2.put("type", request.getQuery().getRequester().getType());
			
			
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimeStamp());
			orderRequest.put("query",orderRequest1 );
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/users/auth/fetch-modes";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-CM-ID","sbx" );
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			int responseCode = con.getResponseCode();
			if(responseCode ==202)
				response=true;
			
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	
	
	@Override
	public Boolean onFetchModes(OnFetchModesRequest request) {
		Boolean response=false;
		try {
			
		OnFetchModeCollection collection=new OnFetchModeCollection();

		BeanUtil.map(request, collection);
		collection.setCreatedTime(new Date());
		onFetchModeRepository.save(collection);
			response=true;
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public OnFetchModesRequest getFetchModes(String requestId)
	{
		OnFetchModesRequest response=null;
		try {
			OnFetchModeCollection collection=onFetchModeRepository.findByRespRequestId(requestId);
		if(collection !=null)
		{
			response=new OnFetchModesRequest();
			BeanUtil.map(collection, response);
		}
		
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}
	
	
	@Override
	public Boolean authInit(FetchModesRequest request) {
		Boolean response=false;
		try {
			
			System.out.println("requestId"+request.getRequestId());
			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();	
			
			orderRequest1.put("id", request.getQuery().getId());
			orderRequest1.put("purpose", request.getQuery().getPurpose());
			orderRequest1.put("authMode", request.getQuery().getAuthMode());
			orderRequest1.put("requester", orderRequest2);
			
				
			orderRequest2.put("id", request.getQuery().getRequester().getId());
			orderRequest2.put("type", request.getQuery().getRequester().getType());
			
			
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimeStamp());
			orderRequest.put("query",orderRequest1 );
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/users/auth/init";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-CM-ID","sbx" );
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			int responseCode = con.getResponseCode();
			if(responseCode ==202)
				response=true;
			
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public Boolean onAuthinit(OnAuthInitRequest request) {
		Boolean response=false;
		try {
			
			OnAuthInitCollection collection=new OnAuthInitCollection();

		BeanUtil.map(request, collection);
		collection.setCreatedTime(new Date());
		onAuthInitRepository.save(collection);
			response=true;
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	
	@Override
	public OnAuthInitRequest getOnAuthInit(String requestId)
	{
		OnAuthInitRequest response=null;
		try {
			OnAuthInitCollection  collection=onAuthInitRepository.findByRespRequestId(requestId);
		if(collection !=null)
		{
			response=new OnAuthInitRequest();
			BeanUtil.map(collection, response);
		}
		
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}

	
	
	@Override
	public Boolean authConfirm(AuthConfirmRequest request) {
		Boolean response=false;
		try {
			
			System.out.println("requestId"+request.getRequestId());
			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();	
			JSONObject orderRequest3 = new JSONObject();	
			
			if(request.getCredential().getAuthCode() !=null)
			orderRequest1.put("authCode", request.getCredential().getAuthCode());
			
			if(request.getCredential().getDemographic() !=null) {
			orderRequest1.put("demographic", orderRequest3);
			
			orderRequest3.put("name", request.getCredential().getDemographic().getName());
			orderRequest3.put("gender", request.getCredential().getDemographic().getGender());
			orderRequest3.put("dateOfBirth", request.getCredential().getDemographic().getDateOfBirth());
			orderRequest3.put("identifier", orderRequest2);	
			orderRequest2.put("type", request.getCredential().getDemographic().getIdentifier().getType());
			orderRequest2.put("value", request.getCredential().getDemographic().getIdentifier().getValue());
			}
			
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			orderRequest.put("transactionId", request.getTransactionId());
			orderRequest.put("credential",orderRequest1 );
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/users/auth/confirm";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-CM-ID","sbx" );
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			int responseCode = con.getResponseCode();
			if(responseCode ==202)
				response=true;
			
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public Boolean onAuthConfirm(OnAuthConfirmRequest request) {
		Boolean response=false;
		try {
			
			OnAuthConfirmCollection collection=new OnAuthConfirmCollection();

		BeanUtil.map(request, collection);
		collection.setCreatedTime(new Date());
		onAuthConfirmRepository.save(collection);
			response=true;
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public OnAuthConfirmRequest getOnAuthConfirm(String requestId)
	{
		OnAuthConfirmRequest response=null;
		try {
			OnAuthConfirmCollection  collection=onAuthConfirmRepository.findByRespRequestId(requestId);
		if(collection !=null)
		{
			response= new OnAuthConfirmRequest();
			BeanUtil.map(collection, response);
		}
		
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}

	@Override
	public Boolean addCareContext(CareContextRequest request) {
		Boolean response=false;
		try {
			
			
			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();	
			JSONObject orderRequest3 = new JSONObject();	
			
			
			orderRequest1.put("accessToken", request.getLink().getAccessToken());
			
		
			orderRequest1.put("patient", orderRequest2);
			
			orderRequest2.put("referenceNumber", request.getLink().getPatient().getReferenceNumber());
			orderRequest2.put("display", request.getLink().getPatient().getDisplay());
			orderRequest2.put("careContexts", orderRequest3);	
			orderRequest3.put("referenceNumber", request.getLink().getPatient().getCareContexts().getReferenceNumber());
			orderRequest3.put("display", request.getLink().getPatient().getCareContexts().getDisplay());
			
			
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			
			orderRequest.put("link",orderRequest1 );
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/links/link/add-contexts";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-CM-ID","sbx" );
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			int responseCode = con.getResponseCode();
			if(responseCode ==202)
				response=true;
			
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public Boolean onCareContext(OnCareContext request) {
		Boolean response =false;
		try {
			OnCareContextCollection collection=new OnCareContextCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			onCareContextRepository.save(collection);
			response=true;
			
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discover(CareContextDiscoverRequest request) {
		Boolean response=false;
		try {
			CareContextDiscoverCollection collection= new CareContextDiscoverCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			careContextDiscoverRepository.save(collection);
			response=true;
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
		
	}
	
	@Override
	public CareContextDiscoverRequest getCareContextDiscover(String requestId) {
		CareContextDiscoverRequest response=null;
		try {
			CareContextDiscoverCollection collection=careContextDiscoverRepository.findByRequestId(requestId);
			if(collection !=null)
			{
				response=new CareContextDiscoverRequest();
				BeanUtil.map(collection, response);
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onDiscover(OnDiscoverRequest request) {
		Boolean response =false;
		try {
			
//			JSONObject orderRequest = new JSONObject();
//
//			JSONObject orderRequest1 = new JSONObject();
//			JSONObject orderRequest2 = new JSONObject();	
//			JSONObject orderRequest3 = new JSONObject();
//			JSONObject orderRequest4 = new JSONObject();
//			JSONArray array=new JSONArray();
//			
//			orderRequest1.put("referenceNumber", request.getPatient().getReferenceNumber());
//			orderRequest1.put("display", request.getPatient().getDisplay());
//			orderRequest1.put("referenceNumber", request.getPatient().getReferenceNumber());
//			
//		
//			orderRequest1.put("careContexts", orderRequest2);
//			
//			orderRequest2.put("referenceNumber", request.getPatient().getCareContexts().);
//			orderRequest2.put("display", request.getPatient().getDisplay());
//			
//			array.put(request.getPatient().getMatchedBy());
//			orderRequest2.put("matchedBy", array);	
//			orderRequest2.put("error",orderRequest3);
//			orderRequest3.put("code", request.getError().getCode());
//			orderRequest3.put("message", request.getError().getMessage());
//			orderRequest2.put("resp",orderRequest4);
//			orderRequest4.put("requestId", request.getResp().getRequestId());
//			
//			orderRequest.put("requestId", request.getRequestId());
//			orderRequest.put("timestamp", request.getTimestamp());
//			orderRequest.put("transactionId", request.getTransactionId());
//			
//			
//			orderRequest.put("patient",orderRequest1 );
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/links/link/add-contexts";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-CM-ID","sbx" );
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(request.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			int responseCode = con.getResponseCode();
			if(responseCode ==202)
				response=true;

			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean linkInit(LinkRequest request) {
		Boolean response=false;
		try {
			LinkInitCollection collection=new LinkInitCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			linkInitRepository.save(collection);
			response=true;
			
		}
		
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public Boolean onLinkInit(OnLinkRequest request) {
		Boolean response=false;
		try {
			
			
//			JSONObject orderRequest = new JSONObject();
//
//			JSONObject orderRequest1 = new JSONObject();
//			JSONObject orderRequest2 = new JSONObject();	
//			JSONObject orderRequest3 = new JSONObject();	
//			
//			
//			orderRequest1.put("accessToken", request.getLink().getAccessToken());
//			
//		
//			orderRequest1.put("patient", orderRequest2);
//			
//			orderRequest2.put("referenceNumber", request.getLink().getPatient().getReferenceNumber());
//			orderRequest2.put("display", request.getLink().getPatient().getDisplay());
//			orderRequest2.put("careContexts", orderRequest3);	
//			orderRequest3.put("referenceNumber", request.getLink().getPatient().getCareContexts().getReferenceNumber());
//			orderRequest3.put("display", request.getLink().getPatient().getCareContexts().getDisplay());
//			
//			
//			orderRequest.put("requestId", request.getRequestId());
//			orderRequest.put("timestamp", request.getTimestamp());
//			
//			orderRequest.put("link",orderRequest1 );
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/links/link/on-init";
//			JSONObject orderRequest = new JSONObject();
//			orderRquest.put("txnId", txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			con.setRequestProperty("X-CM-ID","sbx" );
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(request.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			int responseCode = con.getResponseCode();
			if(responseCode ==202)
				response=true;
			
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public OnLinkRequest getLinkInit(String requestId) {
		OnLinkRequest response=null;
		try {
		LinkInitCollection collection=linkInitRepository.findByRequestId(requestId);
		response=new OnLinkRequest();
		BeanUtil.map(collection, response);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean linkConfirm(LinkConfirm request) {
		Boolean response=false;
		try {
			
			LinkConfirmCollection collection=new LinkConfirmCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			linkConfirmRepository.save(collection);
			response=true;
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onLinkConfirm(OnLinkConfirm request) {
		Boolean response=false;
		try {
		
		NdhmOauthResponse oauth = session();
		System.out.println("token" + oauth.getAccessToken());

		String url = "https://dev.ndhm.gov.in/gateway/v0.5/links/link/on-confirm";
//		JSONObject orderRequest = new JSONObject();
//		orderRquest.put("txnId", txnId);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setDoOutput(true);

		System.out.println(con.getErrorStream());
		con.setDoInput(true);
		// optional default is POST
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
		con.setRequestProperty("X-CM-ID","sbx" );
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(request.toString());
		wr.flush();
		wr.close();
		con.disconnect();
		InputStream in = con.getInputStream();
		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(con.getInputStream()));
		String inputLine;
		System.out.println(con.getErrorStream());
		/* response = new StringBuffer(); */
		StringBuffer output = new StringBuffer();
		int c = 0;
		while ((c = in.read()) != -1) {

			output.append((char) c);

		}
		System.out.println("response:" + output.toString());
		int responseCode = con.getResponseCode();
		if(responseCode ==202)
			response=true;
		
	}

	catch (Exception e) {
		e.printStackTrace();
		logger.error("Error : " + e.getMessage());
		throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;

	}

	@Override
	public Boolean ndhmNotify(NotifyRequest request) {
	Boolean response=false;
		try {
		NdhmNotifyCollection collection=new NdhmNotifyCollection();
		BeanUtil.map(request, collection);
		collection.setCreatedTime(new Date());
		ndhmNotifyRepository.save(collection);
		response=true;
		
	}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onNotify(OnNotifyRequest request) {
		Boolean response=false;
		try {
		NdhmOauthResponse oauth = session();
		System.out.println("token" + oauth.getAccessToken());

		String url = "https://dev.ndhm.gov.in/gateway/v0.5/consents/hip/on-notify";
//		JSONObject orderRequest = new JSONObject();
//		orderRquest.put("txnId", txnId);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setDoOutput(true);

		System.out.println(con.getErrorStream());
		con.setDoInput(true);
		// optional default is POST
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
		con.setRequestProperty("X-CM-ID","sbx" );
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(request.toString());
		wr.flush();
		wr.close();
		con.disconnect();
		InputStream in = con.getInputStream();
		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(con.getInputStream()));
		String inputLine;
		System.out.println(con.getErrorStream());
		/* response = new StringBuffer(); */
		StringBuffer output = new StringBuffer();
		int c = 0;
		while ((c = in.read()) != -1) {

			output.append((char) c);

		}
		System.out.println("response:" + output.toString());
		int responseCode = con.getResponseCode();
		if(responseCode ==202)
			response=true;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;


	}
	
	

	
	
	
}
