package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AcknowledgementRequest;
import com.dpdocter.beans.AuthConfirmRequest;
import com.dpdocter.beans.CareContext;
import com.dpdocter.beans.CareContextDiscoverRequest;
import com.dpdocter.beans.CareContextRequest;
import com.dpdocter.beans.ConsentFetchRequest;
import com.dpdocter.beans.DataEncryptionResponse;
import com.dpdocter.beans.DiscoverPatientResponse;
import com.dpdocter.beans.Districts;
import com.dpdocter.beans.FetchModesRequest;
import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.HealthInfoNotify;
import com.dpdocter.beans.HipInfoNotify;
import com.dpdocter.beans.HipNotifier;
import com.dpdocter.beans.HiuDataRequest;
import com.dpdocter.beans.HiuOnNotify;
import com.dpdocter.beans.LinkConfirm;
import com.dpdocter.beans.LinkConfirmPatient;
import com.dpdocter.beans.LinkInitCollection;
import com.dpdocter.beans.LinkMeta;
import com.dpdocter.beans.LinkRequest;
import com.dpdocter.beans.LinkResponse;
import com.dpdocter.beans.MobileTokenRequest;
import com.dpdocter.beans.NDHMPrecriptionRecordData;
import com.dpdocter.beans.NDHMRecordDataCode;
import com.dpdocter.beans.NDHMRecordDataDosageInstruction;
import com.dpdocter.beans.NDHMRecordDataRequester;
import com.dpdocter.beans.NDHMRecordDataResource;
import com.dpdocter.beans.NDHMRecordDataSubject;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.beans.NdhmOnPatientFindRequest;
import com.dpdocter.beans.NdhmOtp;
import com.dpdocter.beans.NdhmOtpStatus;
import com.dpdocter.beans.NdhmPatientRequest;
import com.dpdocter.beans.NdhmStatus;
import com.dpdocter.beans.NotifyHiuRequest;
import com.dpdocter.beans.NotifyRequest;
import com.dpdocter.beans.OnAuthConfirmCollection;
import com.dpdocter.beans.OnAuthConfirmRequest;
import com.dpdocter.beans.OnAuthInitRequest;
import com.dpdocter.beans.OnCareContext;
import com.dpdocter.beans.OnConsentFetchRequest;
import com.dpdocter.beans.OnConsentRequestStatus;
import com.dpdocter.beans.OnDiscoverRequest;
import com.dpdocter.beans.OnFetchModesRequest;
import com.dpdocter.beans.OnLinkConfirm;
import com.dpdocter.beans.OnLinkRequest;
import com.dpdocter.beans.OnNotifyRequest;
import com.dpdocter.beans.PatientVisitLookupBean;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.StatusNotify;
import com.dpdocter.beans.StatusResponse;
import com.dpdocter.collections.CareContextDiscoverCollection;
import com.dpdocter.collections.ConsentFetchRequestCollection;
import com.dpdocter.collections.ConsentInitCollection;
import com.dpdocter.collections.ConsentOnInitRequestCollection;
import com.dpdocter.collections.DischargeSummaryCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HipDataFlowCollection;
import com.dpdocter.collections.HiuDataRequestCollection;
import com.dpdocter.collections.HiuDataTransferCollection;
import com.dpdocter.collections.HiuNotifyCollection;
import com.dpdocter.collections.LinkConfirmCollection;
import com.dpdocter.collections.NdhmNotifyCollection;
import com.dpdocter.collections.NdhmPatientFindCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.collections.OnAuthInitCollection;
import com.dpdocter.collections.OnCareContextCollection;
import com.dpdocter.collections.OnConsentRequestStatusCollection;
import com.dpdocter.collections.OnFetchModeCollection;
import com.dpdocter.collections.OperationNoteCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.NDHMRecordDataResourceType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CareContextDiscoverRepository;
import com.dpdocter.repository.ConsentFetchRepository;
import com.dpdocter.repository.ConsentInitRepository;
import com.dpdocter.repository.ConsentStatusRequestRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HealthDataFlowRepository;
import com.dpdocter.repository.HipDataFlowRepository;
import com.dpdocter.repository.HiuConsentRequestInitRepository;
import com.dpdocter.repository.HiuDataRequestRepository;
import com.dpdocter.repository.HiuDataTransferRepository;
import com.dpdocter.repository.HiuNotifyRepository;
import com.dpdocter.repository.LinkConfirmRepository;
import com.dpdocter.repository.LinkInitRepository;
import com.dpdocter.repository.NdhmNotifyRepository;
import com.dpdocter.repository.NdhmPatientFindRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.OnAuthConfirmRepository;
import com.dpdocter.repository.OnAuthInitRepository;
import com.dpdocter.repository.OnCareContextRepository;
import com.dpdocter.repository.OnFetchModeRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ConsentOnInitRequest;
import com.dpdocter.request.CreateAadhaarRequest;
import com.dpdocter.request.CreateProfileRequest;
import com.dpdocter.request.DataFlowRequest;
import com.dpdocter.request.DataTransferRequest;
import com.dpdocter.request.DhPublicKeyDataFlowRequest;
import com.dpdocter.request.EntriesDataTransferRequest;
import com.dpdocter.request.GatewayConsentInitRequest;
import com.dpdocter.request.GatewayConsentStatusRequest;
import com.dpdocter.request.KeyMaterialRequestDataFlow;
import com.dpdocter.response.GetCardProfileResponse;
import com.dpdocter.security.DHKeyExchangeCrypto;
import com.dpdocter.security.DhKeyExchangeCryptoHiu;
import com.dpdocter.security.DischargeSummarySample;
import com.dpdocter.security.OPConsultNoteSample;
import com.dpdocter.security.PrescriptionSample;
import com.dpdocter.security.ResourcePopulator;
import com.dpdocter.services.NDHMservices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.webservices.GateWayHiOnRequest;
import com.dpdocter.webservices.GateWayOnRequest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.util.web.LoginUtils;
import common.util.web.Response;

@Service
public class NDHMserviceImpl implements NDHMservices {

	private static Logger logger = LogManager.getLogger(NDHMserviceImpl.class.getName());

	@Value(value = "${ndhm.clientId}")
	private String NDHM_CLIENTID;

	@Value(value = "${ndhm.clientSecret}")
	private String NDHM_CLIENT_SECRET;

	@Value(value = "${ndhm.hiu.clientId}")
	private String NDHM_HIU_CLIENTID;

	@Value(value = "${ndhm.hiu.clientSecret}")
	private String NDHM_HIU_CLIENT_SECRET;
	
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
	private HipDataFlowRepository hipDataFlowRepository;
	
	@Autowired
	private NdhmNotifyRepository ndhmNotifyRepository;
	
	@Autowired
	private HealthDataFlowRepository healthDataFlowRepository;
	
	@Autowired
	private ConsentInitRepository consentInitRepository;
	
	@Autowired
	private ConsentStatusRequestRepository consentStatusRequestRepository;

	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	
	@Autowired
	private PrescriptionRepository prescriptionRepository;
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private SMSServices smsServices;
	
	@Autowired
	private OTPRepository otpRepository;
	
	@Autowired
	private NdhmPatientFindRepository ndhmPatientFindRepository;
	
	@Autowired
	private HiuNotifyRepository hiuNotifyRepository;
	
	@Autowired
	private ConsentFetchRepository consentFetchRepository;
	
	
	@Autowired
	private HiuDataRequestRepository hiuDataRequestRepository;
	
	@Autowired
	private HiuDataTransferRepository hiuDataTransferRepository;
	
	@Autowired
	private HiuConsentRequestInitRepository hiuConsentRequestInitRepository;


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

			// response = output.toString();//
			response = mapper.readValue(output.toString(), NdhmOtp.class);
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

			// response = output.toString();//
			response = mapper.readValue(output.toString(), NdhmOtp.class);
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
			int responseCode = con.getResponseCode();
			if (responseCode == 200) {
				response.setStatus(true);
			}

