package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;

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
				
				String body = mailBodyGenerator.doctorWelcomeEmailBody(
						doctorContactUs.getTitle() + " " + doctorContactUs.getFirstName(), tokenCollection.getId(),
						"doctorWelcomeTemplate.vm", null, null);
				mailService.sendEmail(doctorContactUs.getEmailAddress(), doctorWelcomeSubject, body, null);

				
				
				body = mailBodyGenerator.generateContactEmailBody(doctorContactUs, "Doctor");
				mailService.sendEmail(mailTo, signupRequestSubject, body, null);

				if (doctorContactUsCollection != null) {
					response = doctorWelcomeMessage;
				}
			} catch (DuplicateKeyException de) {
				logger.error(de);
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
	public Boolean resendWelcomeMessage(String emailAddress) {
		Boolean response = false;
		DoctorContactUsCollection doctorContactUsCollection = null;
		if (emailAddress != null) {
			
			try {
				doctorContactUsCollection = doctorContactUsRepository.findByEmailIdAndUserName(emailAddress);
				
				if(doctorContactUsCollection == null){
					throw new BusinessException(ServiceError.NoRecord , "Record Not found for email address");
				}

				
				TokenCollection tokenCollection = new TokenCollection();
				tokenCollection.setResourceId(doctorContactUsCollection.getId());
				tokenCollection.setCreatedTime(new Date());
				tokenCollection = tokenRepository.save(tokenCollection);
				
				String body = mailBodyGenerator.doctorWelcomeEmailBody(
						doctorContactUsCollection.getTitle() + " " + doctorContactUsCollection.getFirstName(), tokenCollection.getId(),
						"doctorWelcomeTemplate.vm", null, null);
				mailService.sendEmail(doctorContactUsCollection.getEmailAddress(), doctorWelcomeSubject, body, null);

				response = true;
			} catch (BusinessException be) {
				logger.error(be);
				throw be;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error occured while sending welcome mail");
				throw new BusinessException(ServiceError.Unknown, " Error occured while sending welcome mail");
			}
		}
		return response;
	}
	@Override
	@Transactional
	public List<DoctorContactUs> getDoctorContactList(int page, int size, String searchTerm) {
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
							Aggregation.skip((long)(page) * size), Aggregation.limit(size));
				else
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			} else {
				if (size > 0)
					aggregation = Aggregation.newAggregation(
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((long)(page) * size), Aggregation.limit(size));
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
						.findByContactId(contactId);
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
}
