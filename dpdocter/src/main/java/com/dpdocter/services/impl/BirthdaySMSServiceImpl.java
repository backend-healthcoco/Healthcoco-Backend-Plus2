package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.BirthdaySMSDetailsForPatients;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.InteraktResponse;
import com.dpdocter.services.BirthdaySMSServices;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;

@Service
@Transactional
public class BirthdaySMSServiceImpl implements BirthdaySMSServices {
	private static Logger logger = Logger.getLogger(BirthdaySMSServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private MailService mailService;

	@Value(value = "${sms.birthday.wish.to.doctor}")
	private String birthdayWishSMStoDoctor;

	@Value(value = "${sms.birthday.wish.to.patient}")
	private String birthdayWishSMStoPatient;

	@Autowired
	private LocationRepository locationRepository;
	
	@Value(value = "${interakt.secret.key}")
	private String secretKey;


	@Scheduled(cron = "0 15 10 * * ?", zone = "IST")
	@Override
	public void sendBirthdaySMSToPatients() {
		try {
			Date date = new Date(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(
					Fields.field("doctorId", "$patient.doctorId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$location.hospitalId"),
					Fields.field("locationName", "$location.locationName"), Fields.field("createdTime", "$createdTime"),
					Fields.field("patient", "$patient"),
					Fields.field("localPatientName", "$patient.localPatientName")));
			Criteria criteria = new Criteria("discarded").is(false).andOperator(
					new Criteria("isSendBirthdaySMS").is(true), new Criteria("isActivate").is(true),
					new Criteria("patient.dob.days").is(day), new Criteria("patient.dob.months").is(month),
					new Criteria("patient.discarded").is(false), new Criteria("doctorId").ne(null),
					new Criteria("locationId").ne(null));

			Aggregation aggregation = Aggregation.newAggregation(
					// Aggregation.lookup("doctor_clinic_profile_cl", "_id",
					// "userLocationId", "doctorClinic"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("patient_cl", "doctorId", "doctorId", "patient"), Aggregation.unwind("patient"),
					Aggregation.match(criteria), projectList, Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<BirthdaySMSDetailsForPatients> results = mongoTemplate.aggregate(aggregation,
					DoctorClinicProfileCollection.class, BirthdaySMSDetailsForPatients.class);

			List<BirthdaySMSDetailsForPatients> birthdaySMSDetailsForPatientsList = results.getMappedResults();

			if (birthdaySMSDetailsForPatientsList.size() > 0)
				for (BirthdaySMSDetailsForPatients birthdaySMSDetailsForPatient : birthdaySMSDetailsForPatientsList) {
					if (birthdaySMSDetailsForPatient.getLocationId().toString()
							.equals(birthdaySMSDetailsForPatient.getPatient().getLocationId())) {

						UserCollection userCollection = userRepository
								.findById(new ObjectId(birthdaySMSDetailsForPatient.getPatient().getUserId()))
								.orElse(null);

						LocationCollection locationCollection = locationRepository
								.findById(new ObjectId(birthdaySMSDetailsForPatient.getPatient().getLocationId()))
								.orElse(null);
						if (locationCollection.getIsDentalChain()) {
							SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
							smsTrackDetail.setDoctorId(birthdaySMSDetailsForPatient.getDoctorId());
							smsTrackDetail.setLocationId(birthdaySMSDetailsForPatient.getLocationId());
							smsTrackDetail.setHospitalId(birthdaySMSDetailsForPatient.getHospitalId());
							smsTrackDetail.setType("BIRTHDAY WISH TO PATIENT");
							smsTrackDetail.setTemplateId("1307165106647920437");

							SMSDetail smsDetail = new SMSDetail();
							smsDetail.setUserId(userCollection.getId());
							SMS sms = new SMS();
							smsDetail.setUserName(birthdaySMSDetailsForPatient.getLocalPatientName());
							String text = "Hi " + birthdaySMSDetailsForPatient.getLocalPatientName() + ","
									+ "Smilebird wishes you a very Healthy and Happy Birthday. Stay Smiling!" + "\n"
									+ "Team Smilebird";
							sms.setSmsText(text);

							SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(userCollection.getMobileNumber());
							sms.setSmsAddress(smsAddress);

							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);
							sMSServices.sendDentalChainSMS(smsTrackDetail, true);

							//
							sendWhatsappMsg(birthdaySMSDetailsForPatient.getLocalPatientName(),
									userCollection.getMobileNumber());
						} else {
							String message = birthdayWishSMStoPatient;
							SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
							smsTrackDetail.setTemplateId("1307162676825087554");
							smsTrackDetail.setDoctorId(birthdaySMSDetailsForPatient.getDoctorId());
							smsTrackDetail.setLocationId(birthdaySMSDetailsForPatient.getLocationId());
							smsTrackDetail.setHospitalId(birthdaySMSDetailsForPatient.getHospitalId());
							smsTrackDetail.setType("BIRTHDAY WISH TO PATIENT");
							SMSDetail smsDetail = new SMSDetail();
							smsDetail.setUserId(userCollection.getId());
							SMS sms = new SMS();
							smsDetail.setUserName(birthdaySMSDetailsForPatient.getLocalPatientName());

							sms.setSmsText(
									message.replace("{patientName}", birthdaySMSDetailsForPatient.getLocalPatientName())
											.replace("{clinicName}", birthdaySMSDetailsForPatient.getLocationName()));

							SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(userCollection.getMobileNumber());
							sms.setSmsAddress(smsAddress);

							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);

							sMSServices.sendSMS(smsTrackDetail, true);

						}
					}
				}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Sending Birthday SMS to patients");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While Sending Birthday SMS to patients",
						e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending Birthday SMS to patients");
		}
	}

