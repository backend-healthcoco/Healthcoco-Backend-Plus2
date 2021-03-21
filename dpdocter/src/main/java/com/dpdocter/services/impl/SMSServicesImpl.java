package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.dpdocter.beans.Message;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSFormat;
import com.dpdocter.beans.SMSReport;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.beans.UserMobileNumbers;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MessageCollection;
import com.dpdocter.collections.SMSDeliveryReportsCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SubscriptionDetailCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MessageRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.SmsDeliveryReportsRepository;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DoctorSMSResponse;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.services.SMSServices;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.util.web.DPDoctorUtils;

@Service
public class SMSServicesImpl implements SMSServices {
	private static Logger logger = LogManager.getLogger(SMSServicesImpl.class);

	@Autowired
	private SMSTrackRepository smsTrackRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${AUTH_KEY}")
	private String AUTH_KEY;

	@Value(value = "${SENDER_ID}")
	private String SENDER_ID;

	@Value(value = "${UNICODE}")
	private String UNICODE;

	@Value(value = "${DEFAULT_ROUTE}")
	private String ROUTE;
	
	@Value(value = "${PROMOTIONAL_ROUTE}")
	private String PROMOTIONAL_ROUTE;

	@Value(value = "${DEFAULT_COUNTRY}")
	private String COUNTRY_CODE;

	@Value(value = "${SMS_POST_URL}")
	private String SMS_POST_URL;

	@Value("${is.env.production}")
	private Boolean isEnvProduction;

	@Value("${send.patient.otp.sms.from.twilio}")
	private Boolean sendSmsFromTwilio;

	@Value(value = "${mobile.numbers.resource}")
	private String MOBILE_NUMBERS_RESOURCE;
	
	@Value("${sms.tfactor.auth.key}")
	private String tfactorAuthKey;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SMSFormatRepository sMSFormatRepository;

	@Value(value = "${sms.twilio.account.sid}")
	private String ACCOUNT_SID;

	@Value(value = "${sms.twilio.auth.token}")
	private String AUTH_TOKEN;

	@Value(value = "${sms.twilio.country.code}")
	private String TWILIO_COUNTRY_CODE;

	@Value(value = "${sms.twilio.from.number}")
	private String TWILIO_FROM_NUMBER;

	@Autowired
	private SubscriptionDetailRepository subscriptionDetailRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private SmsDeliveryReportsRepository smsDeliveryReportsRepository;
	
	
	@Value(value = "${SERVICE_ID}")
	private String SID;
	
	@Value(value = "${API_KEY}")
	private String KEY;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;


	
//	@Override
//	@Transactional
//	public Boolean sendSMS(SMSTrackDetail smsTrackDetail, Boolean save) {
//		Boolean response = false;
//		String responseId = null;
//		LocationCollection locationCollection = null;
//		try {
//			if(smsTrackDetail.getLocationId() != null)
//			{
//				locationCollection = locationRepository.findById(smsTrackDetail.getLocationId()).orElse(null);
//			}
//			
//			Message message = new Message();
//			List<SMS> smsList = new ArrayList<SMS>();
//			message.setAuthKey(AUTH_KEY);
//			message.setCountryCode(COUNTRY_CODE);
//			message.setRoute(ROUTE);
//			if(locationCollection != null && !DPDoctorUtils.anyStringEmpty(locationCollection.getSmsCode()))
//			{
//				message.setSenderId(locationCollection.getSmsCode());
//			}
//			else
//			{
//				message.setSenderId(SENDER_ID);
//			}
//			message.setUnicode(UNICODE);
//			Boolean isSMSInAccount = true;
//			UserMobileNumbers userNumber = null;
////			SubscriptionDetailCollection subscriptionDetailCollection = null;
//
//			if (!isEnvProduction) {
//				FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
//				ObjectInputStream in = new ObjectInputStream(fileIn);
//				userNumber = (UserMobileNumbers) in.readObject();
//				in.close();
//				fileIn.close();
//			}
////			if (!DPDoctorUtils.anyStringEmpty(smsTrackDetail.getLocationId())) {
////				
////				List<SubscriptionDetailCollection> subscriptionDetailCollections = subscriptionDetailRepository
////						.findSuscriptionDetailBylocationId(smsTrackDetail.getLocationId());
////				if(subscriptionDetailCollections !=null)subscriptionDetailCollection = subscriptionDetailCollections.get(0);
////			}
//
//			for (SMSDetail smsDetails : smsTrackDetail.getSmsDetails()) {
//				/*if (!DPDoctorUtils.anyStringEmpty(smsTrackDetail.getLocationId()))
//					isSMSInAccount = this.checkNoOFsms(smsDetails.getSms().getSmsText(), subscriptionDetailCollection);*/
//				if (isSMSInAccount) {
//					if (!isEnvProduction) {
//						if (userNumber != null && smsDetails.getSms() != null
//								&& smsDetails.getSms().getSmsAddress() != null) {
//							String recipient = smsDetails.getSms().getSmsAddress().getRecipient();
//							if (userNumber.mobileNumber.contains(recipient)) {
//								smsDetails.getSms().getSmsAddress().setRecipient(COUNTRY_CODE + recipient);
//								SMS sms = new SMS();
//								BeanUtil.map(smsDetails.getSms(), sms);
//								if (sms.getSmsText() != null)
//									sms.setSmsText(UriUtils.encode(sms.getSmsText(), "UTF-8"));
//								smsList.add(sms);
//								message.setSms(smsList);
//								String xmlSMSData = createXMLData(message);
//								responseId = hitSMSUrl(SMS_POST_URL, xmlSMSData);
//								smsTrackDetail.setResponseId(responseId);
//							}
//						}
//					} else {
//						SMS sms = new SMS();
//						BeanUtil.map(smsDetails.getSms(), sms);
//						if (sms.getSmsText() != null)
//							sms.setSmsText(UriUtils.encode(sms.getSmsText(), "UTF-8"));
//						smsList.add(sms);
//						message.setSms(smsList);
//						String xmlSMSData = createXMLData(message);
//						responseId = hitSMSUrl(SMS_POST_URL, xmlSMSData);
//						smsTrackDetail.setResponseId(responseId);
//					}
//				}
//
//				if (save)
//					smsTrackRepository.save(smsTrackDetail);
//				if (!DPDoctorUtils.anyStringEmpty(responseId))
//					response = true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Error : " + e.getMessage());
//			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
//		}
//		return response;
//	}
	
	

