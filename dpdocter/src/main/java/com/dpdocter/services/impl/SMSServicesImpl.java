package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.bson.Document;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.MessageStatus;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSFormat;
import com.dpdocter.beans.SMSReport;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.beans.SmsParts;
import com.dpdocter.beans.UserMobileNumbers;
import com.dpdocter.collections.BulkSmsHistoryCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MessageCollection;
import com.dpdocter.collections.SMSDeliveryReportsCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SubscriptionCollection;
import com.dpdocter.collections.SubscriptionDetailCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BulkSmsHistoryRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MessageRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.SmsDeliveryReportsRepository;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DoctorCollectionLookupResponse;
import com.dpdocter.response.DoctorSMSResponse;
import com.dpdocter.response.MessageIdLookupResponse;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SmsSpitterServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class SMSServicesImpl implements SMSServices {
	private static Logger logger = Logger.getLogger(SMSServicesImpl.class);

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
	private SmsDeliveryReportsRepository smsDeliveryReportsRepository;

	@Value(value = "${SERVICE_ID}")
	private String SID;

	@Value(value = "${API_KEY}")
	private String KEY;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Value(value = "${SMILEBIRD_SENDER_ID}")
	private String SMILEBIRD_SENDER_ID;

	@Autowired
	private SmsSpitterServices smsSpitterServices;

	@Autowired
	private BulkSmsHistoryRepository bulkSmsHistoryRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Value(value = "${interakt.secret.key}")
	private String secretKey;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private LocationRepository locationRepository;

	@Async
	@Override
	@Transactional
	public Boolean sendAndSaveOTPSMS(String message, String mobileNumber, String otp) {
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
						response = getOTPSMSResponse(recipient, message, otp);

					}
				}
			} else {
				if (message != null && mobileNumber != null) {
					String recipient = mobileNumber;
					response = getOTPSMSResponse(recipient, message, otp);

				}
			}
		} catch (Exception e) {
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
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
					smsTrackDetails = smsTrackRepository.findByLocationIdAndHospitalIdAndTypeIn(locationObjectId,
							hospitalObjectId, type, PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					smsTrackDetails = smsTrackRepository.findByLocationIdAndHospitalIdAndTypeIn(locationObjectId,
							hospitalObjectId, type, new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
				if (size > 0)
					smsTrackDetails = smsTrackRepository.findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(
							doctorObjectId, locationObjectId, hospitalObjectId, type,
							PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					smsTrackDetails = smsTrackRepository.findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(
							doctorObjectId, locationObjectId, hospitalObjectId, type,
							new Sort(Sort.Direction.DESC, "createdTime"));
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
			SMSDeliveryReportsCollection smsDeliveryReportsCollection = new SMSDeliveryReportsCollection();
			for (SMSDeliveryReports smsDeliveryReport : request) {
				// for checking if request is coming from 3rd party
				BeanUtil.map(smsDeliveryReport, smsDeliveryReportsCollection);
				smsDeliveryReportsRepository.save(smsDeliveryReportsCollection);
				//
				SMSTrackDetail smsTrackDetail = smsTrackRepository.findByResponseId(smsDeliveryReport.getRequestId());
				if (smsTrackDetail != null) {
					for (SMSDetail smsDetail : smsTrackDetail.getSmsDetails()) {
						for (SMSReport report : smsDeliveryReport.getReport()) {
							if (smsDetail.getSms() != null && smsDetail.getSms().getSmsAddress() != null
									&& smsDetail.getSms().getSmsAddress().getRecipient() != null) {
								if (smsDetail.getSms().getSmsAddress().getRecipient()
										.equals(report.getNumber().replaceFirst("91", ""))) {
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

			smsFormatCollection = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalIdAndType(doctorObjectId,
					locationObjectId, hospitalObjectId, request.getType().getType());
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
				SMSFormatCollection smsFormatCollection = sMSFormatRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndType(doctorObjectId, locationObjectId,
								hospitalObjectId, type);
				if (smsFormatCollection != null) {
					smsFormatCollections = new ArrayList<SMSFormatCollection>();
					smsFormatCollections.add(smsFormatCollection);
				}
			} else
				smsFormatCollections = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalId(doctorObjectId,
						locationObjectId, hospitalObjectId);
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

	@Override
	public Boolean getOTPSMSResponse(String mobileNumber, String message, String otp) {

		Boolean boolResponse = false;
		// http://dndsms.resellergrow.com/api/otp.php?authkey=93114AV2rXJuxL56001692&mobile=9766914900&message=0808&sender=HTCOCO&otp=0808
		StringBuffer response = new StringBuffer();
		try {
			// String password = new String(loginRequest.getPassword());
			String url = "http://dndsms.resellergrow.com/api/otp.php?authkey=" + AUTH_KEY + "&mobile=" + mobileNumber
					+ "&message=" + URLEncoder.encode(message, "UTF-8") + "&sender=" + SENDER_ID + "&otp=" + otp;
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
			/* response = new StringBuffer(); */

			while ((inputLine = in.readLine()) != null) {

				response.append(inputLine);

			}
			in.close();
			if (responseCode == 200)
				boolResponse = true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}

		return boolResponse;

	}

	@Override
	public Boolean sendPatientOTP(String mobileNumber, String otp) {
		Boolean boolResponse = false;
		// http://dndsms.resellergrow.com/api/otp.php?authkey=93114AV2rXJuxL56001692&mobile=9766914900&message=0808&sender=HTCOCO&otp=0808
		StringBuffer response = new StringBuffer();
		try {
			String url = "https://2factor.in/API/V1/" + tfactorAuthKey + "/SMS/" + mobileNumber + "/" + otp + "/OTP";
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
			if (responseCode == 200)
				boolResponse = true;
		} catch (Exception e) {
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

		String url = "http://dndsms.resellergrow.com/api/sendhttp.php?authkey=" + "93114AV2rXJuxL56001692" + "&mobiles="
				+ "9766914900" + "&message=" + UriUtils.encode(text, "UTF-8") + "&sender=" + "HTCOCO" + "&route=" + "4"
				+ "&country=" + "91" + "&unicode=" + "1";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is POST
		con.setRequestMethod("GET");

		// add request header
		// con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		con.setRequestProperty("Accept-Charset", "UTF-8");

	}

//commenting new service 	
	// new sms service
	@Override
	public String getBulkSMSResponse(List<String> mobileNumbers, String message, String doctorId, String locationId,
			long subCredits, String templateId, String smsRoute) {
		String response = null;
		try {

			message = StringEscapeUtils.unescapeJava(message);
			String type = smsRoute;

			String strUrl = "https://api.kaleyra.io/v1/" + SID + "/messages";

			List<String> numberlist = new ArrayList<String>(mobileNumbers);
			String numberString = StringUtils.join(numberlist, ',');
			// working
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest httprequest = RequestBuilder.post().addParameter("to", numberString)
					.addParameter("type", type).addParameter("body", message).addParameter("sender", SENDER_ID)
					.addParameter("template_id", templateId).setUri(strUrl).setHeader("api-key", KEY).build();
			// System.out.println("response"+client.execute(httprequest));
			org.apache.http.HttpResponse responses = client.execute(httprequest);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			responses.getEntity().writeTo(out);
			String responseString = out.toString();
			System.out.println("responseString" + responseString);
			ObjectMapper mapper = new ObjectMapper();

			MessageResponse list = mapper.readValue(out.toString(), MessageResponse.class);

			MessageCollection collection = new MessageCollection();
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
			response = list.getMessageId();

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
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
				.findByLocationId(smsTrackDetail.getLocationId());
		if (!DPDoctorUtils.isNullOrEmptyList(doctorClinicProfileCollections))
			doctorClinicProfileCollection = doctorClinicProfileCollections.get(0);

		if (doctorClinicProfileCollection != null) {
			List<SubscriptionCollection> subscriptionCollections = subscriptionRepository
					.findByDoctorId(doctorClinicProfileCollection.getDoctorId());
			SubscriptionCollection subscriptionCollection = null;
			if (!DPDoctorUtils.isNullOrEmptyList(subscriptionCollections)) {
				subscriptionCollection = subscriptionCollections.get(0);
				if (subscriptionCollection != null) {
					switch (subscriptionCollection.getPackageName()) {
					case ADVANCE:
					case PRO:
						response = sendSMSToUser(smsTrackDetail, save);
						break;
					case BASIC:
					case STANDARD:
					case FREE:

						DoctorCollection doctorCollection = doctorRepository
								.findByUserId(doctorClinicProfileCollection.getDoctorId());
						if (doctorCollection != null) {
							BulkSmsCredits bulk = doctorCollection.getBulkSmsCredit();
							if (bulk != null && bulk.getCreditBalance() > 0) {
								response = sendSMSToUser(smsTrackDetail, save);
							}
						}
						break;
					}
				}
			}
		}
		return response;
	}

	private Boolean sendSMSToUser(SMSTrackDetail smsTrackDetail, Boolean save) {
		Boolean response = false;
		try {
			String type = "TXN";
			if (smsTrackDetail.getType() != null)
				if (smsTrackDetail.getType().equals("BIRTHDAY WISH TO PATIENT")
						|| smsTrackDetail.getType().equals("BIRTHDAY WISH TO DOCTOR"))
					type = "MKT";

			String message = smsTrackDetail.getSmsDetails().get(0).getSms().getSmsText();
			String mobileNumber = smsTrackDetail.getSmsDetails().get(0).getSms().getSmsAddress().getRecipient();
			String strUrl = "https://api.kaleyra.io/v1/" + SID + "/messages";

			String senderId = null;

			senderId = SENDER_ID;

			ObjectMapper mapper = new ObjectMapper();
			UserMobileNumbers userNumber = null;

			if (!isEnvProduction) {
				FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				userNumber = (UserMobileNumbers) in.readObject();
				in.close();
				fileIn.close();
				System.out.println(userNumber);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			MessageResponse list = null;

			if (smsTrackDetail.getType().equals("BIRTHDAY WISH TO DOCTOR")) {
				DoctorCollection doctorClinicProfileCollection = doctorRepository
						.findByUserId(smsTrackDetail.getDoctorId());

				if (doctorClinicProfileCollection != null
						&& doctorClinicProfileCollection.getIsTransactionalSms() != null
						&& doctorClinicProfileCollection.getIsTransactionalSms()) {

					HttpClient client = HttpClients.custom().build();
					HttpUriRequest httprequest = RequestBuilder.post().addParameter("to", COUNTRY_CODE + mobileNumber)
							.addParameter("type", type).addParameter("body", message).addParameter("sender", SENDER_ID)
							.addParameter("template_id", smsTrackDetail.getTemplateId()).setUri(strUrl)
							.setHeader("api-key", KEY).build();
					System.out.println("senderId" + senderId);
					org.apache.http.HttpResponse responses = client.execute(httprequest);
					responses.getEntity().writeTo(out);
					list = mapper.readValue(out.toString(), MessageResponse.class);
				}
			} else {
				HttpClient client = HttpClients.custom().build();
				HttpUriRequest httprequest = RequestBuilder.post().addParameter("to", COUNTRY_CODE + mobileNumber)
						.addParameter("type", type).addParameter("body", message).addParameter("sender", SENDER_ID)
						// .addParameter("unicode", "1")
						.addParameter("template_id", smsTrackDetail.getTemplateId()).setUri(strUrl)
						.setHeader("api-key", KEY).build();
				System.out.println("httprequest " + httprequest.toString());
				System.out.println("response" + client.execute(httprequest).getStatusLine().getStatusCode());
				org.apache.http.HttpResponse responses = client.execute(httprequest);
				responses.getEntity().writeTo(out);
				list = mapper.readValue(out.toString(), MessageResponse.class);
			}
			SmsParts sms = smsSpitterServices.splitSms(message);
			Integer totalLength = sms.getEncoding().getMaxLengthSinglePart();
			Integer messageLength = message.length();

			long credits = (messageLength / totalLength);

			long temp = messageLength % totalLength;
			if (credits == 0 || temp != 0)
				credits = credits + 1;

			long subCredits = credits * 1;

			if (save) {

				String responseString = out.toString();
				System.out.println("responseString" + responseString);

				MessageCollection collection = new MessageCollection();
				if (list != null) {
					if (!DPDoctorUtils.anyStringEmpty(list.getId()))
						list.setMessageId(list.getId());
					list.setId(null);
					BeanUtil.map(list, collection);
				}
				collection.setDoctorId(smsTrackDetail.getDoctorId());
				collection.setLocationId(smsTrackDetail.getLocationId());
				collection.setHospitalId(smsTrackDetail.getHospitalId());
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				collection.setMessageType(smsTrackDetail.getType());
				collection.setTotalCreditsSpent(subCredits);
				messageRepository.save(collection);
				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						getSmsStatus(collection.getMessageId());
					}
				});
			}

			if (!smsTrackDetail.getType().equals(ComponentType.SMS_CREDIT_BALANCE.getType()))
				updateSmsCredits(list.getBody(), smsTrackDetail.getDoctorId(), smsTrackDetail.getLocationId());
			response = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Async
	public void updateSmsCredits(String message, ObjectId doctorId, ObjectId locationId) {

		SmsParts sms = smsSpitterServices.splitSms(message);
		Integer totalLength = sms.getEncoding().getMaxLengthSinglePart();
		Integer messageLength = message.length();

		long credits = (messageLength / totalLength);

		long temp = messageLength % totalLength;
		if (credits == 0 || temp != 0)
			credits = credits + 1;

		long subCredits = credits * 1;

		DoctorCollection doctorCollection = null;
		BulkSmsHistoryCollection history = null;
		List<BulkSmsHistoryCollection> bulkSmsHistoryCollections = bulkSmsHistoryRepository.findByDoctorId(doctorId);
		if (!DPDoctorUtils.isNullOrEmptyList(bulkSmsHistoryCollections)) {
			history = bulkSmsHistoryCollections.get(0);
			doctorCollection = doctorRepository.findByUserId(doctorId);

			List<SubscriptionCollection> subscriptionCollections = subscriptionRepository
					.findByDoctorIdAndPackageName(doctorCollection.getUserId(), PackageType.STANDARD);
			SubscriptionCollection subscriptionCollection = null;
			if (!DPDoctorUtils.isNullOrEmptyList(subscriptionCollections))
				subscriptionCollection = subscriptionCollections.get(0);

			if (subscriptionCollection != null) {

				BulkSmsCredits bulk = doctorCollection.getBulkSmsCredit();
				if (doctorCollection != null && bulk != null) {

					if (bulk.getCreditBalance() > subCredits || bulk.getCreditBalance() == subCredits) {

						bulk.setCreditBalance(bulk.getCreditBalance() - subCredits);
						bulk.setCreditSpent(bulk.getCreditSpent() + subCredits);
						doctorCollection.setBulkSmsCredit(bulk);
						doctorRepository.save(doctorCollection);
						if (history != null) {
							history.setCreditBalance(subCredits);
							history.setCreditSpent(history.getCreditSpent() + subCredits);
							history.setUpdatedTime(new Date());
							bulkSmsHistoryRepository.save(history);
						}
					}
				}
			}
		} else {
			DoctorClinicProfileCollection doctorClinicProfileCollection = null;
			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByLocationId(locationId);
			if (!DPDoctorUtils.isNullOrEmptyList(doctorClinicProfileCollections))
				doctorClinicProfileCollection = doctorClinicProfileCollections.get(0);

			if (doctorClinicProfileCollection != null) {
				DoctorCollection doctorCollection1 = null;
				BulkSmsHistoryCollection history1 = null;
				List<BulkSmsHistoryCollection> bulkSmsHistoryCollections1 = bulkSmsHistoryRepository
						.findByDoctorId(doctorClinicProfileCollection.getDoctorId());
				if (!DPDoctorUtils.isNullOrEmptyList(bulkSmsHistoryCollections1)) {
					history1 = bulkSmsHistoryCollections1.get(0);
					doctorCollection1 = doctorRepository.findByUserId(doctorClinicProfileCollection.getDoctorId());

					List<SubscriptionCollection> subscriptionCollections = subscriptionRepository
							.findByDoctorIdAndPackageName(doctorCollection1.getUserId(), PackageType.STANDARD);
					SubscriptionCollection subscriptionCollection = null;
					if (!DPDoctorUtils.isNullOrEmptyList(subscriptionCollections))
						subscriptionCollection = subscriptionCollections.get(0);

					if (subscriptionCollection != null) {

						BulkSmsCredits bulk = doctorCollection1.getBulkSmsCredit();
						if (doctorCollection1 != null && bulk != null) {

							if (bulk.getCreditBalance() > subCredits || bulk.getCreditBalance() == subCredits) {

								bulk.setCreditBalance(bulk.getCreditBalance() - subCredits);
								bulk.setCreditSpent(bulk.getCreditSpent() + subCredits);
								doctorCollection1.setBulkSmsCredit(bulk);
								doctorRepository.save(doctorCollection1);
								if (history1 != null) {
									history1.setCreditBalance(subCredits);
									history1.setCreditSpent(history1.getCreditSpent() + subCredits);
									history1.setUpdatedTime(new Date());
									bulkSmsHistoryRepository.save(history1);
								}
							}
						}
					}
				}
			}
		}
	}

	@Scheduled(cron = "0 0 6 * * ?", zone = "IST")
	@Override
	public void sendSmsCreditsAlerts() {
		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorCollectionLookupResponse> results = mongoTemplate.aggregate(aggregation,
					DoctorCollection.class, DoctorCollectionLookupResponse.class);

			List<DoctorCollectionLookupResponse> doctorCollections = results.getMappedResults();
			if (doctorCollections.size() > 0)
				for (DoctorCollectionLookupResponse doctorCollection : doctorCollections) {
					List<SubscriptionCollection> subscriptionCollections = subscriptionRepository
							.findByDoctorIdAndPackageName(new ObjectId(doctorCollection.getUserId()),
									PackageType.STANDARD);
					SubscriptionCollection subscriptionCollection = null;
					if (!DPDoctorUtils.isNullOrEmptyList(subscriptionCollections))
						subscriptionCollection = subscriptionCollections.get(0);
					if (subscriptionCollection != null) {
						UserCollection userCollection = userRepository
								.findById(new ObjectId(doctorCollection.getUserId())).orElse(null);
						final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();

						if (subscriptionCollection.getPackageName() == PackageType.STANDARD) {
							BulkSmsCredits bulk = doctorCollection.getBulkSmsCredit();
							String doctorId = doctorCollection.getId().toString();
							String emailAddress = userCollection.getEmailAddress();
							String mobileNumber = userCollection.getMobileNumber();
							if (doctorCollections != null && bulk != null) {
								switch (bulk.getCreditBalance().intValue()) {
								case 0:
									List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
											.findByDoctorId(new ObjectId(doctorCollection.getUserId()));
									DoctorClinicProfileCollection doctorClinicProfileCollection = null;
									doctorClinicProfileCollection = doctorClinicProfileCollections.get(0);
									if (doctorClinicProfileCollections != null
											&& !doctorClinicProfileCollections.isEmpty()) {
										LocationCollection locationCollection = locationRepository
												.findById(doctorClinicProfileCollection.getLocationId()).orElse(null);

										if (locationCollection != null) {
											locationCollection.setSmsAccountActive(false);
											locationRepository.save(locationCollection);
										}
									}
									sendtEmailSmsNotification(0, doctorId, doctorName, emailAddress, mobileNumber);
									break;
								case 50:
									sendtEmailSmsNotification(50, doctorId, doctorName, emailAddress, mobileNumber);
									break;
								case 100:
									sendtEmailSmsNotification(100, doctorId, doctorName, emailAddress, mobileNumber);
									break;
								}
							}
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While sendSmsCreditsAlerts");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While sendSmsCreditsAlerts");
		}
	}

	private void sendtEmailSmsNotification(int smsCreditCount, String doctorId, String doctorName, String emailAddress,
			String mobileNumber) throws MessagingException {
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(new ObjectId(doctorId));
		smsTrackDetail.setType(ComponentType.SMS_CREDIT_BALANCE.getType());
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(new ObjectId(doctorId));
		SMS sms = new SMS();
		sms.setSmsText("Alert!You have " + smsCreditCount
				+ " SMS credits remaining in your account. Recharge your account.-Healthcoco");
		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(mobileNumber);
		sms.setSmsAddress(smsAddress);
		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		smsTrackDetail.setTemplateId("1307170133250574920");
		sendSMS(smsTrackDetail, true);
		System.out.println("sms: " + sms.getSmsAddress().getRecipient() + sms.getSmsText());
		String emailBody1 = "";

		String body = mailBodyGenerator.smsCreditBalanceEmailBody(emailBody1, doctorName, smsCreditCount,
				"smsCreditBalanceToDoctor.vm");

		mailService.sendEmail("shreshtha.solanki@healthcoco.com", "Healthcoco SMS Credit Balance", body, null);

		pushNotificationServices.notifyUser(doctorId,
				"Alert!You have " + smsCreditCount + " SMS credits remaining in your account. Recharge your account.",
				ComponentType.SMS_CREDIT_BALANCE.getType(), null, null);
	}

	@Scheduled(cron = "0 0 2 * * ?", zone = "IST")
	@Override
	public void updateSmsStatus() {
		try {

			Date todayDate = new Date(); // today's time
			ZoneId defaultZoneId = ZoneId.systemDefault();
			// Converting the date to Instant
			Instant instant = todayDate.toInstant();
			Instant yesterday = instant.minus(1, ChronoUnit.DAYS);
			// Converting the Date to LocalDate
			LocalDate localDate = yesterday.atZone(defaultZoneId).toLocalDate();
			LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);// set time 00:00
			LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);// set time 23:59

			System.out.println("---Scheduler Started---");
			String emailBody = "";
			String body = mailBodyGenerator.messageStatusUpdateEmailBody(emailBody, "confirmappointment.vm");
			mailService.sendEmail("shreshtha.solanki@healthcoco.com", "Message Status Update Scheduler Started", body,
					null);
			LocalDateTime startOfDay1 = startOfDay.plusHours(1);
			LocalDateTime gte = startOfDay;
			while (startOfDay1.isBefore(endOfDay)) {

				LocalDateTime lte = startOfDay1;
				Criteria criteria = new Criteria("isSync").is(false);
				criteria.and("status").is("SENT - No DLR");
				criteria.and("createdTime").gte(gte).lte(lte);
				CustomAggregationOperation project = new CustomAggregationOperation(
						new Document("$project", new BasicDBObject("messageId", "$messageId")));

				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), project,
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
				AggregationResults<MessageIdLookupResponse> results = mongoTemplate.aggregate(aggregation,
						MessageCollection.class, MessageIdLookupResponse.class);
				List<MessageIdLookupResponse> messageCollections = results.getMappedResults();
				if (messageCollections.size() > 0)
					for (MessageIdLookupResponse messageIdLookupResponse : messageCollections) {
						MessageStatus response = null;
						String url = null;

						url = "https://api.kaleyra.io/v1/" + SID + "/messages/status?message_ids="
								+ messageIdLookupResponse.getMessageId();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						String numberString = StringUtils.join(messageIdLookupResponse.getMessageId(), ',');
						HttpClient client = HttpClients.custom().build();
						HttpUriRequest httprequest = RequestBuilder.get().addParameter("message_ids", numberString)

								.setUri(url).setHeader("api-key", KEY).build();
						org.apache.http.HttpResponse responses = client.execute(httprequest);
						responses.getEntity().writeTo(out);
						ObjectMapper mapper = new ObjectMapper();
						response = mapper.readValue(out.toString(), MessageStatus.class);
						MessageCollection messageCollection = messageRepository
								.findByMessageId(messageIdLookupResponse.getMessageId());
						messageCollection.setIsSync(true);
						messageCollection.setStatus(response.getData().get(0).getStatus());
						messageRepository.save(messageCollection);
					}
				gte = startOfDay1;
				startOfDay1 = startOfDay1.plusHours(1);
			}
			System.out.println("---Scheduler Ended---");
			String emailBody1 = "";
			String body1 = mailBodyGenerator.messageStatusUpdateEmailBody(emailBody1, "confirmappointment.vm");
			mailService.sendEmail("shreshtha.solanki@healthcoco.com", "Message Status Update Scheduler Ended", body1,
					null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While updateSmsStatus");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While updateSmsStatus");
		}
	}

	@Async
	@Override
	public MessageStatus getSmsStatus(String messageId) {

		MessageStatus response = null;
		try {
			List<String> messageStatus = new ArrayList<String>();

			messageStatus.add(messageId);
			String url = null;

			url = "https://api.kaleyra.io/v1/" + SID + "/messages/status?message_ids=" + messageStatus;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			String numberString = StringUtils.join(messageStatus, ',');
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest httprequest = RequestBuilder.get().addParameter("message_ids", numberString)

					.setUri(url).setHeader("api-key", KEY).build();
			org.apache.http.HttpResponse responses = client.execute(httprequest);
			responses.getEntity().writeTo(out);
			ObjectMapper mapper = new ObjectMapper();
			response = mapper.readValue(out.toString(), MessageStatus.class);

			MessageCollection collection = messageRepository.findByMessageId(messageId);
			collection.setStatus(response.getData().get(0).getStatus());
			messageRepository.save(collection);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Sms status");
		}
		return response;

	}

	@Override
	public Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, Boolean save) {
		Boolean response = false;
		try {

			String type = "OTP";
			String message = smsTrackDetail.getSmsDetails().get(0).getSms().getSmsText();
			String mobileNumber = smsTrackDetail.getSmsDetails().get(0).getSms().getSmsAddress().getRecipient();
			String strUrl = "https://api.kaleyra.io/v1/" + SID + "/messages";

			String senderId = null;

			senderId = SENDER_ID;

			ObjectMapper mapper = new ObjectMapper();
			Boolean isSMSInAccount = true;
			UserMobileNumbers userNumber = null;

			if (!isEnvProduction) {
				FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				userNumber = (UserMobileNumbers) in.readObject();
				in.close();
				fileIn.close();
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			MessageResponse list = null;
			if (isSMSInAccount) {

				HttpClient client = HttpClients.custom().build();
				HttpUriRequest httprequest = RequestBuilder.post().addParameter("to", COUNTRY_CODE + mobileNumber)
						.addParameter("type", type).addParameter("body", message).addParameter("sender", SENDER_ID)
						.addParameter("template_id", smsTrackDetail.getTemplateId()).setUri(strUrl)
						.setHeader("api-key", KEY).build();
				System.out.println("senderId" + senderId);
				org.apache.http.HttpResponse responses = client.execute(httprequest);
				responses.getEntity().writeTo(out);
				list = mapper.readValue(out.toString(), MessageResponse.class);
				response = true;
			}

			if (save) {
				String responseString = out.toString();
				System.out.println("responseString" + responseString);

				MessageCollection collection = new MessageCollection();
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

		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while sending Sms");
		}
		return response;
	}

	@Override
	public Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, String otp, Boolean save) {
		return null;
	}

	@Override
	public Boolean sendDentalChainSMS(SMSTrackDetail smsTrackDetail, boolean save) {
		Boolean response = false;
		String responseId = null;
		try {
			String type = "TXN";
			if (smsTrackDetail.getType() != null)
				if (smsTrackDetail.getType().equals("BIRTHDAY WISH TO PATIENT"))
					type = "MKT";
			String message = smsTrackDetail.getSmsDetails().get(0).getSms().getSmsText();
			String mobileNumber = smsTrackDetail.getSmsDetails().get(0).getSms().getSmsAddress().getRecipient();
			String strUrl = "https://api.kaleyra.io/v1/" + SID + "/messages";

			String senderId = SMILEBIRD_SENDER_ID;

			ObjectMapper mapper = new ObjectMapper();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			MessageResponse list = null;

			HttpClient client = HttpClients.custom().build();
			HttpUriRequest httprequest = RequestBuilder.post().addParameter("to", COUNTRY_CODE + mobileNumber)
					.addParameter("type", type).addParameter("body", message)
					.addParameter("sender", SMILEBIRD_SENDER_ID)
					.addParameter("template_id", smsTrackDetail.getTemplateId()).setUri(strUrl)
					.setHeader("api-key", KEY).build();
			System.out.println("senderId" + senderId);
			org.apache.http.HttpResponse responses = client.execute(httprequest);
			responses.getEntity().writeTo(out);
			list = mapper.readValue(out.toString(), MessageResponse.class);

			if (save) {
				String responseString = out.toString();
				System.out.println("responseString" + responseString);

				MessageCollection collection = new MessageCollection();
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
			if (!DPDoctorUtils.anyStringEmpty(responseId))
				response = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

}
