package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.collections.DoctorOTPCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.OTPState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.DoctorOTPRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;

import common.util.web.LoginUtils;

@Service
public class OTPServiceImpl implements OTPService {

    private static Logger logger = Logger.getLogger(OTPServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private SMSServices sMSServices;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private DoctorOTPRepository doctorOTPRepository;

    @Autowired
    private SMSFormatRepository sMSFormatRepository;

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
	
    @Value(value = "${mail.recordsShareOtpBeforeVerification.subject}")
    private String recordsShareOtpBeforeVerification;

    @Value(value = "${mail.recordsShareOtpAfterVerification.subject}")
    private String recordsShareOtpAfterVerification;

    @Override
    @Transactional
    public String otpGenerator(String doctorId, String locationId, String hospitalId, String patientId, UriInfo uriInfo) {
	String OTP = null;
	try {
	    OTP = LoginUtils.generateOTP();
	    UserCollection userCollection = userRepository.findOne(doctorId);
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    UserCollection patient = userRepository.findOne(patientId);
	    PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(patientId, doctorId, locationId, hospitalId);
	    if (userCollection != null && patient != null && userLocationCollection != null) {

		String doctorName = (userCollection.getTitle() != null ? userCollection.getTitle() : "") + " " + userCollection.getFirstName();

		OTPCollection otpCollection = new OTPCollection();
		otpCollection.setCreatedTime(new Date());
		otpCollection.setOtpNumber(OTP);

		if (userCollection != null)
		    otpCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		otpCollection.setGeneratorId(doctorId);
		otpCollection = otpRepository.save(otpCollection);

		DoctorOTPCollection doctorOTPCollection = new DoctorOTPCollection();
		doctorOTPCollection.setCreatedTime(new Date());
		doctorOTPCollection.setOtpId(otpCollection.getId());
		doctorOTPCollection.setUserLocationId(userLocationCollection.getId());
		doctorOTPCollection.setPatientId(patientId);
		doctorOTPCollection = doctorOTPRepository.save(doctorOTPCollection);

		SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(doctorId, locationId, hospitalId, patientId, patient.getFirstName(),
				"OTP to share Healthcoco records with "+doctorName+" is "+OTP+".Pls don't share this with anyone.Stay Healthy & Happy!!",
			patient.getMobileNumber(), "OTPVerification");
		sMSServices.sendSMS(smsTrackDetail, false);

		if (patientCollection != null && patientCollection.getEmailAddress() != null && !patientCollection.getEmailAddress().isEmpty()) {
		    String body = mailBodyGenerator.generateRecordsShareOtpBeforeVerificationEmailBody(patientCollection.getEmailAddress(),
			    patient.getFirstName(), doctorName, uriInfo);
		    mailService.sendEmail(patientCollection.getEmailAddress(), recordsShareOtpBeforeVerification, body, null);
		}
		pushNotificationServices.notifyUser(patient.getId(), "Dr. "+userCollection.getFirstName()+" has requested to view your medical history, share OTP that was sent to your registered mobile number to provide access", null, null);
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
    public Boolean verifyOTP(String doctorId, String locationId, String hospitalId, String patientId, String otpNumber, UriInfo uriInfo) {
	Boolean response = false;
	try {
	    UserCollection userCollection = userRepository.findOne(doctorId);
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    UserCollection patient = userRepository.findOne(patientId);

	    PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(patientId, doctorId, locationId, hospitalId);
	    if (userCollection != null && patient != null && userLocationCollection != null && patientId != null) {
		String doctorName = (userCollection.getTitle() != null ? userCollection.getTitle() : "") + " " + userCollection.getFirstName();
		List<DoctorOTPCollection> doctorOTPCollection = doctorOTPRepository.find(userLocationCollection.getId(), patientId,
			new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "createdTime")));
		if (doctorOTPCollection != null) {
		    OTPCollection otpCollection = otpRepository.findOne(doctorOTPCollection.get(0).getOtpId());
		    if (otpCollection != null) {
			if (otpCollection.getOtpNumber().equals(otpNumber)) {
			    if (isOTPValid(otpCollection.getCreatedTime())) {
				otpCollection.setState(OTPState.VERIFIED);
				otpCollection = otpRepository.save(otpCollection);
				response = true;
				if (patientCollection != null && patientCollection.getEmailAddress() != null && !patientCollection.getEmailAddress().isEmpty()) {
				    String body = mailBodyGenerator.generateRecordsShareOtpAfterVerificationEmailBody(patientCollection.getEmailAddress(),
					    patient.getFirstName(), doctorName, uriInfo);
				    mailService.sendEmail(patientCollection.getEmailAddress(),
					    recordsShareOtpAfterVerification + " " + userCollection.getFirstName(), body, null);
				}
				pushNotificationServices.notifyUser(patient.getId(), "Dr. "+userCollection.getFirstName()+" can now access your medical history, Tap to know more about Healthcoco share.", null, null);
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
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    if (userLocationCollection != null) {
		List<DoctorOTPCollection> doctorOTPCollection = doctorOTPRepository.find(userLocationCollection.getId(), patientId,
			new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "createdTime")));
		if (doctorOTPCollection != null && !doctorOTPCollection.isEmpty() && doctorOTPCollection.size() > 0) {
		    OTPCollection otpCollection = otpRepository.findOne(doctorOTPCollection.get(0).getOtpId());
		    if (otpCollection != null && otpCollection.getState().equals(OTPState.VERIFIED)) {
			if (isOTPValid(otpCollection.getCreatedTime()))
			    response = true;
		    }
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
	return Minutes.minutesBetween(new DateTime(createdTime), new DateTime()).isLessThan(Minutes.minutes(Integer.parseInt(otpTimeDifference)));
    }

    @Override
    @Transactional
    public boolean isNonVerifiedOTPValid(Date createdTime) {
	return Minutes.minutesBetween(new DateTime(createdTime), new DateTime()).isLessThan(Minutes.minutes(Integer.parseInt(otpNonVerifiedTimeDifference)));
    }

    @Override
    @Transactional
    public Boolean otpGenerator(String mobileNumber) {
    	Boolean response = false;
	String OTP = null;
	try {
	    OTP = LoginUtils.generateOTP();
	    SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null,
	    	OTP+" is your Healthcoco verification code. Code is valid for 30 minutes only, one time use. Stay Healthy and Happy!", mobileNumber,
		    "OTPVerification");
	    response = sMSServices.sendSMS(smsTrackDetail, false);

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
	    throw new BusinessException(ServiceError.Unknown, "Error While Generating OTP");
	}
	return response;
    }

    @Override
    @Transactional
    public boolean verifyOTP(String mobileNumber, String otpNumber) {
	Boolean response = false;
	try {
	    OTPCollection otpCollection = otpRepository.findOne(mobileNumber, otpNumber, mobileNumber);
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
	        OTPCollection otpCollection = otpRepository.findOne(mobileNumber, otpNumber, mobileNumber);
		    if (otpCollection != null && otpCollection.getState().equals(OTPState.VERIFIED)) response = true;
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error While checking OTP");
	    throw new BusinessException(ServiceError.Unknown, "Error While checking OTP");
	}
	return response;
    }
}