	@Async
	@Override
	@Transactional
	public Boolean sendAndSaveOTPSMS(String message, String mobileNumber,String otp) {
		Boolean response = false;
		try {
			UserMobileNumbers userNumber = null;

			if (!isEnvProduction) {
				FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				userNumber = (UserMobileNumbers) in.readObject();
				in.close();
				fileIn.close();
			}
			
			if (!isEnvProduction) {
				if (userNumber != null && message != null && mobileNumber != null) {
					String recipient = mobileNumber;
					if (userNumber.mobileNumber.contains(recipient)) {
						// xmlSMSData = createXMLData(message);
						response=getOTPSMSResponse(recipient, message, otp);

					}
				}
			} else {
				if (message != null && mobileNumber != null) {
					String recipient = mobileNumber;
					response=getOTPSMSResponse(recipient, message, otp);

				}
			}
		} catch (Exception e) {
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}
	
	private static String hitSMSUrl(final String SMS_URL, String xmlSMSData) {
		StringBuffer response = null;
		try {
			logger.info("Sending SMS...!");
			URL url = new URL(SMS_URL);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setRequestMethod("POST");

			DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
			wr.writeBytes(xmlSMSData);
			wr.flush();
			wr.close();
			httpConnection.disconnect();

			BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			logger.info("SMS Sent...!");
			logger.info("Response : " + response.toString());

			in.close();

		} catch (Exception e) {
			logger.error("Error : " + e.getMessage());
			response = new StringBuffer();
			response.append("-2");
		}

		return response.toString();
	}

	private String createXMLData(Message message) {
		String xmlData = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

			StringWriter xmlStringWriter = new StringWriter();

			jaxbMarshaller.marshal(message, xmlStringWriter);

			xmlData = "data=" + xmlStringWriter.toString();
		} catch (JAXBException e) {
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return xmlData;
	}

	@Override
	@Transactional
	public SMSResponse getSMS(int page, int size, String doctorId, String locationId, String hospitalId) {
		SMSResponse response = null;
		List<SMSTrackDetail> smsTrackDetails = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			String[] type = { "APPOINTMENT", "PRESCRIPTION", "VISITS" };
			if (doctorObjectId == null) {
				if (size > 0)
					smsTrackDetails = smsTrackRepository.findByLocationIdAndHospitalIdAndTypeIn(locationObjectId, hospitalObjectId,
							type, PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					smsTrackDetails = smsTrackRepository.findByLocationIdAndHospitalIdAndTypeIn(locationObjectId, hospitalObjectId,
							type, new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
				if (size > 0)
					smsTrackDetails = smsTrackRepository.findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(doctorObjectId,
							locationObjectId, hospitalObjectId, type,
							PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					smsTrackDetails = smsTrackRepository.findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(doctorObjectId,
							locationObjectId, hospitalObjectId, type, new Sort(Sort.Direction.DESC, "createdTime"));
			}

			@SuppressWarnings("unchecked")
			Collection<ObjectId> doctorIds = CollectionUtils.collect(smsTrackDetails,
					new BeanToPropertyValueTransformer("doctorId"));
			if (doctorIds != null && !doctorIds.isEmpty()) {
				response = new SMSResponse();
				List<DoctorSMSResponse> doctors = getSpecifiedDoctors(doctorIds, locationObjectId, hospitalObjectId);
				response.setDoctors(doctors);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting SMS");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting SMS");
		}
		return response;
	}

	private List<DoctorSMSResponse> getSpecifiedDoctors(Collection<ObjectId> doctorIds, ObjectId locationId,
			ObjectId hospitalId) {
		List<DoctorSMSResponse> doctors = new ArrayList<DoctorSMSResponse>();
		for (ObjectId doctorId : doctorIds) {
			DoctorSMSResponse doctorSMSResponse = new DoctorSMSResponse();
			int count = smsTrackRepository.getDoctorsSMSCount(doctorId, locationId, hospitalId);
			UserCollection user = userRepository.findById(doctorId).orElse(null);
			doctorSMSResponse.setDoctorId(doctorId.toString());
			if (user != null)
				doctorSMSResponse.setDoctorName(user.getFirstName());
			doctorSMSResponse.setMsgSentCount(count + "");
			doctors.add(doctorSMSResponse);
		}
		return doctors;
	}

	@Override
	@Transactional
	public List<SMSTrack> getSMSDetails(long page, int size, String patientId, String doctorId, String locationId,
			String hospitalId) {
		List<SMSTrack> response = null;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("type").in("APPOINTMENT", "PRESCRIPTION", "VISITS");

			if (doctorObjectId == null) {
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId)) {
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
							.and("smsDetails.userId").is(patientObjectId);
				}
			} else {
				if (DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
					criteria.and("doctorId").is(doctorObjectId).and("smsDetails.userId").is(patientObjectId);
				else
					criteria.and("doctorId").is(doctorObjectId).and("locationId").is(locationObjectId).and("hospitalId")
							.is(hospitalObjectId).and("smsDetails.userId").is(patientObjectId);
			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "smsDetails.sentTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "smsDetails.sentTime")));
			}
			AggregationResults<SMSTrack> aggregationResults = mongoTemplate.aggregate(aggregation, SMSTrackDetail.class,
					SMSTrack.class);
			response = aggregationResults.getMappedResults();
		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	@Transactional
	public void updateDeliveryReports(List<SMSDeliveryReports> request) {
		try {
			SMSDeliveryReportsCollection smsDeliveryReportsCollection=new SMSDeliveryReportsCollection();
			for (SMSDeliveryReports smsDeliveryReport : request) {
		//for checking if request is coming from 3rd party		
				BeanUtil.map(smsDeliveryReport, smsDeliveryReportsCollection);
				smsDeliveryReportsRepository.save(smsDeliveryReportsCollection);
				//
				SMSTrackDetail smsTrackDetail = smsTrackRepository.findByResponseId(smsDeliveryReport.getRequestId());
				if (smsTrackDetail != null) {
					for (SMSDetail smsDetail : smsTrackDetail.getSmsDetails()) {
						for (SMSReport report : smsDeliveryReport.getReport()) {
							if (smsDetail.getSms() != null && smsDetail.getSms().getSmsAddress() != null
									&& smsDetail.getSms().getSmsAddress().getRecipient() != null) {
								if (smsDetail.getSms().getSmsAddress().getRecipient().equals(report.getNumber().replaceFirst("91", ""))) {
									smsDetail.setDeliveredTime(report.getDate());
									smsDetail.setDeliveryStatus(SMSStatus.valueOf(report.getDesc()));
								}
							}
						}
					}
					smsTrackRepository.save(smsTrackDetail);
				}

			}
		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}
	@Override
	@Transactional
	public void addNumber(String mobileNumber) {
		try {
			if (!isEnvProduction) {
				FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				UserMobileNumbers userNumber = (UserMobileNumbers) in.readObject();
				in.close();
				fileIn.close();

				if (!userNumber.mobileNumber.contains(mobileNumber))
					userNumber.mobileNumber.add(mobileNumber);

				FileOutputStream fileOut = new FileOutputStream(MOBILE_NUMBERS_RESOURCE);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(userNumber);
				out.close();
				fileOut.close();
			}
		} catch (BusinessException | IOException | ClassNotFoundException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public void deleteNumber(String mobileNumber) {
		try {
			if (!isEnvProduction) {
				FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				UserMobileNumbers userNumber = (UserMobileNumbers) in.readObject();
				in.close();
				fileIn.close();

				if (userNumber.mobileNumber.contains(mobileNumber))
					userNumber.mobileNumber.remove(mobileNumber);

				FileOutputStream fileOut = new FileOutputStream(MOBILE_NUMBERS_RESOURCE);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(userNumber);
				out.close();
				fileOut.close();
			}
		} catch (BusinessException | IOException | ClassNotFoundException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	@Transactional
	public SMSTrackDetail createSMSTrackDetail(String doctorId, String locationId, String hospitalId, String patientId,
			String patientName, String message, String mobileNumber, String type) {
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		try {
			smsTrackDetail.setDoctorId(!DPDoctorUtils.anyStringEmpty(doctorId) ? new ObjectId(doctorId) : null);
			smsTrackDetail.setHospitalId(!DPDoctorUtils.anyStringEmpty(locationId) ? new ObjectId(hospitalId) : null);
			smsTrackDetail.setLocationId(!DPDoctorUtils.anyStringEmpty(hospitalId) ? new ObjectId(locationId) : null);
			smsTrackDetail.setType(type);
			SMSDetail smsDetail = new SMSDetail();
			smsDetail.setUserId(!DPDoctorUtils.anyStringEmpty(patientId) ? new ObjectId(patientId) : null);
			smsDetail.setUserName(patientName);
			SMS sms = new SMS();
			sms.setSmsText(message);

			SMSAddress smsAddress = new SMSAddress();
			smsAddress.setRecipient(mobileNumber);
			sms.setSmsAddress(smsAddress);

			smsDetail.setSms(sms);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setSmsDetails(smsDetails);

		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return smsTrackDetail;
	}

	@Override
	@Transactional
	public SMSFormat addSmsFormat(SMSFormat request) {
		SMSFormat response = null;
		SMSFormatCollection smsFormatCollection = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			smsFormatCollection = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalIdAndType(doctorObjectId, locationObjectId, hospitalObjectId,
					request.getType().getType());
			if (smsFormatCollection == null) {
				smsFormatCollection = new SMSFormatCollection();
				BeanUtil.map(request, smsFormatCollection);
				smsFormatCollection.setCreatedTime(new Date());
			} else {
				smsFormatCollection.setContent(request.getContent());
				smsFormatCollection.setUpdatedTime(new Date());
			}
			smsFormatCollection = sMSFormatRepository.save(smsFormatCollection);
			response = new SMSFormat();
			BeanUtil.map(smsFormatCollection, response);
		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while Adding/Editing Sms Format");
		}
		return response;
	}

	@Override
	@Transactional
	public List<SMSFormat> getSmsFormat(String doctorId, String locationId, String hospitalId, String type) {
		List<SMSFormat> response = null;
		List<SMSFormatCollection> smsFormatCollections = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			if (type != null) {
				SMSFormatCollection smsFormatCollection = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalIdAndType(doctorObjectId, locationObjectId,
						hospitalObjectId, type);
				if (smsFormatCollection != null) {
					smsFormatCollections = new ArrayList<SMSFormatCollection>();
					smsFormatCollections.add(smsFormatCollection);
				}
			} else
				smsFormatCollections = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalId(doctorObjectId, locationObjectId, hospitalObjectId);
			if (smsFormatCollections != null) {
				response = new ArrayList<SMSFormat>();
				for (SMSFormatCollection smsFormatCollection : smsFormatCollections) {
					SMSFormat smsFormat = new SMSFormat();
					BeanUtil.map(smsFormatCollection, smsFormat);
					response.add(smsFormat);
				}
			}
		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while Sending Sms Format");
		}
		return response;
	}

	/*
	 * @Override public Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, Boolean
	 * save) { Boolean response = false; try { // if (!isEnvProduction) { response =
	 * sendSMS(smsTrackDetail, save); // } else { // if (sendSmsFromTwilio) { // for
	 * (SMSDetail smsDetails : smsTrackDetail.getSmsDetails()) { // if
	 * (smsDetails.getSms() != null && smsDetails.getSms().getSmsAddress() != null)
	 * { // String recipient = TWILIO_COUNTRY_CODE +
	 * smsDetails.getSms().getSmsAddress().getRecipient(); //
	 * smsDetails.getSms().getSmsAddress().setRecipient(recipient); // SMS sms = new
	 * SMS(); // BeanUtil.map(smsDetails.getSms(), sms); // TwilioRestClient client
	 * = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN); // // List<NameValuePair>
	 * params = new ArrayList<NameValuePair>(); // params.add(new
	 * BasicNameValuePair("To", recipient)); // params.add(new
	 * BasicNameValuePair("From", TWILIO_FROM_NUMBER));// 15005550006 //
	 * params.add(new BasicNameValuePair("Body", sms.getSmsText())); // //
	 * MessageFactory messageFactory = client.getAccount().getMessageFactory(); //
	 * com.twilio.sdk.resource.instance.Message message =
	 * messageFactory.create(params); // responseId = message.getSid(); //
	 * smsTrackDetail.setResponseId(responseId); // } // } // if
	 * (!DPDoctorUtils.anyStringEmpty(responseId)) // response = true; // } else {
	 * // response = sendSMS(smsTrackDetail, save); // } // }
	 * 
	 * } catch (BusinessException e) { logger.error(e); throw new
	 * BusinessException(ServiceError.Unknown, "Error while sendind Sms"); } return
	 * response; }
	 */

	public Boolean checkNoOFsms(String massage, SubscriptionDetailCollection subscriptionDetailCollection) {
		Boolean response = false;
		CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
		Double count;
		int div;
		boolean status = asciiEncoder.canEncode(massage);
		Double length = (double) massage.length();
		if (status)
			div = 160;
		else
			div = 70;
		count = (length / 160) - Math.floor(length / div);
		if (count > 0) {
			count = Math.floor(length / div) + 1;
		} else {
			count = Math.floor(length / div);

		}

		if (subscriptionDetailCollection != null) {
			if (subscriptionDetailCollection.getNoOfsms() > 0) {
				subscriptionDetailCollection.setNoOfsms(subscriptionDetailCollection.getNoOfsms() - count.intValue());
				subscriptionDetailCollection = subscriptionDetailRepository.save(subscriptionDetailCollection);
				response = true;
			} else
				throw new BusinessException(ServiceError.Unknown, "Error while Sending low sms in Doctor Account");

		} else {
			throw new BusinessException(ServiceError.Unknown, "Error while Sending  Invalid location Id");
		}
		return response;
	}
	
//	@Override
//	public String getBulkSMSResponse(List<String> mobileNumbers, String message) {
//
//		StringBuffer response = new StringBuffer();
//		try {
//			Set<String> numbers= new HashSet<>(mobileNumbers);
//			List<String> numberlist = new ArrayList<String> (numbers);
//			String numberString = StringUtils.join(numberlist, ',');
//			// String password = new String(loginRequest.getPassword());
//			
//			message = StringEscapeUtils.unescapeJava(message);
//			String url =  "http://dndsms.resellergrow.com/api/sendhttp.php?authkey=" + AUTH_KEY + "&mobiles="
//					+ numberString + "&message=" + UriUtils.encode(message, "UTF-8") + "&sender="
//					+ SENDER_ID + "&route=" + PROMOTIONAL_ROUTE + "&country=" + COUNTRY_CODE+ "&unicode="+UNICODE;
//			URL obj = new URL(url);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			System.out.println("url:"+url);
//			// optional default is POST
//			con.setRequestMethod("GET");
//            System.out.println(con.getResponseCode());
//			// add request header
//			// con.setRequestProperty("User-Agent", USER_AGENT);
//			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//			con.setRequestProperty("Accept-Charset", "UTF-8");
//
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			/* response = new StringBuffer(); */
//
//			while ((inputLine = in.readLine()) != null) {
//
//				response.append(inputLine);
//
//			}
//			in.close();
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			return "Failed";
//		}
//
//		return response.toString();
//
//	}
	
	@Override
	public Boolean getOTPSMSResponse(String mobileNumber, String message , String otp) {

		Boolean boolResponse = false;
		//http://dndsms.resellergrow.com/api/otp.php?authkey=93114AV2rXJuxL56001692&mobile=9766914900&message=0808&sender=HTCOCO&otp=0808
		StringBuffer response = new StringBuffer();
		try {
			// String password = new String(loginRequest.getPassword());
			String url =  "http://dndsms.resellergrow.com/api/otp.php?authkey=" + AUTH_KEY + "&mobile="
					+ mobileNumber + "&message=" + URLEncoder.encode(message , "UTF-8") + "&sender="
					+ SENDER_ID + "&otp=" + otp;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is POST
			con.setRequestMethod("GET");

			// add request header
			// con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			con.setRequestProperty("Accept-Charset", "UTF-8");
			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */

			while ((inputLine = in.readLine()) != null) {

				response.append(inputLine);

			}
			in.close();			
			if(responseCode == 200)boolResponse = true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}

		return boolResponse;

	}

	@Override
	public Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, String otp, Boolean save) {
		Boolean response = false;
		try {
			if(smsTrackDetail.getSmsDetails().size() > 0)
			{
				response = sendAndSaveOTPSMS(smsTrackDetail.getSmsDetails().get(0).getSms().getSmsText(), smsTrackDetail.getSmsDetails().get(0).getSms().getSmsAddress().getRecipient(), otp);
			}
			response = true;
		} catch (BusinessException e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while sendind Sms");
		}
		return response;
	}
	
	@Override
	public Boolean sendPatientOTP(String mobileNumber , String otp)
	{
		Boolean boolResponse = false;
		//http://dndsms.resellergrow.com/api/otp.php?authkey=93114AV2rXJuxL56001692&mobile=9766914900&message=0808&sender=HTCOCO&otp=0808
		StringBuffer response = new StringBuffer();
		try {
			String url = "https://2factor.in/API/V1/"+tfactorAuthKey+"/SMS/"+mobileNumber+"/"+otp+"/OTP";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is POST
			con.setRequestMethod("GET");
			// add request header
			// con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			con.setRequestProperty("Accept-Charset", "UTF-8");
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				response.append(inputLine);

			}
			in.close();			
			if(responseCode == 200)boolResponse = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return boolResponse;
	}
	
	public static void main(String[] args) throws Exception {
		
		StringBuilder builder = new StringBuilder();
		builder.append("Healthcoco user {patientName}! {doctorName} has suggested you below scan(s).\n");
		builder.append("\n");
		builder.append("TMJ both (2D) tooth no [4] \n");
		builder.append("Maxillary Sinus (2D) tooth no [6,9] \n");
		builder.append("\n");
		builder.append("{locationName} {clinicNumber} {locationMapLink}");
		String text = builder.toString();
		
		String url =  "http://dndsms.resellergrow.com/api/sendhttp.php?authkey=" + "93114AV2rXJuxL56001692" + "&mobiles="
				+ "9766914900" + "&message=" + UriUtils.encode(text, "UTF-8") + "&sender="
				+ "HTCOCO" + "&route=" + "4" + "&country=" + "91"+ "&unicode="+"1";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is POST
		con.setRequestMethod("GET");

		// add request header
		// con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		con.setRequestProperty("Accept-Charset", "UTF-8");
//		int responseCode = con.getResponseCode();
//
//		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String inputLine;
		/* response = new StringBuffer(); */

		
	}
	
	//old dndsms service
//	@Override
//	public String getBulkSMSResponse(List<String> mobileNumbers, String message,String doctorId,String locationId,long subCredits) {
//		StringBuffer response = new StringBuffer();
//		try {
//			Set<String> numbers = new HashSet<>(mobileNumbers);
//			List<String> numberlist = new ArrayList<String>(numbers);
//			String numberString = StringUtils.join(numberlist, ',');
//			// String password = new String(loginRequest.getPassword());
//			
//			 SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
//				
//				smsTrackDetail.setType("BULK_SMS");
//				smsTrackDetail.setDoctorId(new ObjectId(doctorId));
//				smsTrackDetail.setLocationId(new ObjectId(locationId));
//				smsTrackDetail.setTotalCreditsSpent(subCredits);
//				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
//
//			
//			JAXBContext contextObj = JAXBContext.newInstance(MessageXmlbean.class);  
//			Marshaller marshallerObj = contextObj.createMarshaller();  
//		    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
//		    
//		    message = StringEscapeUtils.unescapeJava(message);
//			//String	messages=UriUtils.encode(message, "UTF-8");
//		    List<XMLMobile> numberlists=new ArrayList<XMLMobile>();
//		   
////		    XMLMobile mobile2=new XMLMobile();
////		    
////		    
////		    mobile1.setMobileNumber("9604248471");
////		    mobile2.setMobileNumber("7219653706");
////		    numberlists.add(mobile1);
////		    numberlists.add(mobile2);
//		    
//		   
//		    
//		    for(String mobiles:mobileNumbers) {
//		    	
//		    	// sms.setSmsText(message);
//					
//					//smsAddress.setRecipient(mobiles);
//					
//				    
//				//	sms.setSmsAddress(new SMSAddress(mobiles));
//					
//					smsDetails.add(new SMSDetail(new SMS(message, new SMSAddress(mobiles)), SMSStatus.IN_PROGRESS));
//		     //mobile1.setMobileNumber(mobiles);
//		     numberlists.add(new XMLMobile(mobiles));
//		     
//		    }
//		   
//		   // smsDetail.setSms(sms);
//			//smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
//		
//		smsTrackDetail.setSmsDetails(smsDetails);
//		    MessageXmlbean xmlBean=new MessageXmlbean(AUTH_KEY,new XmlMessage(message,(numberlists)),SENDER_ID,PROMOTIONAL_ROUTE,COUNTRY_CODE,UNICODE);
//		   // marshallerObj.marshal(xmlBean, os);
//
//			
//			
//			String strUrl = "http://dndsms.resellergrow.com/api/postsms.php";
//			
//			
////				url = "http://dndsms.resellergrow.com/api/sendhttp.php?authkey=" + AUTH_KEY + "&mobiles=" + numberString
////						+ "&message=" + UriUtils.encode(message, "UTF-8") + "&sender=" + SENDER_ID + "&route="
////						+ PROMOTIONAL_ROUTE + "&country=" + COUNTRY_CODE + "&unicode=" + UNICODE;
//
//			
//			URL obj = new URL(strUrl);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			
//			con.setDoOutput(true);
//			
//			con.setDoInput(true);
//			// optional default is POST
//			con.setRequestMethod("POST");
//			
//			
//
//			// add request header
//			// con.setRequestProperty("User-Agent", USER_AGENT);
//			con.setRequestProperty("User-Agent",
//					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//			con.setRequestProperty("Accept-Charset", "UTF-8");
//			
//			 DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			 StringWriter sw = new StringWriter();
//			 
//			 marshallerObj.marshal(xmlBean,sw);
//			 
//			String sq= sw.toString();
//		
//			System.out.println("Object:"+sq);
//          //   wr.writeBytes(sq);
//               wr.write(sq.getBytes("UTF-8")); //for unicode message's instead of wr.writeBytes(param);
//
//             wr.flush();
//             wr.close();
//             con.disconnect();
//
//
//
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			/* response = new StringBuffer(); */
//
//			while ((inputLine = in.readLine()) != null) {
//
//				response.append(inputLine);
//				System.out.println("response:"+response.toString());
//			}
//			smsTrackDetail.setResponseId(response.toString());
//			smsTrackDetail.setCreatedTime(new Date());
//			smsTrackDetail.setUpdatedTime(new Date());
//			smsTrackRepository.save(smsTrackDetail);
//			in.close();
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			return "Failed";
//		}
//
//		return response.toString();
//
//	}

	//new sms service
		@Override
		public String getBulkSMSResponse(List<String>mobileNumbers,String message,String doctorId,String locationId,long subCredits) {
			String response = null;
			try {

				 
				 message = StringEscapeUtils.unescapeJava(message);
	     	String type="MKT";
			
				String strUrl = "https://api.ap.kaleyra.io/v1/"+SID+"/messages";
				
				List<String> numberlist = new ArrayList<String>(mobileNumbers);
				String numberString = StringUtils.join(numberlist, ',');
				//working
				HttpClient client = HttpClients.custom().build();
				HttpUriRequest httprequest = RequestBuilder.post().addParameter("to",numberString)
						.addParameter("type", type)
						.addParameter("body", message)
						.addParameter("sender",SENDER_ID)
						.addParameter("unicode", "1")
				  .setUri(strUrl)
				  .setHeader( "api-key", KEY)
				  .build();
				//System.out.println("response"+client.execute(httprequest));
				 org.apache.http.HttpResponse responses = client.execute(httprequest);

				 
				 ByteArrayOutputStream out = new ByteArrayOutputStream();
				   
				   
				 responses.getEntity().writeTo(out);
				 String responseString = out.toString();
				System.out.println("responseString"+responseString);
				  ObjectMapper mapper = new ObjectMapper();

				MessageResponse list = mapper.readValue(out.toString(),MessageResponse.class);
		       
				MessageCollection collection=new MessageCollection();
				list.setMessageId(list.getId());
				list.setId(null);
				BeanUtil.map(list, collection);
				collection.setDoctorId(new ObjectId(doctorId));
				collection.setLocationId(new ObjectId(locationId));
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				collection.setMessageType("BULK_SMS");
				collection.setTotalCreditsSpent(subCredits);
				messageRepository.save(collection);			
				response=list.getMessageId();
				
		
			} catch (Exception e) {

				e.printStackTrace();
				return "Failed";
			}

			return response.toString();

		}

	@Override
		@Transactional
		public Boolean sendSMS(SMSTrackDetail smsTrackDetail, Boolean save) {
			Boolean response = false;
			String responseId = null;
			
			
			LocationCollection locationCollection = null;
			try {
				if(smsTrackDetail.getLocationId() != null)
				{
					locationCollection = locationRepository.findById(smsTrackDetail.getLocationId()).orElse(null);
				}
				
				String type="TXN";
				String message=smsTrackDetail.getSmsDetails().get(0).getSms().getSmsText();
				String mobileNumber=smsTrackDetail.getSmsDetails().get(0).getSms().getSmsAddress().getRecipient();
				String strUrl = "https://api.ap.kaleyra.io/v1/"+SID+"/messages";
			
				String senderId=null;
				
					senderId=SENDER_ID;
				
				
			
				 ObjectMapper mapper = new ObjectMapper();
				Boolean isSMSInAccount = true;
				UserMobileNumbers userNumber = null;
//				SubscriptionDetailCollection subscriptionDetailCollection = null;

				if (!isEnvProduction) {
					FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					userNumber = (UserMobileNumbers) in.readObject();
					in.close();
					fileIn.close();
					System.out.println(userNumber);
				}
//				if (!DPDoctorUtils.anyStringEmpty(smsTrackDetail.getLocationId())) {
//					
//					List<SubscriptionDetailCollection> subscriptionDetailCollections = subscriptionDetailRepository
//							.findSuscriptionDetailBylocationId(smsTrackDetail.getLocationId());
//					if(subscriptionDetailCollections !=null)subscriptionDetailCollection = subscriptionDetailCollections.get(0);
//				}
				
				 ByteArrayOutputStream out = new ByteArrayOutputStream();
				 MessageResponse list=null;
			//	for (SMSDetail smsDetails : smsTrackDetail.getSmsDetails()) {
					/*if (!DPDoctorUtils.anyStringEmpty(smsTrackDetail.getLocationId()))
						isSMSInAccount = this.checkNoOFsms(smsDetails.getSms().getSmsText(), subscriptionDetailCollection);*/
				 DoctorCollection doctorClinicProfileCollection=doctorRepository.findByUserId(smsTrackDetail.getDoctorId());

					if (doctorClinicProfileCollection.getIsTransactionalSms() == true) {
				 //if (isSMSInAccount) {
			//			if (!isEnvProduction) {
						//	String recipient = smsDetails.getSms().getSmsAddress().getRecipient();
//							if (userNumber != null && smsDetails.getSms() != null
//									&& smsDetails.getSms().getSmsAddress() != null) {
							//	if (userNumber.mobileNumber.contains(mobileNumber)) {
								//	smsDetails.getSms().getSmsAddress().setRecipient(COUNTRY_CODE + mobileNumber);

								HttpClient client = HttpClients.custom().build();
								HttpUriRequest httprequest = RequestBuilder.post().addParameter("to",COUNTRY_CODE + mobileNumber)
										.addParameter("type", type)
										.addParameter("body", message)
										.addParameter("sender",SENDER_ID)
										.addParameter("unicode", "1")
										.addParameter("template_id", smsTrackDetail.getTemplateId())
								  .setUri(strUrl)
								  .setHeader( "api-key", KEY)
								  .build();
								//System.out.println("response"+client.execute(httprequest));
								System.out.println("senderId"+senderId);
								 org.apache.http.HttpResponse responses = client.execute(httprequest);
								 responses.getEntity().writeTo(out);
								 list = mapper.readValue(out.toString(),MessageResponse.class);
								}
				//			}
				//			}
						 
					

				
					if (save)
					{
					   
					   
					 
					 String responseString = out.toString();
					System.out.println("responseString"+responseString);
					 

					
			       
					MessageCollection collection=new MessageCollection();
					list.setMessageId(list.getId());
					list.setId(null);
					BeanUtil.map(list, collection);
					collection.setDoctorId(smsTrackDetail.getDoctorId());
					collection.setLocationId(smsTrackDetail.getLocationId());
					collection.setHospitalId(smsTrackDetail.getHospitalId());
					collection.setCreatedTime(new Date());
					collection.setUpdatedTime(new Date());
					collection.setMessageType(smsTrackDetail.getType());
					messageRepository.save(collection);	
					}
					//response=list.getMessageId();
					if (!DPDoctorUtils.anyStringEmpty(responseId))
						response = true;
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error : " + e.getMessage());
				throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
			}
			return response;
		}

	
	@Override
	public Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, Boolean save) {
		Boolean response = false;
	try {

//				response = sendSMS(smsTrackDetail, save);

				String responseId = null;
				
					String type="OTP";
					String message=smsTrackDetail.getSmsDetails().get(0).getSms().getSmsText();
					String mobileNumber=smsTrackDetail.getSmsDetails().get(0).getSms().getSmsAddress().getRecipient();
					String strUrl = "https://api.ap.kaleyra.io/v1/"+SID+"/messages";
				
					String senderId=null;
					
						senderId=SENDER_ID;
					
					
				
					 ObjectMapper mapper = new ObjectMapper();
					Boolean isSMSInAccount = true;
					UserMobileNumbers userNumber = null;
//					SubscriptionDetailCollection subscriptionDetailCollection = null;

					if (!isEnvProduction) {
						FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						userNumber = (UserMobileNumbers) in.readObject();
						in.close();
						fileIn.close();
					}
//					if (!DPDoctorUtils.anyStringEmpty(smsTrackDetail.getLocationId())) {
//						
//						List<SubscriptionDetailCollection> subscriptionDetailCollections = subscriptionDetailRepository
//								.findSuscriptionDetailBylocationId(smsTrackDetail.getLocationId());
//						if(subscriptionDetailCollections !=null)subscriptionDetailCollection = subscriptionDetailCollections.get(0);
//					}
					 ByteArrayOutputStream out = new ByteArrayOutputStream();
					 MessageResponse list=null;
				//	for (SMSDetail smsDetails : smsTrackDetail.getSmsDetails()) {
						/*if (!DPDoctorUtils.anyStringEmpty(smsTrackDetail.getLocationId()))
							isSMSInAccount = this.checkNoOFsms(smsDetails.getSms().getSmsText(), subscriptionDetailCollection);*/
						if (isSMSInAccount) {
					//		if (!isEnvProduction) {
							//	String recipient = smsDetails.getSms().getSmsAddress().getRecipient();
//								if (userNumber != null && smsDetails.getSms() != null
//										&& smsDetails.getSms().getSmsAddress() != null) {
	//								if (userNumber.mobileNumber.contains(mobileNumber)) {
									//	smsDetails.getSms().getSmsAddress().setRecipient(COUNTRY_CODE + mobileNumber);

									HttpClient client = HttpClients.custom().build();
									HttpUriRequest httprequest = RequestBuilder.post().addParameter("to",COUNTRY_CODE + mobileNumber)
											.addParameter("type", type)
											.addParameter("body", message)
											.addParameter("sender",SENDER_ID)
											.addParameter("unicode", "1")
											.addParameter("template_id", smsTrackDetail.getTemplateId())
									  .setUri(strUrl)
									  .setHeader( "api-key", KEY)
									  .build();
									//System.out.println("response"+client.execute(httprequest));
									System.out.println("senderId"+senderId);
									 org.apache.http.HttpResponse responses = client.execute(httprequest);
									 responses.getEntity().writeTo(out);
									 list = mapper.readValue(out.toString(),MessageResponse.class);
									 response = true;
									}
							//	}
				//				}
							 
						
						if (save)
						{
						   
						   
						 
						 String responseString = out.toString();
						System.out.println("responseString"+responseString);
						 
		       
						MessageCollection collection=new MessageCollection();
						list.setMessageId(list.getId());
						list.setId(null);
						BeanUtil.map(list, collection);
						collection.setDoctorId(smsTrackDetail.getDoctorId());
						collection.setLocationId(smsTrackDetail.getLocationId());
						collection.setHospitalId(smsTrackDetail.getHospitalId());
						collection.setCreatedTime(new Date());
						collection.setUpdatedTime(new Date());
						collection.setMessageType(smsTrackDetail.getType());
						messageRepository.save(collection);	
						response = true;
						}
						//response=list.getMessageId();
						
							
					
				

		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while sending Sms");
		} 
		return response;
	}
	
	
}