	private void sendWhatsappMsg(String localPatientName, String mobileNumber) {
		try {
			JSONObject requestObject1 = new JSONObject();
			JSONObject requestObject2 = new JSONObject();
			JSONArray requestObject3 = new JSONArray();
			requestObject1.put("phoneNumber", mobileNumber);
			requestObject1.put("countryCode", "+91");
			requestObject1.put("type", "Template");

			requestObject2.put("name", "smilebird_birthday_wish_for_clinic_patient");
			requestObject2.put("languageCode", "en");
			requestObject3.put(localPatientName);
			requestObject2.put("bodyValues", requestObject3);
			requestObject1.put("template", requestObject2);
			InputStream is = null;
			URL url = new URL("https://api.interakt.ai/v1/public/message/");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Basic " + secretKey);
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			System.out.println(requestObject1);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(requestObject1.toString());
			wr.close();

			// Get Response

			try {
				is = connection.getInputStream();
			} catch (IOException ioe) {
				if (connection instanceof HttpURLConnection) {
					HttpURLConnection httpConn = (HttpURLConnection) connection;
					int statusCode = httpConn.getResponseCode();
					if (statusCode != 200) {
						is = httpConn.getErrorStream();
					}
				}
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			System.out.println("http response" + response.toString());

			ObjectMapper mapper = new ObjectMapper();

			InteraktResponse interaktResponse = mapper.readValue(response.toString(), InteraktResponse.class);
			if (!interaktResponse.getResult()) {
				logger.warn("Error while sending message :" + interaktResponse.getMessage());
				throw new BusinessException(ServiceError.Unknown,
						"Error while sending message:" + interaktResponse.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();

		}
	}

	@Scheduled(cron = "0 30 12 * * ?", zone = "IST")
	@Override
	public void sendBirthdaySMSToDoctors() {
		try {
			Date date = new Date(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			Criteria criteria = new Criteria("userLocation.discarded").is(false).andOperator(
					(new Criteria("docter.dob.days").is(day)), new Criteria("docter.dob.months").is(month));
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("doctor_clinic_profile_cl", "_id", "doctorId", "userLocation"),
					Aggregation.lookup("docter_cl", "_id", "userId", "docter"), Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation, UserCollection.class,
					UserCollection.class);
			List<UserCollection> userCollections = results.getMappedResults();

			if (userCollections.size() > 0)
				for (UserCollection userCollection : userCollections) {

					String message = birthdayWishSMStoDoctor;
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
					smsTrackDetail.setType("BIRTHDAY WISH TO DOCTOR");
					SMSDetail smsDetail = new SMSDetail();
					smsDetail.setUserId(userCollection.getId());
					SMS sms = new SMS();
					smsDetail.setUserName(userCollection.getFirstName());
					sms.setSmsText(message);

					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(userCollection.getMobileNumber());
					sms.setSmsAddress(smsAddress);

					smsDetail.setSms(sms);
					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
					smsDetails.add(smsDetail);
					smsTrackDetail.setSmsDetails(smsDetails);
					smsTrackDetail.setTemplateId("1307161191432703411");
					sMSServices.sendSMS(smsTrackDetail, true);

				}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Sending Birthday SMS to Doctor");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While Sending Birthday SMS to Doctor",
						e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending Birthday SMS to Doctor");

		}

	}

}
