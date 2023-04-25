package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.PatientCard;
import com.dpdocter.collections.DoctorOTPCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.OTPState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.DoctorOTPRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;
import common.util.web.LoginUtils;

@Service
public class OTPServiceImpl implements OTPService {

	private static Logger logger = Logger.getLogger(OTPServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private OTPRepository otpRepository;

	@Autowired
	private DoctorOTPRepository doctorOTPRepository;

	@Value(value = "${otp.validation.time.difference.in.mins}")
	private String otpTimeDifference;

	@Value(value = "${otp.non.verified.time.difference.in.mins}")
	private String otpNonVerifiedTimeDifference;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MailService mailService;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${mail.recordsShareOtpBeforeVerification.subject}")
	private String recordsShareOtpBeforeVerification;

	@Value(value = "${mail.recordsShareOtpAfterVerification.subject}")
	private String recordsShareOtpAfterVerification;

	@Override
	@Transactional
	public String otpGenerator(String doctorId, String locationId, String hospitalId, String patientId) {
		String OTP = null;
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

			OTP = LoginUtils.generateOTP();
			UserCollection userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
			// UserCollection patient = userRepository.findById(patientObjectId);
			// PatientCollection patientCollection =
			// patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(patientObjectId,
			// doctorObjectId, locationObjectId, hospitalObjectId);
			PatientCard patientCard = null;

			List<PatientCard> patientCards = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(
									new Criteria("userId").is(patientObjectId).and("locationId").is(locationObjectId)
											.and("hospitalId").is(hospitalObjectId).and("doctorId").is(doctorObjectId)),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
					PatientCollection.class, PatientCard.class).getMappedResults();
			if (patientCards != null && !patientCards.isEmpty())
				patientCard = patientCards.get(0);
			if (userCollection != null && patientCard != null) {

				if (DPDoctorUtils.anyStringEmpty(patientCard.getUser().getMobileNumber())) {
					logger.error("Patient Mobile Number is not available");
					throw new BusinessException(ServiceError.InvalidInput, "Patient Mobile Number is not available");
				}
				String doctorName = (userCollection.getTitle() != null ? userCollection.getTitle() : "") + " "
						+ userCollection.getFirstName();

				OTPCollection otpCollection = new OTPCollection();
				otpCollection.setCreatedTime(new Date());
				otpCollection.setOtpNumber(OTP);

				if (userCollection != null)
					otpCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				otpCollection.setGeneratorId(doctorId);
				otpCollection = otpRepository.save(otpCollection);

				DoctorOTPCollection doctorOTPCollection = new DoctorOTPCollection();
				doctorOTPCollection.setCreatedTime(new Date());
				doctorOTPCollection.setOtpId(otpCollection.getId());
				doctorOTPCollection.setDoctorId(doctorObjectId);
				doctorOTPCollection.setLocationId(locationObjectId);
				doctorOTPCollection.setPatientId(new ObjectId(patientId));
				doctorOTPCollection = doctorOTPRepository.save(doctorOTPCollection);

				SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(doctorId, locationId, hospitalId,
						patientId, patientCard.getLocalPatientName(),
						"OTP to share Healthcoco records with " + doctorName + " is " + OTP
								+ ". Pls don't share this with anyone. Stay Healthy and Happy!!",
						patientCard.getUser().getMobileNumber(), "OTPVerification");

				sMSServices.sendSMS(smsTrackDetail, false);

				if (patientCard != null && patientCard.getEmailAddress() != null
						&& !patientCard.getEmailAddress().isEmpty()) {
					String body = mailBodyGenerator.generateRecordsShareOtpBeforeVerificationEmailBody(
							patientCard.getEmailAddress(), patientCard.getLocalPatientName(), doctorName);
					mailService.sendEmail(patientCard.getEmailAddress(), recordsShareOtpBeforeVerification, body, null);
				}
				pushNotificationServices.notifyUser(patientCard.getId().toString(), userCollection.getTitle() + " "
						+ userCollection.getFirstName()
						+ " has requested to view your medical history, share OTP that was sent to your registered mobile number to provide access",
						null, null, null);
			} else {
				logger.error("Invalid doctorId or patientId");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid doctorId or patientId");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Generating OTP");
			throw new BusinessException(ServiceError.Unknown, "Error While Generating OTP");
		}

