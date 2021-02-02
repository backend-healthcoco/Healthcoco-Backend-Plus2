package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
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
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
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



	@Scheduled(cron = "0 0 9 * * ?", zone = "IST")
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
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),Aggregation.unwind("location"),
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
								.findById(new ObjectId(birthdaySMSDetailsForPatient.getPatient().getUserId())).orElse(null);
						String message = birthdayWishSMStoPatient;
						SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
						String templateId="1307161191522457378";
						smsTrackDetail.setTemplateId(templateId);
						smsTrackDetail.setDoctorId(birthdaySMSDetailsForPatient.getDoctorId());
						smsTrackDetail.setLocationId(birthdaySMSDetailsForPatient.getLocationId());
						smsTrackDetail.setHospitalId(birthdaySMSDetailsForPatient.getHospitalId());
						smsTrackDetail.setType("BIRTHDAY WISH TO PATIENT");
						SMSDetail smsDetail = new SMSDetail();
						smsDetail.setUserId(userCollection.getId());
						SMS sms = new SMS();
						smsDetail.setUserName(birthdaySMSDetailsForPatient.getLocalPatientName());
						
						sms.setSmsText(message.replace("{patientName}", birthdaySMSDetailsForPatient.getLocalPatientName())
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

		} catch (

		Exception e) {
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
					String templateId="1307161191432703411";
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
					smsTrackDetail.setTemplateId(templateId);
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
