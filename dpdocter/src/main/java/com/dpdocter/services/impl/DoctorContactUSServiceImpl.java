package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;

@Service
public class DoctorContactUSServiceImpl implements DoctorContactUsService {

	private static Logger logger = Logger.getLogger(DoctorContactUSServiceImpl.class.getName());

	@Autowired
	DoctorContactUsRepository doctorContactUsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${doctor.welcome.message}")
	private String doctorWelcomeMessage;

	@Value(value = "${mail.signup.request.to}")
	private String mailTo;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Value(value = "${mail.contact.us.welcome.subject}")
	private String doctorWelcomeSubject;

	@Value(value = "${mail.signup.request.subject}")
	private String signupRequestSubject;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private SMSServices smsServices;
	
	@Value(value = "${welcome.link}")
	private String welcomeLink;
	
	@Autowired
	private ForgotPasswordService forgotPasswordService;

	@Override
	@Transactional
	public String submitDoctorContactUSInfo(DoctorContactUs doctorContactUs) {
		String response = null;
		DoctorContactUsCollection doctorContactUsCollection = new DoctorContactUsCollection();
		if (doctorContactUs != null) {
			BeanUtil.map(doctorContactUs, doctorContactUsCollection);
			try {
				doctorContactUsCollection.setCreatedTime(new Date());
				doctorContactUsCollection.setUserName(doctorContactUs.getEmailAddress());
				doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);

				
				TokenCollection tokenCollection = new TokenCollection();
				tokenCollection.setResourceId(doctorContactUsCollection.getId());
				tokenCollection.setCreatedTime(new Date());
				tokenCollection = tokenRepository.save(tokenCollection);
				
				String body = mailBodyGenerator.generateActivationEmailBody(
						doctorContactUs.getTitle() + " " + doctorContactUs.getFirstName(), tokenCollection.getId(),
						"doctorWelcomeTemplate.vm", null, null, null);
				mailService.sendEmail(doctorContactUs.getEmailAddress(), doctorWelcomeSubject, body, null);

				
				body = mailBodyGenerator.generateContactEmailBody(doctorContactUs, "Doctor");
				mailService.sendEmail(mailTo, signupRequestSubject, body, null);
				
				
			//	Thank you for signing up, we are excited to get you started with Healthcoco+. Our representative will reach out to you within 1 working day.
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
					
						smsTrackDetail.setType(ComponentType.SIGNED_UP.getType());
						SMSDetail smsDetail = new SMSDetail();
						
						smsDetail.setUserName(doctorContactUs.getFirstName());
						SMS sms = new SMS();
						String link = welcomeLink + "/" + tokenCollection.getId()+"/";
						String shortUrl = DPDoctorUtils.urlShortner(link);
						sms.setSmsText("Please set your Healthcoco+ password, link is valid for 60 min only. Password set link  " 
								+ shortUrl);
		
							SMSAddress smsAddress = new SMSAddress();
						smsAddress.setRecipient(doctorContactUs.getMobileNumber());
						sms.setSmsAddress(smsAddress);
						smsDetail.setSms(sms);
						smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
						List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
						smsDetails.add(smsDetail);
						smsTrackDetail.setSmsDetails(smsDetails);
						smsServices.sendSMS(smsTrackDetail, true);
				
				
				
				if (doctorContactUsCollection != null) {
					response = doctorWelcomeMessage;
				}
			} catch (DuplicateKeyException de) {
				logger.error(de);
				ForgotUsernamePasswordRequest request = new ForgotUsernamePasswordRequest();
				request.setEmailAddress(doctorContactUs.getEmailAddress());
				request.setMobileNumber(doctorContactUs.getMobileNumber());
				request.setUsername(doctorContactUs.getUserName());
				forgotPasswordService.forgotPasswordForDoctor(request);
				throw new BusinessException(ServiceError.Unknown,
						"An account already exists with " + doctorContactUs.getEmailAddress()+" .Please use another email address to register.");
			} catch (BusinessException be) {
				logger.error(be);
				throw be;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error occured while creating doctor");
				throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
			}
		}
		return response;
	}

	@Override
	@Transactional
	public List<DoctorContactUs> getDoctorContactList(long page, int size, String searchTerm) {
		List<DoctorContactUs> response = null;
		// String searchTerm = null;
		Criteria criteria = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = new Criteria().orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						(new Criteria("emailAddress").regex("^" + searchTerm, "i")));
			Aggregation aggregation = null;
			if (criteria != null) {
				if (size > 0)
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((page) * size), Aggregation.limit(size));
				else
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			} else {
				if (size > 0)
					aggregation = Aggregation.newAggregation(
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((page) * size), Aggregation.limit(size));
				else
					aggregation = Aggregation
							.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}

			AggregationResults<DoctorContactUs> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorContactUsCollection.class, DoctorContactUs.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting hospitals " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting doctor contact List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactUs updateDoctorContactState(String contactId, DoctorContactStateType contactState) {
		DoctorContactUs response = null;
		if (contactId != null && !(contactId.isEmpty())) {
			try {
				DoctorContactUsCollection doctorContactUsCollection = doctorContactUsRepository
						.findById(new ObjectId(contactId)).orElse(null);
				if (doctorContactUsCollection != null) {
					doctorContactUsCollection.setContactState(contactState);
					doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);
					if (doctorContactUsCollection != null) {
						response = new DoctorContactUs();
						BeanUtil.map(doctorContactUsCollection, response);
					}
				}
			} catch (Exception e) {
				logger.warn("Error while updating contact state :: " + e);
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,
						"Error while updating doctor contact state " + e.getMessage());
			}
		}
		return response;
	}
	
	private void sendWelcomeMessage(String mobileNumber , String tokenId) {
		try {
				String link = welcomeLink + "/" + tokenId;
				String shortUrl = DPDoctorUtils.urlShortner(link);
				String message = "Thank you for joining the Healthcoco community. Kindly set the new password by clicking on the link below, and login to enjoy our services. "+shortUrl;
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setType("WELCOME_SMS");
				SMSDetail smsDetail = new SMSDetail();
				SMS sms = new SMS();
				sms.setSmsText(message);

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(mobileNumber);
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
