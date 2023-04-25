package com.dpdocter.services.v2.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.v2.PatientCard;
import com.dpdocter.collections.AdmitCardCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.response.v2.AdmitCardResponse;
import com.dpdocter.services.v2.AdmitCardService;

import common.util.web.DPDoctorUtils;

@Transactional
@Service(value = "AdmitCardServiceImplV2")
public class AdmitCardServiceImpl implements AdmitCardService {

	private static Logger logger = Logger.getLogger(AdmitCardServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientRepository patientRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.admitCard.a4.fileName}")
	private String admitCardReportA4FileName;

	@Override
	public List<AdmitCardResponse> getAdmitCards(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, long updatedTime, Boolean discarded) {
		List<AdmitCardResponse> response = null;
		try {
			Criteria criteria = new Criteria("isPatientDiscarded").ne(true);
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria = criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (updatedTime > 0) {
				criteria = criteria.and("updatedTime").is(new Date(updatedTime));
			}

			criteria = criteria.and("discarded").is(discarded);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<AdmitCardResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					AdmitCardCollection.class, AdmitCardResponse.class);
			response = aggregationResults.getMappedResults();

			PatientCollection patientCollection = null;
			PatientCard patient = null;

			for (AdmitCardResponse admitCardResponse : response) {

				patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						new ObjectId(admitCardResponse.getPatientId()), new ObjectId(admitCardResponse.getLocationId()),
						new ObjectId(admitCardResponse.getHospitalId()));
				patient = new PatientCard();
				BeanUtil.map(patientCollection, patient);
				admitCardResponse.setPatient(patient);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While get Admit cards ");
		}
		return response;
	}

}
