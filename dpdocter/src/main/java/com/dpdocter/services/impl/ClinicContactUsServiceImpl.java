package com.dpdocter.services.impl;

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

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.collections.ClinicContactUsCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicContactUsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.ClinicContactUsService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicContactUsServiceImpl implements ClinicContactUsService {

	private static Logger logger = Logger.getLogger(ClinicContactUsServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ClinicContactUsRepository clinicContactUsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Value(value = "${doctor.welcome.message}")
	private String doctorWelcomeMessage;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Value(value = "${mail.contact.us.welcome.subject}")
	private String doctorWelcomeSubject;

	@Value(value = "${mail.signup.request.to}")
	private String mailTo;

	@Value(value = "${mail.signup.request.subject}")
	private String signupRequestSubject;

	@Override
	public String submitClinicContactUSInfo(ClinicContactUs clinicContactUs) {
		String response = null;
		DoctorCollection doctorCollection = null;
		ClinicContactUsCollection clinicContactUsCollection = new ClinicContactUsCollection();

		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(clinicContactUs.getDoctorId())).orElse(null);

			if (userCollection != null)
				doctorCollection = doctorRepository.findByUserId(userCollection.getId());
			if (doctorCollection != null) {
				BeanUtil.map(clinicContactUs, clinicContactUsCollection);
				clinicContactUsCollection = clinicContactUsRepository.save(clinicContactUsCollection);
				String body = mailBodyGenerator.generateActivationEmailBody(
						userCollection.getTitle() + " " + userCollection.getFirstName(), null,
						"doctorWelcomeTemplate.vm", null, null);
				mailService.sendEmail(clinicContactUs.getEmailAddress(), doctorWelcomeSubject, body, null);
				body = mailBodyGenerator.generateContactEmailBody(
						userCollection.getTitle() + " " + userCollection.getFirstName(), "Location",
						userCollection.getMobileNumber(), userCollection.getEmailAddress(), clinicContactUs.getCity());
				mailService.sendEmail(mailTo, signupRequestSubject, body, null);
				if (clinicContactUsCollection != null) {
					response = doctorWelcomeMessage;
				}
			} else
				throw new BusinessException(ServiceError.Unknown, "doctorId");
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown,
					"An account already exists with this email address.Please use another email address to register.");
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while creating doctor");
			throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
		}

		return response;
	}

	@Override
	public ClinicContactUs updateClinicContactState(String contactId, DoctorContactStateType contactState) {
		ClinicContactUs response = null;
		if (contactId != null && !(contactId.isEmpty())) {
			try {
				ClinicContactUsCollection clinicContactUsCollection = clinicContactUsRepository
						.findById(new ObjectId(contactId)).orElse(null);
				if (clinicContactUsCollection != null) {
					clinicContactUsCollection.setContactState(contactState);
					clinicContactUsCollection = clinicContactUsRepository.save(clinicContactUsCollection);
					if (clinicContactUsCollection != null) {
						response = new ClinicContactUs();
						BeanUtil.map(clinicContactUsCollection, response);
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

	@Override
	public List<ClinicContactUs> getDoctorContactList(long page, int size, String searchTerm) {
		List<ClinicContactUs> response = null;
		// String searchTerm = null;
		Criteria criteria = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = new Criteria("locationName").regex("^" + searchTerm, "i");
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

			AggregationResults<ClinicContactUs> aggregationResults = mongoTemplate.aggregate(aggregation,
					ClinicContactUsCollection.class, ClinicContactUs.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting hospitals " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting doctor contact List " + e.getMessage());
		}
		return response;
	}

}