		return OTP;
	}

	@Override
	@Transactional
	public Boolean verifyOTP(String doctorId, String locationId, String hospitalId, String patientId,
			String otpNumber) {
		Boolean response = false;
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

			UserCollection userCollection = userRepository.findById(doctorObjectId).orElse(null);
			// UserCollection patient = userRepository.findOne(patientObjectId);

			// PatientCollection patientCollection =
			// patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(patientObjectId,
			// doctorObjectId, locationObjectId, hospitalObjectId);
			PatientCard patientCard = null;
			List<PatientCard> patientCards = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(
									new Criteria("userId").is(patientObjectId).and("locationId").is(locationObjectId)
											.and("hospitalId").is(hospitalObjectId).and("doctorId").is(doctorObjectId)),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
					PatientCollection.class, PatientCard.class).getMappedResults();
			if (patientCards != null && !patientCards.isEmpty())
				patientCard = patientCards.get(0);
			if (userCollection != null && patientCard != null && patientId != null) {
				String doctorName = (userCollection.getTitle() != null ? userCollection.getTitle() : "") + " "
						+ userCollection.getFirstName();
				List<DoctorOTPCollection> doctorOTPCollection = doctorOTPRepository
						.findByDoctorIdAndLocationIdAndPatientId(doctorObjectId, locationObjectId, patientObjectId,
								PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "createdTime")));
				if (doctorOTPCollection != null) {
					OTPCollection otpCollection = otpRepository.findById(doctorOTPCollection.get(0).getOtpId())
							.orElse(null);
					if (otpCollection != null) {
						if (otpCollection.getOtpNumber().equals(otpNumber)) {
							if (isOTPValid(otpCollection.getCreatedTime())) {
								otpCollection.setState(OTPState.VERIFIED);
								otpCollection = otpRepository.save(otpCollection);
								response = true;
								if (patientCard != null && patientCard.getEmailAddress() != null
										&& !patientCard.getEmailAddress().isEmpty()) {
									String body = mailBodyGenerator.generateRecordsShareOtpAfterVerificationEmailBody(
											patientCard.getEmailAddress(), patientCard.getLocalPatientName(),
											doctorName);
									mailService.sendEmail(
											patientCard.getEmailAddress(), recordsShareOtpAfterVerification + " "
													+ userCollection.getTitle() + " " + userCollection.getFirstName(),
											body, null);
								}
								pushNotificationServices.notifyUser(patientCard.getId().toString(), userCollection
										.getTitle() + " " + userCollection.getFirstName()
										+ " can now access your medical history, Tap to know more about Healthcoco share.",
										null, null, null);
							} else {
								logger.error("OTP is expired");
								throw new BusinessException(ServiceError.NotFound, "OTP is expired");
							}
						} else {
							logger.error("Incorrect OTP");
							throw new BusinessException(ServiceError.NotFound, "Incorrect OTP");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Verifying OTP");
			throw new BusinessException(ServiceError.Unknown, "Error While Verifying OTP");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean checkOTPVerified(String doctorId, String locationId, String hospitalId, String patientId) {
		Boolean response = false;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			List<DoctorOTPCollection> doctorOTPCollection = doctorOTPRepository.findByDoctorIdAndLocationIdAndPatientId(
					doctorObjectId, locationObjectId, patientObjectId,
					PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "createdTime")));
			if (doctorOTPCollection != null && !doctorOTPCollection.isEmpty() && doctorOTPCollection.size() > 0) {
				OTPCollection otpCollection = otpRepository.findById(doctorOTPCollection.get(0).getOtpId())
						.orElse(null);
				if (otpCollection != null && otpCollection.getState().equals(OTPState.VERIFIED)) {
					if (isOTPValid(otpCollection.getCreatedTime()))
						response = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While checking OTP");
			throw new BusinessException(ServiceError.Unknown, "Error While checking OTP");
		}
		return response;
	}

	@Override
	@Transactional
	public boolean isOTPValid(Date createdTime) {
		return Minutes.minutesBetween(new DateTime(createdTime), new DateTime())
				.isLessThan(Minutes.minutes(Integer.parseInt(otpTimeDifference)));
	}

	@Override
	@Transactional
	public boolean isNonVerifiedOTPValid(Date createdTime) {
		return Minutes.minutesBetween(new DateTime(createdTime), new DateTime())
				.isLessThan(Minutes.minutes(Integer.parseInt(otpNonVerifiedTimeDifference)));
	}

	@Override
	@Transactional
	public Boolean otpGenerator(String mobileNumber, Boolean isPatientOTP) {
		Boolean response = false;
		String OTP = null;
		try {

			OTP = LoginUtils.generateOTP();
			SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null, OTP
					+ " is your Healthcoco OTP. Code is valid for 30 minutes only, one time use. Stay Healthy and Happy!",
					mobileNumber, "OTPVerification");
			if (!isPatientOTP)
				response = sMSServices.sendOTPSMS(smsTrackDetail, OTP, false);
			else
				response = sMSServices.sendOTPSMS(smsTrackDetail, OTP, false);

			OTPCollection otpCollection = new OTPCollection();
			otpCollection.setCreatedTime(new Date());
			otpCollection.setOtpNumber(OTP);
			otpCollection.setGeneratorId(mobileNumber);
			otpCollection.setMobileNumber(mobileNumber);
			otpCollection.setCreatedBy(mobileNumber);
			otpCollection = otpRepository.save(otpCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Generating OTP");
			throw new BusinessException(ServiceError.Unknown, "Error While Generating OTP " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public boolean verifyOTP(String mobileNumber, String otpNumber) {
		Boolean response = false;
		try {
			OTPCollection otpCollection = otpRepository.findByMobileNumberAndOtpNumberAndGeneratorId(mobileNumber,
					otpNumber, mobileNumber);
			if (otpCollection != null) {
				if (isOTPValid(otpCollection.getCreatedTime())) {
					otpCollection.setState(OTPState.VERIFIED);
					otpCollection = otpRepository.save(otpCollection);
					response = true;
				} else {
					logger.error("OTP is expired");
					throw new BusinessException(ServiceError.NotFound, "OTP is expired");
				}

			} else {
				logger.error("Incorrect OTP");
				throw new BusinessException(ServiceError.NotFound, "Incorrect OTP");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Verifying OTP");
			throw new BusinessException(ServiceError.Unknown, "Error While Verifying OTP");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean checkOTPVerifiedForPatient(String mobileNumber, String otpNumber) {
		Boolean response = false;
		try {
			OTPCollection otpCollection = otpRepository.findByMobileNumberAndOtpNumberAndGeneratorId(mobileNumber,
					otpNumber, mobileNumber);
			if (otpCollection != null && otpCollection.getState().equals(OTPState.VERIFIED))
				response = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While checking OTP");
			throw new BusinessException(ServiceError.Unknown, "Error While checking OTP");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean verifyOTP(String mobileNumber, String otpNumber, String countryCode) {
		Boolean response = false;
		try {
			OTPCollection otpCollection = otpRepository.findByMobileNumberAndOtpNumberAndCountryCode(mobileNumber,
					otpNumber, countryCode);
			System.out.println(otpCollection);
			if (otpCollection != null) {
				if (isOTPValid(otpCollection.getCreatedTime())) {
					otpCollection.setState(OTPState.VERIFIED);
					otpCollection = otpRepository.save(otpCollection);
					response = true;
				} else {
					logger.error("OTP is expired");
					throw new BusinessException(ServiceError.NotFound, "OTP is expired");
				}

			} else {
				logger.error("Incorrect OTP");
				throw new BusinessException(ServiceError.NotFound, "Incorrect OTP");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Verifying OTP");
			throw new BusinessException(ServiceError.Unknown, "Error While Verifying OTP");
		}
		return response;
	}

}