			// response = mapper.readValue(output.toString(),NdhmOtpStatus.class);
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

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
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
			response = mapper.readValue(output.toString(), NdhmStatus.class);
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
			response = mapper.readValue(output.toString(), NdhmOtp.class);

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
			response = mapper.readValue(output.toString(), NdhmOtp.class);

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
			response = mapper.readValue(output.toString(), NdhmOtp.class);

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
			response = mapper.readValue(output.toString(), NdhmOtp.class);

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
			response = mapper.readValue(output.toString(), NdhmOtp.class);

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
			response = mapper.readValue(output.toString(), NdhmOtp.class);

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
			response = mapper.readValue(output.toString(), NdhmOtp.class);

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
			// "https://healthidsbx.ndhm.gov.in/api/v1/registration/mobile/generateMobileOTP";
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
			response.setData(mapper.readValue(output.toString(), NdhmOtp.class));

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
			response.setData(mapper.readValue(output.toString(), NdhmOtp.class));

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
			response.setData(mapper.readValue(output.toString(), NdhmOtp.class));

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
			// con.setRequestProperty("Accept-Language", "en-US");
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
			String respons = output.toString();
			respons = respons.replaceFirst("new", "isNew");
			System.out.println("outputString:" + respons);

			ObjectMapper mapper = new ObjectMapper();
			response.setData(mapper.readValue(respons, GetCardProfileResponse.class));

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
			response.setData(mapper.readValue(output.toString(), GetCardProfileResponse.class));

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

