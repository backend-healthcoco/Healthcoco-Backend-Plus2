package com.dpdocter.services.v2.impl;

import java.util.ArrayList;
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

import com.dpdocter.collections.DischargeSummaryCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.v2.DischargeSummaryResponse;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.v2.DischargeSummaryService;
import com.dpdocter.services.v2.PrescriptionServices;

import common.util.web.DPDoctorUtils;

@Service(value = "DischargeSummaryServiceImplV2")
public class DischargeSummaryServiceImpl implements DischargeSummaryService {

	private static Logger logger = Logger.getLogger(DischargeSummaryServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PrescriptionServices prescriptionServices;

	
	@Value(value = "${jasper.print.dischargeSummary.a4.fileName}")
	private String dischargeSummaryReportA4FileName;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	PushNotificationServices pushNotificationServices;
	
	

	@Override
	@Transactional
	public List<DischargeSummaryResponse> getDischargeSummary(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, String updatedTime, Boolean discarded) {
		List<DischargeSummaryResponse> response = null;
		try {
			DischargeSummaryResponse summaryResponse = null;
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(updatedTime))).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			if (!discarded)
				criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<DischargeSummaryCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
					DischargeSummaryCollection.class, DischargeSummaryCollection.class);
			List<DischargeSummaryCollection> dischargeSummaryCollections = aggregationResults.getMappedResults();
			response = new ArrayList<DischargeSummaryResponse>();
			for (DischargeSummaryCollection dischargeSummaryCollection : dischargeSummaryCollections) {
				summaryResponse = new DischargeSummaryResponse();
				BeanUtil.map(dischargeSummaryCollection, summaryResponse);

				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getPrescriptionId())) {
					summaryResponse.setPrescriptions(prescriptionServices
							.getPrescriptionById(dischargeSummaryCollection.getPrescriptionId().toString()));
				}
				if (dischargeSummaryCollection.getDiagrams() != null
						&& !dischargeSummaryCollection.getDiagrams().isEmpty()) {
					summaryResponse.setDiagrams(new ArrayList<String>());
					for (String img : dischargeSummaryCollection.getDiagrams()) {
						img = getFinalImageURL(img);
						summaryResponse.getDiagrams().add(img);
					}

				}
				response.add(summaryResponse);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting discharge summary : " + e.getCause().getMessage());
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
	

}
