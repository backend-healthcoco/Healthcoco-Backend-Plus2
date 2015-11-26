package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.SMSTrackDetail;
import com.dpdocter.collections.DoctorOTPCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.DoctorOTPRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.OTPService;
import com.dpdocter.sms.services.SMSServices;

import common.util.web.DPDoctorUtils;
import common.util.web.LoginUtils;

@Service
public class OTPServiceImpl implements OTPService{

	private static Logger logger = Logger.getLogger(OTPServiceImpl.class.getName());
	
	@Autowired
    private UserRepository userRepository;

	@Autowired
    private SMSServices sMSServices;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private DoctorOTPRepository doctorOTPRepository;

    @Value(value = "${OTP_VALIDATION_TIME_DIFFERENCE}")
    private String otpTimeDifference;

    @Override
    public String otpGenerator(String doctorId, String locationId, String hospitalId, String patientId, String mobileNumber) {
	String OTP = null;
	try {
	    OTP = LoginUtils.generateOTP();
	    SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(doctorId, locationId, hospitalId, patientId, OTP+ " is OTP for Verification", mobileNumber);
	    sMSServices.sendSMS(smsTrackDetail, false);
	    
	    OTPCollection otpCollection = new OTPCollection();
	    otpCollection.setCreatedTime(new Date());
	    otpCollection.setOtpNumber(OTP);
	    UserCollection userCollection = userRepository.findOne(doctorId);
	    if(userCollection != null)otpCollection.setCreatedBy(userCollection.getFirstName());
	    otpCollection = otpRepository.save(otpCollection);
	    
	    DoctorOTPCollection doctorOTPCollection = new DoctorOTPCollection();
	    doctorOTPCollection.setCreatedTime(new Date());
	    doctorOTPCollection.setOtpId(otpCollection.getId());
	    doctorOTPCollection.setDoctorId(doctorId);
	    doctorOTPCollection.setLocationId(locationId);
	    doctorOTPCollection.setHospitalId(hospitalId);
	    doctorOTPCollection.setPatientId(patientId);
	    doctorOTPCollection = doctorOTPRepository.save(doctorOTPCollection);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error While Generating OTP");
	    throw new BusinessException(ServiceError.Unknown, "Error While Generating OTP");
	}

	return OTP;
}

	@Override
	public Boolean verifyOTP(String doctorId, String locationId, String hospitalId, String patientId, String otpNumber) {
		Boolean response = false;
		try {
		    List<DoctorOTPCollection> doctorOTPCollection = doctorOTPRepository.find(doctorId, locationId, hospitalId, patientId, new Sort(Sort.Direction.DESC, "createdTime"));
		    if(doctorOTPCollection != null){
		    	OTPCollection otpCollection = otpRepository.findOne(doctorOTPCollection.get(0).getOtpId());
		    	if(otpCollection != null){
		    		if(otpCollection.getOtpNumber().equals(otpNumber)){
		    			if(isOTPValid(otpCollection.getCreatedTime())){
		    				otpCollection.setIsVerified(true);
		    				otpCollection = otpRepository.save(otpCollection);
		    				response = true;
		    			}
		    			else{
		    				logger.error("OTP is expired");
			    		    throw new BusinessException(ServiceError.NotFound, "OTP is expired");
		    			}
		    		}
		    		else{
		    			logger.error("Incorrect OTP");
		    		    throw new BusinessException(ServiceError.NotFound, "Incorrect OTP");
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
	public Boolean checkOTPVerified(String doctorId, String locationId, String hospitalId, String patientId) {
		Boolean response = false;
		try {
		    List<DoctorOTPCollection> doctorOTPCollection = doctorOTPRepository.find(doctorId, locationId, hospitalId, patientId, new Sort(Sort.Direction.DESC, "createdTime"));
		    if(doctorOTPCollection != null && !doctorOTPCollection.isEmpty() && doctorOTPCollection.size() > 0){
		    	OTPCollection otpCollection = otpRepository.findOne(doctorOTPCollection.get(0).getOtpId());
		    	if(otpCollection != null)response = otpCollection.getIsVerified();
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error While checking OTP");
		    throw new BusinessException(ServiceError.Unknown, "Error While checking OTP");
		}
		return response;
	}

	public Boolean isOTPValid(Date createdTime){
    	 return Minutes.minutesBetween(new DateTime(createdTime), new DateTime()).isLessThan(Minutes.minutes(Integer.parseInt(otpTimeDifference)));
    }
}