			System.out.println("responseCode......" + con.getResponseCode() + "...." + con.getInputStream());
			String disposition = con.getHeaderField("Content-Disposition");
			String fileName = "";
			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10, disposition.length());
				}
			}

			byte[] output = con.getInputStream().readAllBytes();

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("charset", "utf-8");
			responseHeaders.setContentType(org.springframework.http.MediaType.valueOf("application/pdf"));
			responseHeaders.setContentLength(output.length);
			responseHeaders.set("Content-disposition", "attachment; filename=" + fileName);

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
		// return response;
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
			response.setData(mapper.readValue(output.toString(), GetCardProfileResponse.class));

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
			response.setData(mapper.readValue(output.toString(), GetCardProfileResponse.class));

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
			response.setData(mapper.readValue(output.toString(), GetCardProfileResponse.class));

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
		Boolean response = false;
		try {
			System.out.println("requestId" + request.getRequestId());
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
			orderRequest.put("query", orderRequest1);

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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

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
		Boolean response = false;
		try {

			OnFetchModeCollection collection = new OnFetchModeCollection();

			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			onFetchModeRepository.save(collection);
			response = true;
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public OnFetchModesRequest getFetchModes(String requestId) {
		OnFetchModesRequest response = null;
		try {
			OnFetchModeCollection collection = onFetchModeRepository.findByRespRequestId(requestId);
			if (collection != null) {
				response = new OnFetchModesRequest();
				BeanUtil.map(collection, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public Boolean authInit(FetchModesRequest request) {
		Boolean response = false;
		try {

			System.out.println("requestId" + request.getRequestId());
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
			orderRequest.put("query", orderRequest1);

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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

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
		Boolean response = false;
		try {

			OnAuthInitCollection collection = new OnAuthInitCollection();

			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			onAuthInitRepository.save(collection);
			response = true;
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public OnAuthInitRequest getOnAuthInit(String requestId) {
		OnAuthInitRequest response = null;
		try {
			OnAuthInitCollection collection = onAuthInitRepository.findByRespRequestId(requestId);
			if (collection != null) {
				response = new OnAuthInitRequest();
				BeanUtil.map(collection, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public Boolean authConfirm(AuthConfirmRequest request) {
		Boolean response = false;
		try {

			System.out.println("requestId" + request.getRequestId());
			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();
			JSONObject orderRequest3 = new JSONObject();

			if (request.getCredential().getAuthCode() != null)
				orderRequest1.put("authCode", request.getCredential().getAuthCode());

			if (request.getCredential().getDemographic() != null) {
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
			orderRequest.put("credential", orderRequest1);

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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

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
		Boolean response = false;
		try {

			OnAuthConfirmCollection collection = new OnAuthConfirmCollection();

			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			onAuthConfirmRepository.save(collection);
			response = true;
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public OnAuthConfirmRequest getOnAuthConfirm(String requestId) {
		OnAuthConfirmRequest response = null;
		try {
			OnAuthConfirmCollection collection = onAuthConfirmRepository.findByRespRequestId(requestId);
			if (collection != null) {
				response = new OnAuthConfirmRequest();
				BeanUtil.map(collection, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public Boolean addCareContext(CareContextRequest request) {
		Boolean response = false;
		try {

			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();
			JSONObject orderRequest3 = new JSONObject();

			orderRequest1.put("accessToken", request.getLink().getAccessToken());

			orderRequest1.put("patient", orderRequest2);

			orderRequest2.put("referenceNumber", request.getLink().getPatient().getReferenceNumber());
			orderRequest2.put("display", request.getLink().getPatient().getDisplay());
			orderRequest2.put("careContexts", request.getLink().getPatient().getCareContexts());
			
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());

			orderRequest.put("link", orderRequest1);

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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

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
		Boolean response = false;
		try {
			OnCareContextCollection collection = new OnCareContextCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			onCareContextRepository.save(collection);
			response = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discover(CareContextDiscoverRequest request) {
		Boolean response = false;
		try {
			CareContextDiscoverCollection collection = new CareContextDiscoverCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			careContextDiscoverRepository.save(collection);
			response = true;
			System.out.println("response"+response);
			if(response==true)
			{
				OnDiscoverRequest discover=new OnDiscoverRequest();
				UUID uuid=UUID.randomUUID();
				discover.setRequestId(uuid.toString());
//				TimeZone tz = TimeZone.getTimeZone("UTC");
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SS"); // Quoted "Z" to indicate UTC, no timezone offset
//				df.setTimeZone(tz);
//				String nowAsISO = df.format(new Date());
		//		discover.setTimestamp(nowAsISO);
				LocalDateTime time= LocalDateTime.now(ZoneOffset.UTC);
				System.out.println("timeStamp"+time.toString());
				discover.setTimestamp(time.toString());
				discover.setTransactionId(collection.getTransactionId());
				
				String patientId=collection.getPatient().getId();
				String patientName=collection.getPatient().getName();
				String gender=request.getPatient().getGender();
				if(gender.equals("M"))
				{
					gender="MALE";
				}
				else if (gender.equals("F")) {
					gender="FEMALE";
				} else{
					gender="OTHER";
				}
				System.out.println("Gender"+gender);
				System.out.println("HealthId"+patientId);
				System.out.println("PatientName "+patientName);
				List<PatientCollection> patientCollections=patientRepository.findByHealthIdAndLocalPatientNameAndGender(patientId,patientName,gender);
				PatientCollection patientCollection=patientCollections.get(0);
				if(patientCollection !=null)
				{
					UserCollection user=userRepository.findById(patientCollection.getUserId()).orElse(null);
					otpGenerator(user.getMobileNumber(),null);
					DiscoverPatientResponse patient=new DiscoverPatientResponse();
					patient.setReferenceNumber(patientCollection.getId().toString());
					patient.setDisplay("Health-Information");
					CareContext care=new CareContext();
					care.setDisplay("Health-Information");
					care.setReferenceNumber(NDHM_CLIENTID);
					List<CareContext>careContexts=new ArrayList<CareContext>();
					careContexts.add(care);
					patient.setCareContexts(careContexts);
					List<String>matchedBy=new ArrayList<String>();
					matchedBy.add("HEALTH_ID");
				patient.setMatchedBy(matchedBy);
					discover.setPatient(patient);
					FetchResponse resp=new FetchResponse();
					resp.setRequestId(collection.getRequestId());
					discover.setResp(resp);
					Boolean status=false;
					status=onDiscover( discover);
					System.out.println("status"+status); 
				}
				
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	
	
	 public Boolean otpGenerator(String mobileNumber,String countryCode) {
	    	Boolean response = false;
		String OTP = null;
		try {
		    OTP = LoginUtils.generateOTP();
		    SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
			
			smsTrackDetail.setType(ComponentType.NDHM_OTP.getType());
			SMSDetail smsDetail = new SMSDetail();
			
		//	smsDetail.setUserName(doctorContactUs.getFirstName());
			SMS sms = new SMS();
		
		//	String link = welcomeLink + "/" + tokenCollection.getId()+"/";
		//	String shortUrl = DPDoctorUtils.urlShortner(link);
			sms.setSmsText(OTP+" is your Healthcoco OTP. Code is valid for 30 minutes only, one time use. Stay Healthy and Happy! OTPVerification");

				SMSAddress smsAddress = new SMSAddress();
			mobileNumber=mobileNumber.replaceFirst("+91", "");
			smsAddress.setRecipient(mobileNumber);
			sms.setSmsAddress(smsAddress);
			smsDetail.setSms(sms);
			smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setSmsDetails(smsDetails);
			smsServices.sendOTPSMS(smsTrackDetail, true);

		    OTPCollection otpCollection = new OTPCollection();
		    otpCollection.setCreatedTime(new Date());
		    otpCollection.setOtpNumber(OTP);
		    otpCollection.setGeneratorId(mobileNumber);
		    otpCollection.setMobileNumber(mobileNumber);
		    otpCollection.setCountryCode(countryCode);
		    otpCollection.setCreatedBy(mobileNumber);
		    otpCollection = otpRepository.save(otpCollection);

		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error While Generating OTP");
		    throw new BusinessException(ServiceError.Unknown, "Error While Generating OTP "+e.getMessage());
		}
		return response;
	    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public CareContextDiscoverRequest getCareContextDiscover(String requestId) {
		CareContextDiscoverRequest response = null;
		try {
			CareContextDiscoverCollection collection = careContextDiscoverRepository.findByRequestId(requestId);
			if (collection != null) {
				response = new CareContextDiscoverRequest();
				if(collection !=null)
				{
					BeanUtil.map(collection,response);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onDiscover(OnDiscoverRequest request) {
		Boolean response = false;
		try {

			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();	
			JSONObject orderRequest3 = new JSONObject();
			JSONObject orderRequest4 = new JSONObject();
			JSONArray array=new JSONArray();
			
			orderRequest1.put("referenceNumber", request.getPatient().getReferenceNumber());
			orderRequest1.put("display", request.getPatient().getDisplay());
			
			
		
			orderRequest1.put("careContexts",request.getPatient().getCareContexts());
			
			
			
			array.put(request.getPatient().getMatchedBy());
			System.out.println("matchedBy"+request.getPatient().getMatchedBy());
			orderRequest1.put("matchedBy", request.getPatient().getMatchedBy());	
			//orderRequest2.put("error",orderRequest3);
		//	orderRequest3.put("code", request.getError().getCode());
		//	orderRequest3.put("message", request.getError().getMessage());
			//orderRequest2.put("resp",orderRequest4);
			//orderRequest4.put("requestId", request.getResp().getRequestId());
			
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			orderRequest.put("transactionId", request.getTransactionId());
			
			
			orderRequest.put("patient",orderRequest1 );
			orderRequest.put("error",request.getError());
			orderRequest.put("resp",orderRequest2);
			orderRequest2.put("requestId",request.getResp().getRequestId());
			System.out.println("req"+orderRequest);
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/care-contexts/on-discover";
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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean linkInit(LinkRequest request) {
		Boolean response = false;
		try {
			LinkInitCollection collection = new LinkInitCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			linkInitRepository.save(collection);
			response = true;
			if(response==true)
			{
				OnLinkRequest discover=new OnLinkRequest();
				UUID uuid=UUID.randomUUID();
				discover.setRequestId(uuid.toString());
//				TimeZone tz = TimeZone.getTimeZone("UTC");
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SS"); // Quoted "Z" to indicate UTC, no timezone offset
//				df.setTimeZone(tz);
		//		String nowAsISO = df.format(new Date());
			LocalDateTime time= LocalDateTime.now(ZoneOffset.UTC);
			System.out.println("timeStamp"+time.toString());
				discover.setTimestamp(time.toString());
				discover.setTransactionId(collection.getTransactionId());
				LinkResponse link=new LinkResponse();
				link.setAuthenticationType("DIRECT");
				link.setReferenceNumber(collection.getPatient().getReferenceNumber());
				
				LinkMeta meta =new LinkMeta();
				meta.setCommunicationMedium("MOBILE");
				link.setMeta(meta);
				discover.setLink(link);
				FetchResponse resp=new FetchResponse();
				resp.setRequestId(collection.getRequestId());
				discover.setResp(resp);
		Boolean	status=	onLinkInit(discover);
		System.out.println("Status"+status);
			}
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
		Boolean response = false;
		try {

			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();	
			JSONObject orderRequest3 = new JSONObject();	
			
			
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
			
		
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			orderRequest.put("transactionId", request.getTransactionId());
			
			orderRequest1.put("referenceNumber",request.getLink().getReferenceNumber());
			orderRequest1.put("authenticationType",request.getLink().getAuthenticationType());
			orderRequest2.put("communicationMedium",request.getLink().getMeta().getCommunicationMedium());
			orderRequest1.put("meta",orderRequest2);
		    orderRequest.put("link",request.getLink());
			orderRequest.put("error",request.getError());
			orderRequest3.put("requestId",request.getResp().getRequestId());
			orderRequest.put("resp",orderRequest3);
			orderRequest.put("link", orderRequest1);
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());
			System.out.println("req"+orderRequest);
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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public LinkRequest getLinkInit(String requestId) {
		LinkRequest response = null;
		try {
			LinkInitCollection collection = linkInitRepository.findByRequestId(requestId);
			response = new LinkRequest();
			if(collection !=null)
			{
				BeanUtil.map(collection,response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean linkConfirm(LinkConfirm request) {
		Boolean response = false;
		try {

			LinkConfirmCollection collection = new LinkConfirmCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			linkConfirmRepository.save(collection);
			response = true;
			
			if(response==true)
			{
				OnLinkConfirm discover=new OnLinkConfirm();
				UUID uuid=UUID.randomUUID();
				discover.setRequestId(uuid.toString());
//				TimeZone tz = TimeZone.getTimeZone("UTC");
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SS"); // Quoted "Z" to indicate UTC, no timezone offset
//				df.setTimeZone(tz);
//				String nowAsISO = df.format(new Date());
//				discover.setTimestamp(nowAsISO);
				LocalDateTime time= LocalDateTime.now(ZoneOffset.UTC);
				System.out.println("timeStamp"+time.toString());
					discover.setTimestamp(time.toString());
				
				LinkConfirmPatient link=new LinkConfirmPatient();
				link.setDisplay("LinkConfirm");
				link.setReferenceNumber(collection.getConfirmation().getLinkRefNumber());
				List<CareContext>careContexts=new ArrayList<CareContext>();
				CareContext care=new CareContext();
				care.setDisplay("Health-Information");
				care.setReferenceNumber(NDHM_CLIENTID);
				careContexts.add(care);
				link.setCareContexts(careContexts);
				FetchResponse resp=new FetchResponse();
				resp.setRequestId(collection.getRequestId());
				discover.setResp(resp);
				discover.setPatient(link);
		Boolean	status=	onLinkConfirm(discover);
		System.out.println("Status"+status);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onLinkConfirm(OnLinkConfirm request) {
		Boolean response = false;
		try {
			
			JSONObject orderRequest = new JSONObject();

			JSONObject orderRequest1 = new JSONObject();
			JSONObject orderRequest2 = new JSONObject();	
			JSONObject orderRequest3 = new JSONObject();	
			
			
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
			
			
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			orderRequest1.put("referenceNumber", request.getPatient().getReferenceNumber());
			orderRequest1.put("display",request.getPatient().getDisplay());
			orderRequest1.put("careContexts",request.getPatient().getCareContexts());
			orderRequest.put("patient",orderRequest1);
			
			orderRequest.put("error",request.getError());
			orderRequest3.put("requestId",request.getResp().getRequestId());
			orderRequest.put("resp",orderRequest3);
			System.out.println("req"+orderRequest);
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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	@Transactional
	@Async
	public Boolean onDataFlowRequest(DataFlowRequest request) {
		Boolean response = false;
		try {
			System.out.println("OnDataFlow Request");
			// NdhmOauthResponse oauth = session();
			// System.out.println("token" + oauth.getAccessToken());

			// String url =
			// "https://your-hrp-server.com/v0.5/health-information/hip/request";

//			URL obj = new URL(url);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			con.setDoOutput(true);
//
//			System.out.println(con.getErrorStream());
//			con.setDoInput(true);
//			// optional default is POST
//			con.setRequestMethod("POST");
//			con.setRequestProperty("Accept-Language", "en-US");
//			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
//			con.setRequestProperty("X-HIP-ID", NDHM_CLIENTID);
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(request.toString());
//			wr.flush();
//			wr.close();
//			con.disconnect();
//			InputStream in = con.getInputStream();
//			// BufferedReader in = new BufferedReader(new
//			// InputStreamReader(con.getInputStream()));
//			String inputLine;
//			System.out.println(con.getErrorStream());
//			/* response = new StringBuffer(); */
//			StringBuffer output = new StringBuffer();
//			int c = 0;
//			while ((c = in.read()) != -1) {
//
//				output.append((char) c);
//
//			}
//			System.out.println("response:" + output.toString());
//			int responseCode = con.getResponseCode();
//			if (responseCode == 202)
			HipDataFlowCollection collection = new HipDataFlowCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			hipDataFlowRepository.save(collection);
			response = true;
			Boolean status=false;
			if(response==true)
			{
				GateWayOnRequest gate=new GateWayOnRequest();
				UUID uuid=UUID.randomUUID();
				gate.setRequestId(uuid.toString());
				LocalDateTime time= LocalDateTime.now(ZoneOffset.UTC);
				System.out.println("timeStamp"+time.toString());
				gate.setTimestamp(time.toString());
				GateWayHiOnRequest gateWay=new GateWayHiOnRequest();
				gateWay.setTransactionId(collection.getTransactionId());
				gateWay.setSessionStatus("ACKNOWLEDGED");
				gate.setHiRequest(gateWay);
				FetchResponse resp=new FetchResponse();
				resp.setRequestId(collection.getRequestId());
				gate.setResp(resp);
			 status= onGateWayOnRequest(gate);
			}
			
			NdhmNotifyCollection notify=ndhmNotifyRepository.findByNotificationConsentId(request.getHiRequest().getConsent().getId());
		
			if(notify !=null)
			{
				List<String>hiTypes=notify.getNotification().getConsentDetail().getHiTypes();
				System.out.println("HiTypes"+hiTypes);
				if(hiTypes !=null)
				{
					//String hiType=hiTypes.get(0);
					
						List<PatientCollection> patientCollections=patientRepository.findByHealthId(notify.getNotification().getConsentDetail().getPatient().getId());
						PatientCollection patientCollection =patientCollections.get(0);
						UserCollection user=userRepository.findById(patientCollection.getUserId()).orElse(null);
						patientCollection.setSecMobile(user.getMobileNumber());
						//ResourcePopulator.populatePatientResource(patientCollection);
						List<EntriesDataTransferRequest> entries=new ArrayList<EntriesDataTransferRequest>();
						KeyMaterialRequestDataFlow key=new KeyMaterialRequestDataFlow();

						if(hiTypes.contains("Prescription"))
						{
						if(patientCollection !=null)
						{
							Criteria criteria =new Criteria();
							//criteria.and("createdTime").gte(notify.getNotification().getConsentDetail().getPermission().getDateRange().getFrom())
							//.lte(notify.getNotification().getConsentDetail().getPermission().getDateRange().getTo());

							criteria.and("patientId").is(patientCollection.getUserId());
							Aggregation aggregation = null;
							aggregation=Aggregation
									.newAggregation(
											Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

							List<PrescriptionCollection> prescriptionCollections =mongoTemplate.aggregate(aggregation,
										PrescriptionCollection.class, PrescriptionCollection.class).getMappedResults();
						System.out.println("aggregation"+aggregation);
						//PrescriptionCollection prescriptionCollection=null;
						if(prescriptionCollections!=null) {
					//	for(PrescriptionCollection prescriptionCollection:prescriptionCollections)
					//	{
							PrescriptionCollection prescriptionCollection=prescriptionCollections.get(0);
						DoctorCollection doctorCollection=doctorRepository.findByUserId(prescriptionCollection.getDoctorId());
						System.out.println("Doctor "+doctorCollection);
						UserCollection userCollection=userRepository.findById(doctorCollection.getUserId()).orElse(null);
						System.out.println("User "+doctorCollection);
						String bundle =	PrescriptionSample.prescriptionConvert();
						System.out.println("Fhir:"+null);
						//	mapPrescriptionRecordData(prescriptionCollections, collection.getHiRequest().getKeyMaterial().getNonce(), collection.getHiRequest().getKeyMaterial().getDhPublicKey().getKeyValue());
						DataEncryptionResponse data=null;
						if( collection.getHiRequest().getKeyMaterial() !=null)
						{
						data=DHKeyExchangeCrypto.convert(bundle, collection.getHiRequest().getKeyMaterial().getNonce(), collection.getHiRequest().getKeyMaterial().getDhPublicKey().getKeyValue());
						
						System.out.println("encrypt"+data);
						EntriesDataTransferRequest entry=new EntriesDataTransferRequest();
						entry.setCareContextReference("Prescription");
						entry.setContent(data.getEncryptedData());
						
						key.setNonce(data.getRandomSender());
						DhPublicKeyDataFlowRequest dhPublic=new DhPublicKeyDataFlowRequest();
						dhPublic.setKeyValue(data.getSenderPublicKey());
						key.setDhPublicKey(dhPublic);
						
						
						entries.add(entry);
						}		
						
						
						}
							
							
						}
					}
//						else if(hiTypes.contains("OPConsultation")) {
//							Criteria criteria =new Criteria();
//							criteria.and("createdTime").gte(notify.getNotification().getConsentDetail().getPermission().getDateRange().getFrom())
//							.lte(notify.getNotification().getConsentDetail().getPermission().getDateRange().getTo());
//
//							criteria.and("patientId").is(patientCollection.getUserId());
//							Aggregation aggregation = null;
//							
//							aggregation=Aggregation
//									.newAggregation(
//											Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
//
//							List<OTReportsCollection> operationNotesCollections =mongoTemplate.aggregate(aggregation,
//									OTReportsCollection.class, OTReportsCollection.class).getMappedResults();
//						
//						//	OTReportsCollection operationNotesCollection=operationNotesCollections.get(0);
//							//System.out.println("Doctor "+doctorCollection);
//								for(OTReportsCollection operationNotesCollection:operationNotesCollections)
//							{
//								DoctorCollection doctorCollection=doctorRepository.findByUserId(operationNotesCollection.getDoctorId());
//
//							    UserCollection userCollection=userRepository.findById(doctorCollection.getUserId()).orElse(null);
//								System.out.println("User "+doctorCollection);
//
//								String bundle =	OPConsultNoteSample.OpConvert(operationNotesCollection,patientCollection,userCollection);
//								//	mapPrescriptionRecordData(prescriptionCollections, collection.getHiRequest().getKeyMaterial().getNonce(), collection.getHiRequest().getKeyMaterial().getDhPublicKey().getKeyValue());
//							DataEncryptionResponse data=DHKeyExchangeCrypto.convert(bundle, collection.getHiRequest().getKeyMaterial().getNonce(), collection.getHiRequest().getKeyMaterial().getDhPublicKey().getKeyValue());
//							
//							
//							EntriesDataTransferRequest entry=new EntriesDataTransferRequest();
//							entry.setCareContextReference("OpConsultation");
//							entry.setContent(data.getEncryptedData());
//							
//							key.setNonce(data.getRandomSender());
//							DhPublicKeyDataFlowRequest dhPublic=new DhPublicKeyDataFlowRequest();
//							dhPublic.setKeyValue(data.getSenderPublicKey());
//							key.setDhPublicKey(dhPublic);
//							
//							
//							entries.add(entry);
//
//								
//							}
//						}
//						else if(hiTypes.contains("DischargeSummary")) {
//							
//							Criteria criteria =new Criteria();
//							criteria.and("createdTime").gte(notify.getNotification().getConsentDetail().getPermission().getDateRange().getFrom())
//							.lte(notify.getNotification().getConsentDetail().getPermission().getDateRange().getTo());
//
//							criteria.and("patientId").is(patientCollection.getUserId());
//							Aggregation aggregation = null;
//							aggregation=Aggregation
//									.newAggregation(
//											Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
//
//							List<DischargeSummaryCollection> dischargeSummaryCollections =mongoTemplate.aggregate(aggregation,
//									DischargeSummaryCollection.class, DischargeSummaryCollection.class).getMappedResults();
//							for(DischargeSummaryCollection dischargeSummaryCollection:dischargeSummaryCollections)
//							{
//								String bundle =	DischargeSummarySample.dischargeSummaryConvert(dischargeSummaryCollection,patientCollection);
//								//	mapPrescriptionRecordData(prescriptionCollections, collection.getHiRequest().getKeyMaterial().getNonce(), collection.getHiRequest().getKeyMaterial().getDhPublicKey().getKeyValue());
//							DataEncryptionResponse data=DHKeyExchangeCrypto.convert(bundle, collection.getHiRequest().getKeyMaterial().getNonce(), collection.getHiRequest().getKeyMaterial().getDhPublicKey().getKeyValue());
//							
//							
//							EntriesDataTransferRequest entry=new EntriesDataTransferRequest();
//							entry.setCareContextReference("DischargeSummary");
//							entry.setContent(data.getEncryptedData());
//							
//							key.setNonce(data.getRandomSender());
//							DhPublicKeyDataFlowRequest dhPublic=new DhPublicKeyDataFlowRequest();
//							dhPublic.setKeyValue(data.getSenderPublicKey());
//							key.setDhPublicKey(dhPublic);
//							
//							
//							entries.add(entry);
//
//							}
//						}
						
						DataTransferRequest transfer=new DataTransferRequest();
						transfer.setKeyMaterial(key);
						transfer.setPageCount(0); 
						transfer.setPageNumber(0);
						transfer.setEntries(entries);	
						transfer.setTransactionId(collection.getTransactionId());
					Boolean transferResponse=	onDataTransfer(transfer);
					
					Boolean info=false;
					if(transferResponse ==true)
					{
						HealthInfoNotify dataFlow=new HealthInfoNotify();
						UUID uuid=UUID.randomUUID();
						
						LocalDateTime time= LocalDateTime.now(ZoneOffset.UTC);
						dataFlow.setRequestId(uuid.toString());
						dataFlow.setTimestamp(time.toString());
						
						HipInfoNotify hipNotify=new HipInfoNotify();
						hipNotify.setTransactionId(collection.getTransactionId());
						hipNotify.setConsentId(request.getHiRequest().getConsent().getId());
						hipNotify.setDoneAt(time.toString());
						HipNotifier notifier=new HipNotifier();
						notifier.setId(NDHM_CLIENTID);
						notifier.setType("HIP");
						StatusNotify statusNotify=new StatusNotify();
						statusNotify.setSessionStatus("TRANSFERRED");
						statusNotify.setHipId(NDHM_CLIENTID);
						StatusResponse statusResponse=new StatusResponse();
						statusResponse.setHiStatus("DELIVERED");
						statusResponse.setCareContextReference("Healthcoco");
						statusNotify.setStatusResponses(statusResponse);
						hipNotify.setStatusNotification(statusNotify);
						dataFlow.setNotification(hipNotify);
						info=healthInformationNotify(dataFlow);	
						
					}
					
					System.out.println("transferReponse"+transferResponse);
					
					System.out.println("HealthNotify"+info);
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onGateWayOnRequest(GateWayOnRequest request) {
		Boolean response = false;
		try {
			System.out.println("OnGateway Request");
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());

			JSONObject hiRequestRequest = new JSONObject();
			hiRequestRequest.put("transactionId", request.getHiRequest().getTransactionId());
			hiRequestRequest.put("sessionStatus", request.getHiRequest().getSessionStatus());
			System.out.println(hiRequestRequest);
			orderRequest.put("hiRequest", hiRequestRequest);

		//	JSONObject errorRequest = new JSONObject();
		//	errorRequest.put("code", request.getError().getCode());
		//	errorRequest.put("message", request.getError().getMessage());
		//	System.out.println(errorRequest);
			orderRequest.put("error", request.getError());
			
			JSONObject requestId = new JSONObject();
			requestId.put("requestId", request.getResp().getRequestId());
			orderRequest.put("resp",requestId);
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/health-information/hip/on-request";

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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public Boolean onDataTransfer(DataTransferRequest request) {
		Boolean response = false;
		try {
			
			
			System.out.println("DataTransfer");
			
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("pageNumber", request.getPageNumber());
			orderRequest.put("pageCount", request.getPageCount());
			orderRequest.put("transactionId", request.getTransactionId());
			orderRequest.put("entries", request.getEntries());// list

			JSONObject keyMaterialRequest = new JSONObject();
			keyMaterialRequest.put("cryptoAlg", request.getKeyMaterial().getCryptoAlg());
			keyMaterialRequest.put("curve", request.getKeyMaterial().getCurve());
			keyMaterialRequest.put("nonce", request.getKeyMaterial().getNonce());

			JSONObject dhPublicKeyRequest = new JSONObject();
			if(request.getKeyMaterial() !=null && request.getKeyMaterial().getDhPublicKey() !=null) {
			dhPublicKeyRequest.put("expiry", request.getKeyMaterial().getDhPublicKey().getExpiry());
			dhPublicKeyRequest.put("parameters", request.getKeyMaterial().getDhPublicKey().getParameters());
			
			dhPublicKeyRequest.put("keyValue", request.getKeyMaterial().getDhPublicKey().getKeyValue());

			keyMaterialRequest.put("dhPublicKey", dhPublicKeyRequest);
			}
			orderRequest.put("keyMaterial", keyMaterialRequest);

			System.out.println("request " + orderRequest);
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/patient-hiu/data/notification";

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
			if (responseCode == 202)
				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}


	
	@Override
	public Boolean onConsentRequestOnInitApi(ConsentOnInitRequest request) {
		Boolean response = false;
		try {

//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("requestId", request.getRequestId());
//			orderRequest.put("timestamp", request.getTimestamp());
//
//			JSONObject consentRequest = new JSONObject();
//			consentRequest.put("id", request.getConsentRequest().getId());
//			System.out.println(consentRequest);
//			orderRequest.put("consentRequest", consentRequest);
//
//			JSONObject errorRequest = new JSONObject();
//			errorRequest.put("code", request.getError().getCode());
//			errorRequest.put("message", request.getError().getMessage());
//			System.out.println(errorRequest);
//			orderRequest.put("error", errorRequest);
//
//			JSONObject resRequest = new JSONObject();
//			resRequest.put("requestId", request.getResp().getRequestId());
//			System.out.println(resRequest);
//
//			orderRequest.put("resp", resRequest);
//			System.out.println("request " + orderRequest);
//
//			NdhmOauthResponse oauth = session();
//			System.out.println("token" + oauth.getAccessToken());
//
//			String url = "https://dev.ndhm.gov.in/hiu/v0.5/consent-requests/on-init";
//			URL obj = new URL(url);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			con.setDoOutput(true);
//
//			System.out.println(con.getErrorStream());
//			con.setDoInput(true);
//			// optional default is POST
//			con.setRequestMethod("POST");
//			con.setRequestProperty("Accept-Language", "en-US");
//			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
//			con.setRequestProperty("X-HIU-ID",NDHM_HIU_CLIENTID );
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(orderRequest.toString());
//			wr.flush();
//			wr.close();
//			con.disconnect();
//			InputStream in = con.getInputStream();
//			// BufferedReader in = new BufferedReader(new
//			// InputStreamReader(con.getInputStream()));
//			String inputLine;
//			System.out.println(con.getErrorStream());
//			/* response = new StringBuffer(); */
//			StringBuffer output = new StringBuffer();
//			int c = 0;
//			while ((c = in.read()) != -1) {
//
//				output.append((char) c);
//
//			}
//			System.out.println("response:" + output.toString());
//			int responseCode = con.getResponseCode();
//			if (responseCode == 202)
			ConsentInitCollection collection=new ConsentInitCollection();
			BeanUtil.map(request,collection);
			collection.setCreatedTime(new Date());
			collection.setUpdatedTime(new Date());
			
			consentInitRepository.save(collection);
				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onGatewayConsentRequestInitApi(GatewayConsentInitRequest request) {
		Boolean response = false;
		try {

			JSONObject orderRequest = new JSONObject();

			JSONObject consentRequest = new JSONObject();

			JSONObject purposeRequest = new JSONObject();
			purposeRequest.put("code", request.getConsent().getPurpose().getCode());
			purposeRequest.put("text", request.getConsent().getPurpose().getText());
			purposeRequest.put("refUri", request.getConsent().getPurpose().getRefUri());
			System.out.println(purposeRequest);

			JSONObject requesterRequest = new JSONObject();
			requesterRequest.put("name", request.getConsent().getRequester().getName());
			JSONObject identifierRequest = new JSONObject();
			identifierRequest.put("type", request.getConsent().getRequester().getIdentifier().getType());
			identifierRequest.put("value", request.getConsent().getRequester().getIdentifier().getValue());
			identifierRequest.put("system", request.getConsent().getRequester().getIdentifier().getSystem());
			requesterRequest.put("identifier", identifierRequest);

			JSONObject patientRequest = new JSONObject();
			patientRequest.put("id", request.getConsent().getPatient().getId());
			System.out.println(patientRequest);

		//	JSONObject hipRequest = new JSONObject();
		//	hipRequest.put("id", request.getConsent().getHip().getId());
	//		System.out.println(hipRequest);

			JSONObject hiuRequest = new JSONObject();
			hiuRequest.put("id", request.getConsent().getHiu().getId());
			System.out.println(hiuRequest);

			JSONObject permissionRequest = new JSONObject();
			permissionRequest.put("accessMode", request.getConsent().getPermission().getAccessMode());
			permissionRequest.put("dataEraseAt", request.getConsent().getPermission().getDataEraseAt());

			JSONObject dateRangeRequest = new JSONObject();
			dateRangeRequest.put("from", request.getConsent().getPermission().getDateRange().getFrom());
			dateRangeRequest.put("to", request.getConsent().getPermission().getDateRange().getTo());
			System.out.println(dateRangeRequest);

			JSONObject frequencyRequest = new JSONObject();
			frequencyRequest.put("unit", request.getConsent().getPermission().getFrequency().getUnit());
			frequencyRequest.put("value", request.getConsent().getPermission().getFrequency().getValue());
			frequencyRequest.put("repeats", request.getConsent().getPermission().getFrequency().getRepeats());

			permissionRequest.put("dateRange", dateRangeRequest);
			permissionRequest.put("frequency", frequencyRequest);

			consentRequest.put("purpose", purposeRequest);
			consentRequest.put("requester", requesterRequest);
			consentRequest.put("patient", patientRequest);
	//		consentRequest.put("hip", hipRequest);
			consentRequest.put("hiu", hiuRequest);
	//		consentRequest.put("careContexts", request.getConsent().getCareContexts());
			consentRequest.put("hiTypes", request.getConsent().getHiTypes());
			consentRequest.put("permission", permissionRequest);

			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			orderRequest.put("consent", consentRequest);
			System.out.println("request " + orderRequest);

			
			System.out.println("consent-Init"+orderRequest);
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/consent-requests/init";

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
			con.setRequestProperty("X-CM-ID", "sbx");

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
			if (responseCode == 202)
				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onGatewayConsentRequestStatusApi(GatewayConsentStatusRequest request) {
		Boolean response = false;
		try {

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			orderRequest.put("consentRequestId",request.getConsentRequestId() );
			System.out.println("request " + orderRequest);

			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/consent-requests/status";

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
			con.setRequestProperty("X-CM-ID", "sbx");

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
			if (responseCode == 202)
				response = true;

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
		if(collection !=null) {
		BeanUtil.map(request, collection);
		collection.setCreatedTime(new Date());
		
		ndhmNotifyRepository.save(collection);
		
		response=true;
		}
		System.out.println("response"+response);
		if(response==true) {
			OnNotifyRequest req=new OnNotifyRequest();
			UUID uuid=UUID.randomUUID();
			req.setRequestId(uuid.toString());
//			TimeZone tz = TimeZone.getTimeZone("UTC");
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SS"); // Quoted "Z" to indicate UTC, no timezone offset
//			df.setTimeZone(tz);
//			String nowAsISO = df.format(new Date());
//			req.setTimestamp(nowAsISO);
			LocalDateTime time= LocalDateTime.now(ZoneOffset.UTC);
			System.out.println("timeStamp"+time.toString());
			req.setTimestamp(time.toString());
			AcknowledgementRequest acknowledgementRequest=new AcknowledgementRequest();
			acknowledgementRequest.setConsentId(collection.getNotification().getConsentId());
			acknowledgementRequest.setStatus("OK");
			req.setAcknowledgement(acknowledgementRequest);
			FetchResponse resp=new FetchResponse();
			resp.setRequestId(collection.getRequestId());
			
			req.setResp(resp);
			Boolean status=false;
			status=onNotify(req);
			
			
		System.out.println("Status"+status);
		}
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
			//System.out.println("OnNotify "+request);
			JSONObject orderRequest = new JSONObject();
			JSONObject acknowledge = new JSONObject();
			JSONObject resp = new JSONObject();
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());
			
			acknowledge.put("status", request.getAcknowledgement().getStatus());
			acknowledge.put("consentId", request.getAcknowledgement().getConsentId());
			orderRequest.put("acknowledgement",acknowledge);
			orderRequest.put("error",request.getError());
			resp.put("requestId",request.getResp().getRequestId());
			orderRequest.put("resp",resp);
			
			System.out.println("On notify request: " + orderRequest);
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
	//	con.setRequestProperty("Accept-Language", "en-US");
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
	public OnCareContext getCareContext(String requestId) {
		OnCareContext response=null;
		try {
			OnCareContextCollection collection	= onCareContextRepository.findByRespRequestId(requestId);
			response=new OnCareContext();
			if(collection !=null)
			{
				BeanUtil.map(collection,response);
			}
			
		}
		 catch (Exception e) {
				e.printStackTrace();
				logger.error("Error : " + e.getMessage());
				throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
			}
			return response;
	}

	@Override
	public LinkConfirm getLinkConfim(String requestId) {
		LinkConfirm response=null;
		try {
		LinkConfirmCollection collection = linkConfirmRepository.findByRequestId(requestId);
		response=new LinkConfirm();
		if(collection !=null)
		{
			BeanUtil.map(collection,response);
		}
		}catch (Exception e) {
				e.printStackTrace();
				logger.error("Error : " + e.getMessage());
				throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
			}
			return response;
	}	
	
//	public DataEncryptionResponse mapPrescriptionRecordData(List<PrescriptionCollection> prescriptionCollections,String nounce,String keyPair) throws Exception {
//		List<NDHMPrecriptionRecordData> precriptionRecordData = new ArrayList<NDHMPrecriptionRecordData>();
//		DataEncryptionResponse data=null;
//		for(PrescriptionCollection prescriptionCollection:prescriptionCollections) {
//		if(prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty()) {
//			PatientCollection patientCollection = patientRepository.
//					findByUserIdAndDoctorIdAndLocationIdAndHospitalId(prescriptionCollection.getPatientId(), 
//							prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(), 
//							prescriptionCollection.getHospitalId());
//			
//			for(int i =0; i<prescriptionCollection.getItems().size()-1; i++) {
//				NDHMPrecriptionRecordData ndhmPrecriptionRecordData = new NDHMPrecriptionRecordData();
//				ndhmPrecriptionRecordData.setFullUrl(NDHMRecordDataResourceType.MedicationRequest.getResourceType()+"/"+(i+1));
//				NDHMRecordDataResource resource = new NDHMRecordDataResource();
//				resource.setResourceType(NDHMRecordDataResourceType.MedicationRequest.getResourceType());
//				resource.setId(prescriptionCollection.getItems().get(i).getDrugId().toString());
//				resource.setStatus(prescriptionCollection.getIsActive() ? "active":"inactive");
//				
//				NDHMRecordDataCode medicationCodeableConcept = new NDHMRecordDataCode();
//				medicationCodeableConcept.setText(prescriptionCollection.getItems().get(i).getDrugName()+" "
//												+prescriptionCollection.getItems().get(i).getDosage()+" "
//												+prescriptionCollection.getItems().get(i).getDrugType());
//				
//				resource.setMedicationCodeableConcept(medicationCodeableConcept);
//				
//				NDHMRecordDataSubject subject = new NDHMRecordDataSubject();
//				subject.setDisplay(patientCollection.getLocalPatientName());
//				resource.setSubject(subject);
//				resource.setAuthoredOn(prescriptionCollection.getCreatedTime()+"");
//				
//				NDHMRecordDataRequester requester = new NDHMRecordDataRequester();
//				requester.setDisplay(prescriptionCollection.getCreatedBy());
//				
//				List<NDHMRecordDataDosageInstruction> dosageInstruction = new ArrayList<NDHMRecordDataDosageInstruction>();
//				NDHMRecordDataDosageInstruction dataDosageInstruction = new NDHMRecordDataDosageInstruction();
//				dataDosageInstruction.setText(prescriptionCollection.getItems().get(i).getDosage());
//				dosageInstruction.add(dataDosageInstruction);
//				
//				resource.setDosageInstruction(dosageInstruction);
//				ndhmPrecriptionRecordData.setResource(resource);
//				 data=DHKeyExchangeCrypto.convert(ndhmPrecriptionRecordData.toString(),nounce,keyPair);
//		
//				//	precriptionRecordData.add(ndhmPrecriptionRecordData);
//				}
//			}
//		}
//	//	return precriptionRecordData;
//		return data;
//		
//	}	

	@Override
	public NotifyRequest getNotify(String requestId) {
		NotifyRequest response=null;
		try {
			NdhmNotifyCollection collection=ndhmNotifyRepository.findByRequestId(requestId);
			response=new NotifyRequest();
			if(collection !=null)
			{
				BeanUtil.map(collection,response);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public DataFlowRequest getDataFlow(String transactionId) {
		DataFlowRequest response=null;
				try{
					HipDataFlowCollection collection =healthDataFlowRepository.findByTransactionId(transactionId);
					response=new DataFlowRequest();
					if(collection !=null)
					{
						BeanUtil.map(collection, response);
					}
				}
				catch (Exception e) {
				
					e.printStackTrace();
					logger.error("Error : " + e.getMessage());
					throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
				}
				return response;
	}

	@Override
	public ConsentOnInitRequest getConsentInitRequest(String requestId) {
		ConsentOnInitRequest response=null;
		try {
			
			ConsentInitCollection collection=consentInitRepository.findByRespRequestId(requestId);
			response=new ConsentOnInitRequest();
			if(collection !=null)
			{
				BeanUtil.map(collection,response);
			}
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}

	@Override
	public Boolean healthInformationNotify(HealthInfoNotify request) {
		Boolean response = false;
		try {
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());

			JSONObject hiRequestRequest = new JSONObject();
			hiRequestRequest.put("consentId", request.getNotification().getTransactionId());
			hiRequestRequest.put("transactionId", request.getNotification().getTransactionId());
			hiRequestRequest.put("doneAt", request.getNotification().getDoneAt());
			hiRequestRequest.put("notifier", request.getNotification().getNotifier());
			hiRequestRequest.put("statusNotification", request.getNotification().getStatusNotification());
			System.out.println(hiRequestRequest);
			orderRequest.put("notification", hiRequestRequest);

//			JSONObject errorRequest = new JSONObject();
//			errorRequest.put("code", request.getError().getCode());
//			errorRequest.put("message", request.getError().getMessage());
//			System.out.println(errorRequest);
//			orderRequest.put("error", errorRequest);
//			
//			JSONObject requestId = new JSONObject();
//			requestId.put("requestId", request.getResp().getRequestId());
//			orderRequest.put("resp",requestId);
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/health-information/notify";

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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onConsentRequestStatus(OnConsentRequestStatus request) {
		Boolean response=false;
		try {
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("requestId", request.getRequestId());
//			orderRequest.put("timestamp", request.getTimestamp());
//			orderRequest.put("consentRequest",request.getConsentRequest());
//			orderRequest.put("error", request.getError());
//			orderRequest.put("resp",request.getResp());
//			JSONObject hiRequestRequest = new JSONObject();
//			hiRequestRequest.put("consentId", request.getNotification().getTransactionId());
//			hiRequestRequest.put("transactionId", request.getNotification().getTransactionId());
//			hiRequestRequest.put("doneAt", request.getNotification().getDoneAt());
//			hiRequestRequest.put("notifier", request.getNotification().getNotifier());
//			hiRequestRequest.put("statusNotification", request.getNotification().getStatusNotification());
//			System.out.println(hiRequestRequest);
//			orderRequest.put("notification", hiRequestRequest);

//			JSONObject errorRequest = new JSONObject();
//			errorRequest.put("code", request.getError().getCode());
//			errorRequest.put("message", request.getError().getMessage());
//			System.out.println(errorRequest);
//			orderRequest.put("error", errorRequest);
//			
//			JSONObject requestId = new JSONObject();
//			requestId.put("requestId", request.getResp().getRequestId());
//			orderRequest.put("resp",requestId);
			
//			NdhmOauthResponse oauth = session();
//			System.out.println("token" + oauth.getAccessToken());
//
//			String url = "https://dev.ndhm.gov.in/gateway/v0.5/health-information/notify";
//
//			URL obj = new URL(url);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			con.setDoOutput(true);
//
//			System.out.println(con.getErrorStream());
//			con.setDoInput(true);
//			// optional default is POST
//			con.setRequestMethod("POST");
//			con.setRequestProperty("Accept-Language", "en-US");
//			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
//			con.setRequestProperty("X-CM-ID", "sbx");
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(orderRequest.toString());
//			wr.flush();
//			wr.close();
//			con.disconnect();
//			InputStream in = con.getInputStream();
//			// BufferedReader in = new BufferedReader(new
//			// InputStreamReader(con.getInputStream()));
//			String inputLine;
//			System.out.println(con.getErrorStream());
//			/* response = new StringBuffer(); */
//			StringBuffer output = new StringBuffer();
//			int c = 0;
//			while ((c = in.read()) != -1) {
//
//				output.append((char) c);
//
//			}
//			System.out.println("response:" + output.toString());
//			int responseCode = con.getResponseCode();
//			if (responseCode == 202)
			
			OnConsentRequestStatusCollection collection=new OnConsentRequestStatusCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			consentStatusRequestRepository.save(collection);
			
				response = true;
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public OnConsentRequestStatus getConsentStatus(String requestId) {
		OnConsentRequestStatus response=null;
		try {
			OnConsentRequestStatusCollection collection=consentStatusRequestRepository.findByRespRequestId(requestId);
			response=new OnConsentRequestStatus();
			if(collection !=null)
			{
				BeanUtil.map(collection, response);
			}
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean findPatient(NdhmPatientRequest request) {
		Boolean response=false;
		try {
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("requestId", request.getRequestId());
		orderRequest.put("timestamp", request.getTimestamp());

		JSONObject hiRequestRequest = new JSONObject();
		
		JSONObject patient = new JSONObject();
		patient.put("id", request.getQuery().getPatient().getId());
		hiRequestRequest.put("patient", patient);
		JSONObject requester = new JSONObject();
		requester.put("type", request.getQuery().getRequester().getType());
		requester.put("id", request.getQuery().getRequester().getId());
		hiRequestRequest.put("requester", requester);
		System.out.println(hiRequestRequest);
		orderRequest.put("query", hiRequestRequest);
		System.out.println("Patient request"+orderRequest);
		String url = "https://dev.ndhm.gov.in/gateway/v0.5/patients/find";
		NdhmOauthResponse oauth = session();
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
		con.setRequestProperty("X-CM-ID", "sbx");
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
		if (responseCode == 202)
			response = true;
		}
		
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onFindPatient(NdhmOnPatientFindRequest request) {
		Boolean response=false;
		try {
			NdhmPatientFindCollection collection=new NdhmPatientFindCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			collection.setUpdatedTime(new Date());
			ndhmPatientFindRepository.save(collection);
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
	public NdhmOnPatientFindRequest getNdhmPatient(String requestId) {
		NdhmOnPatientFindRequest response=null;
		try {
			NdhmPatientFindCollection collection=ndhmPatientFindRepository.findByRespRequestId(requestId);
			response=new NdhmOnPatientFindRequest();
			if(collection !=null)
			{
				response=new NdhmOnPatientFindRequest();
				BeanUtil.map(collection, response);
			}
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onNotifyHiu(HiuOnNotify request) {
		Boolean response=false;
		try {
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("requestId", request.getRequestId());
		orderRequest.put("timestamp", request.getTimestamp());

		orderRequest.put("acknowledgement", request.getAcknowledgement());
			
		orderRequest.put("error",request.getError());
		JSONObject resp = new JSONObject();
		resp.put("requestId",request.getResp().getRequestId());
		orderRequest.put("resp",resp);
		
		String url = "https://dev.ndhm.gov.in/gateway/v0.5/consents/hiu/on-notify";
		NdhmOauthResponse oauth = session();
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
		con.setRequestProperty("X-CM-ID", "sbx");
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
		if (responseCode == 202)
			response = true;
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}

	@Override
	public Boolean notifyHiu(NotifyHiuRequest request) {
		Boolean response=false;
		try {
			HiuNotifyCollection collection=new HiuNotifyCollection();
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			collection.setUpdatedTime(new Date());
			hiuNotifyRepository.save(collection);
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
	public NotifyHiuRequest getHiuNotify(String requestId) {
		NotifyHiuRequest response=null;
		try {
			HiuNotifyCollection collection=hiuNotifyRepository.findByNotificationConsentRequestId(requestId);
			response=new NotifyHiuRequest();
			if(collection !=null)
			{
				BeanUtil.map(collection, response);
			}
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean consentFetch(ConsentFetchRequest request) {
		Boolean response=false;
		try {
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("requestId", request.getRequestId());
		orderRequest.put("timestamp", request.getTimestamp());

		orderRequest.put("consentId", request.getConsentId());
			
		
		
		String url = "https://dev.ndhm.gov.in/gateway/v0.5/consents/fetch";
		NdhmOauthResponse oauth = session();
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
		con.setRequestProperty("X-CM-ID", "sbx");
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
		if (responseCode == 202)
			response = true;
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean onConsentFetch(OnConsentFetchRequest request) {
			Boolean response=false;
		try {
			ConsentFetchRequestCollection collection=new ConsentFetchRequestCollection();
			
				BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			collection.setUpdatedTime(new Date());
			consentFetchRepository.save(collection);
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
	public OnConsentFetchRequest getConsentFetch(String requestId) {
		OnConsentFetchRequest response=null;
		try {
			ConsentFetchRequestCollection collection=consentFetchRepository.findByRespRequestId(requestId);
			response=new OnConsentFetchRequest();
			if(collection !=null)
			{
				BeanUtil.map(collection, response);
			}
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public Boolean hiuDataRequest(HiuDataRequest request) {
		Boolean response = false;
		try {
			
			KeyMaterialRequestDataFlow key=new KeyMaterialRequestDataFlow();
			DataEncryptionResponse hiu=DhKeyExchangeCryptoHiu.convert();
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("requestId", request.getRequestId());
			orderRequest.put("timestamp", request.getTimestamp());

			JSONObject hiRequestRequest = new JSONObject();
			JSONObject consent = new JSONObject();
			JSONObject dateRange = new JSONObject();
			consent.put("id", request.getHiRequest().getConsent().getId());
			hiRequestRequest.put("consent", consent);
			dateRange.put("from",request.getHiRequest().getDateRange().getFrom());
			dateRange.put("to", request.getHiRequest().getDateRange().getTo());
			hiRequestRequest.put("dateRange",dateRange);
			hiRequestRequest.put("dataPushUrl", request.getHiRequest().getDataPushUrl());
		
			JSONObject keymaterial = new JSONObject();
			keymaterial.put("cryptoAlg", key.getCryptoAlg());
			keymaterial.put("curve", key.getCurve());
			keymaterial.put("nonce", hiu.getRandomSender());
			JSONObject dhPublicKey = new JSONObject();
			dhPublicKey.put("expiry",request.getHiRequest().getKeyMaterial().getDhPublicKey().getExpiry());
			dhPublicKey.put("parameters",request.getHiRequest().getKeyMaterial().getDhPublicKey().getParameters());
			dhPublicKey.put("keyValue",hiu.getRandomSender());
			keymaterial.put("dhPublicKey", dhPublicKey);
			hiRequestRequest.put("keyMaterial", keymaterial);
			
			//System.out.println("cmRequest"+hiRequestRequest);
			orderRequest.put("hiRequest", hiRequestRequest);

			System.out.println("Cm request"+orderRequest);
		//	JSONObject errorRequest = new JSONObject();
		//	errorRequest.put("code", request.getError().getCode());
		//	errorRequest.put("message", request.getError().getMessage());
		//	System.out.println(errorRequest);
			
			
			NdhmOauthResponse oauth = session();
			System.out.println("token" + oauth.getAccessToken());

			String url = "https://dev.ndhm.gov.in/gateway/v0.5/health-information/cm/request";

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
			con.setRequestProperty("X-CM-ID", "sbx");
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
			if (responseCode == 202)
				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public Boolean onHiuDatarequest(GateWayOnRequest request) {
		Boolean response = false;
		try {

//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("requestId", request.getRequestId());
//			orderRequest.put("timestamp", request.getTimestamp());
//
//			JSONObject hiRequestRequest = new JSONObject();
//			hiRequestRequest.put("transactionId", request.getHiRequest().getTransactionId());
//			hiRequestRequest.put("sessionStatus", request.getHiRequest().getSessionStatus());
//			System.out.println(hiRequestRequest);
//			orderRequest.put("hiRequest", hiRequestRequest);

		//	JSONObject errorRequest = new JSONObject();
		//	errorRequest.put("code", request.getError().getCode());
		//	errorRequest.put("message", request.getError().getMessage());
		//	System.out.println(errorRequest);
	//		orderRequest.put("error", request.getError());
			
	//		JSONObject requestId = new JSONObject();
//			requestId.put("requestId", request.getResp().getRequestId());
//			orderRequest.put("resp",requestId);
			
//			NdhmOauthResponse oauth = session();
//			System.out.println("token" + oauth.getAccessToken());
//
//			String url = "https://dev.ndhm.gov.in/hiu/v0.5/health-information/hiu/on-request";
//
//			URL obj = new URL(url);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			con.setDoOutput(true);
//
//			System.out.println(con.getErrorStream());
//			con.setDoInput(true);
//			// optional default is POST
//			con.setRequestMethod("POST");
//			con.setRequestProperty("Accept-Language", "en-US");
//			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
//			con.setRequestProperty("X-CM-ID", "sbx");
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(orderRequest.toString());
//			wr.flush();
//			wr.close();
//			con.disconnect();
//			InputStream in = con.getInputStream();
//			// BufferedReader in = new BufferedReader(new
//			// InputStreamReader(con.getInputStream()));
//			String inputLine;
//			System.out.println(con.getErrorStream());
//			/* response = new StringBuffer(); */
//			StringBuffer output = new StringBuffer();
//			int c = 0;
//			while ((c = in.read()) != -1) {
//
//				output.append((char) c);
//
//			}
//			System.out.println("response:" + output.toString());
//			int responseCode = con.getResponseCode();
//			if (responseCode == 202)
//				response = true;
			
			HiuDataRequestCollection collection=new HiuDataRequestCollection();
			
			BeanUtil.map(request, collection);
		collection.setCreatedTime(new Date());
		collection.setUpdatedTime(new Date());
		hiuDataRequestRepository.save(collection);
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
	public GateWayOnRequest getHiuDataRequest(String requestId) {
		GateWayOnRequest response=null;
		try {
			HiuDataRequestCollection collection=hiuDataRequestRepository.findByRespRequestId(requestId);
			response=new GateWayOnRequest();
			if(collection !=null)
			{
				BeanUtil.map(collection, response);
			}
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}
	
	
	@Override
	public Boolean onHiuDataTransferApi(DataTransferRequest request) {
		Boolean response = false;
		try {
			
			HiuDataTransferCollection collection=new HiuDataTransferCollection(); 
			BeanUtil.map(request, collection);
			collection.setCreatedTime(new Date());
			collection.setUpdatedTime(new Date());
			hiuDataTransferRepository.save(collection);
			response=true;
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("pageNumber", request.getPageNumber());
//			orderRequest.put("pageCount", request.getPageCount());
//			orderRequest.put("transactionId", request.getTransactionId());
//			orderRequest.put("entries", request.getEntries());// list
//
//			JSONObject keyMaterialRequest = new JSONObject();
//			keyMaterialRequest.put("cryptoAlg", request.getKeyMaterial().getCryptoAlg());
//			keyMaterialRequest.put("curve", request.getKeyMaterial().getCurve());
//			keyMaterialRequest.put("nonce", request.getKeyMaterial().getNonce());
//
//			JSONObject dhPublicKeyRequest = new JSONObject();
//			dhPublicKeyRequest.put("expiry", request.getKeyMaterial().getDhPublicKey().getExpiry());
//			dhPublicKeyRequest.put("parameters", request.getKeyMaterial().getDhPublicKey().getParameters());
//			dhPublicKeyRequest.put("keyValue", request.getKeyMaterial().getDhPublicKey().getKeyValue());
//
//			keyMaterialRequest.put("dhPublicKey", dhPublicKeyRequest);
//
//			orderRequest.put("keyMaterial", keyMaterialRequest);
//
//			System.out.println("request " + orderRequest);
//			NdhmOauthResponse oauth = session();
//			System.out.println("token" + oauth.getAccessToken());
//
//			String url = "https://dev.ndhm.gov.in/patient-hiu/v0.5/health-information/transfer";
//
//			URL obj = new URL(url);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			con.setDoOutput(true);
//
//			System.out.println(con.getErrorStream());
//			con.setDoInput(true);
//			// optional default is POST
//			con.setRequestMethod("POST");
//			con.setRequestProperty("Accept-Language", "en-US");
//			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(orderRequest.toString());
//			wr.flush();
//			wr.close();
//			con.disconnect();
//			InputStream in = con.getInputStream();
//			// BufferedReader in = new BufferedReader(new
//			// InputStreamReader(con.getInputStream()));
//			String inputLine;
//			System.out.println(con.getErrorStream());
//			/* response = new StringBuffer(); */
//			StringBuffer output = new StringBuffer();
//			int c = 0;
//			while ((c = in.read()) != -1) {
//
//				output.append((char) c);
//
//			}
//			System.out.println("response:" + output.toString());
//			int responseCode = con.getResponseCode();
//			if (responseCode == 202)
//				response = true;

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public DataTransferRequest getHiuData(String transactionId) {
		DataTransferRequest response=new DataTransferRequest();
		try {
			HiuDataTransferCollection collection=hiuDataTransferRepository.findByTransactionId(transactionId);
	
			if(collection!=null)
			{
				BeanUtil.map(collection,response);
			}
			
		}
			
		
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	
	}

	

}
