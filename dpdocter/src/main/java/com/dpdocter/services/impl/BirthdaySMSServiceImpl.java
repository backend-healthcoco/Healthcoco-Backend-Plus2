package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.BirthdaySMSServices;
import com.dpdocter.services.SMSServices;

@Service
@Transactional
public class BirthdaySMSServiceImpl implements BirthdaySMSServices {
	private static Logger logger = Logger.getLogger(BirthdaySMSServiceImpl.class.getName());

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSServices sMSServices;

	@Scheduled(cron = "0 0 9 * * ?")
	@Override
	public void sendBirthdaySMSToPatients() {
		try {
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("userLocationId", "$id"),
					Fields.field("doctorId", "$userId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$location.hospitalId"),
					Fields.field("locationName", "$location.locationName"),
					Fields.field("createdTime", "$createdTime")));
			Criteria criteria = new Criteria("discarded").is(false).and("doctorClinic.isSendBirthdaySMS").is(true);
			criteria = criteria.and("isActivate").is(true);
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("doctor_clinic_profile_cl", "_id", "userLocationId", "doctorClinic"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.match(criteria),
					projectList, Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<BirthdaySMSDetailsForPatients> results = mongoTemplate.aggregate(aggregation,
					UserLocationCollection.class, BirthdaySMSDetailsForPatients.class);

			List<BirthdaySMSDetailsForPatients> birthdaySMSDetailsForPatientsList = results.getMappedResults();

			if (birthdaySMSDetailsForPatientsList.size() > 0)
				for (BirthdaySMSDetailsForPatients birthdaySMSDetailsForPatients : birthdaySMSDetailsForPatientsList) {

					List<PatientCollection> patientCollections = patientRepository
							.findByDoctorIdLocationIdAndHospitalId(birthdaySMSDetailsForPatients.getDoctorId(),
									birthdaySMSDetailsForPatients.getLocationId(),
									birthdaySMSDetailsForPatients.getHospitalId());
					if (patientCollections.size() > 0)
						for (PatientCollection patientCollection : patientCollections) {
							UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
							String message = patientCollection.getFirstName() + ","
									+ birthdaySMSDetailsForPatients.getLocationName()
									+ " wishes you a very Healthy and Happy Birthday. Have a great year ahead.";
							SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
							smsTrackDetail.setDoctorId(birthdaySMSDetailsForPatients.getDoctorId());
							smsTrackDetail.setLocationId(birthdaySMSDetailsForPatients.getLocationId());
							smsTrackDetail.setHospitalId(birthdaySMSDetailsForPatients.getHospitalId());
							smsTrackDetail.setType("Birthday Wish for patients");
							SMSDetail smsDetail = new SMSDetail();
							smsDetail.setUserId(patientCollection.getUserId());
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

				}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Sending Birthday SMS to patients");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending Birthday SMS to patients");

		}

	}

	@Scheduled(cron = "0 0 9 * * ?")
	@Override
	public void sendBirthdaySMSToDoctors() {
		try {
			Criteria criteria = new Criteria("userLocation.discarded").is(false);
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("user_location_cl", "_id", "userId", "userLocation"),
					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation, UserCollection.class,
					UserCollection.class);
			List<UserCollection> userCollections = results.getMappedResults();

			if (userCollections.size() > 0)
				for (UserCollection userCollection : userCollections) {

					String message = "Healthcoco wishes you a very Healthy and Happy Birthday.Have a great year ahead.";
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
					smsTrackDetail.setType("Birthday Wish for Doctors");
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
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending Birthday SMS to Doctor");

		}

	}

}
