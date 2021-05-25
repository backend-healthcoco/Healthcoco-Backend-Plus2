package com.dpdocter.services.impl;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.ClinicalNotesResponseFieldWise;
import com.dpdocter.beans.ClinicalnoteLookupBean;
import com.dpdocter.beans.ClinicalnotesComplaintField;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.OTReports;
import com.dpdocter.collections.AdmitCardCollection;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.AdmitCardResponseFieldWise;
import com.dpdocter.response.OTReportsResponse;
import com.dpdocter.services.VisitFieldWiseService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class VisitFieldWiseServiceImpl implements VisitFieldWiseService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Response<Object> getComplaintData(String doctorId, String locationId, String hospitalId, String patientId) {

		Response<Object> response = new Response<Object>();
		List<ClinicalnotesComplaintField> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
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

			Criteria criteria = new Criteria("patientId").is(patientObjectId);
			// .and("isPatientDiscarded").ne(true);

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
									true))),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.limit(1));
//			else
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
//						new CustomAggregationOperation(
//								new Document("$unwind",
//										new BasicDBObject("path", "$appointmentRequest")
//												.append("preserveNullAndEmptyArrays", true))),
//						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<ClinicalnotesComplaintField> aggregationResults = mongoTemplate.aggregate(aggregation,
					ClinicalNotesCollection.class, ClinicalnotesComplaintField.class);
			clinicalNotesCollections = aggregationResults.getMappedResults();

			response.setDataList(clinicalNotesCollections);
//			if (clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()) {
//				clinicalNotes = new ArrayList<ClinicalNotes>();
//				for (ClinicalnoteLookupBean clinicalNotesCollection : clinicalNotesCollections) {
//					ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
//					clinicalNotes.add(clinicalNote);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
//			logger.error(" Error Occurred While Getting Clinical Notes");
//			try {
//				mailService.sendExceptionMail("Backend Business Exception :: While getting clinical notes",
//						e.getMessage());
//			} catch (MessagingException e1) {
//				e1.printStackTrace();
//			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes");
		}
		return response;
	}

	@Override
	public Response<Object> getAdmitCardData(String doctorId, String locationId, String hospitalId, String patientId,
			String type) {
		Response<Object> response = new Response<Object>();
		try {
			ObjectId patientObjectId = new ObjectId(patientId), doctorObjectId = new ObjectId(doctorId),
					locationObjectId = new ObjectId(locationId), hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("patientId").is(patientObjectId).and("doctorId").is(doctorObjectId)
					.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
					.and("isPatientDiscarded").ne(true);

			Integer count = (int) mongoTemplate.count(new Query(criteria), AdmitCardCollection.class);
			response.setCount(count);
			if (count != 0) {
				Aggregation aggregation = null;

				if (type.equalsIgnoreCase("first")) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.ASC, "createdTime")), Aggregation.limit(1));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.limit(1));
				}
				List<AdmitCardResponseFieldWise> patientVisitlookupbeans = mongoTemplate
						.aggregate(aggregation, AdmitCardCollection.class, AdmitCardResponseFieldWise.class)
						.getMappedResults();

				if (patientVisitlookupbeans != null && !patientVisitlookupbeans.isEmpty()) {
					System.out.println(patientVisitlookupbeans.size());
					for (AdmitCardResponseFieldWise patientVisitlookupBean : patientVisitlookupbeans) {
						response.setData(patientVisitlookupBean);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
//		logger.error(e + " Error while geting patient  Visit : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while geting patient admit Card : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public Response<Object> getOperationNotesData(String doctorId, String locationId, String hospitalId,
			String patientId, String type) {
		Response<Object> response = new Response<Object>();
		try {
			ObjectId patientObjectId = new ObjectId(patientId), doctorObjectId = new ObjectId(doctorId),
					locationObjectId = new ObjectId(locationId), hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("patientId").is(patientObjectId).and("doctorId").is(doctorObjectId)
					.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
					.and("isPatientDiscarded").ne(true).and("discarded").ne(true);

			Integer count = (int) mongoTemplate.count(new Query(criteria), OTReportsCollection.class);
			response.setCount(count);
			if (count != 0) {
				Aggregation aggregation = null;

				if (type.equalsIgnoreCase("first")) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.ASC, "createdTime")), Aggregation.limit(1));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.limit(1));
				}
				List<OTReports> patientVisitlookupbeans = mongoTemplate
						.aggregate(aggregation, OTReportsCollection.class, OTReports.class)
						.getMappedResults();

				if (patientVisitlookupbeans != null && !patientVisitlookupbeans.isEmpty()) {
					System.out.println(patientVisitlookupbeans.size());
					for (OTReports patientVisitlookupBean : patientVisitlookupbeans) {
						response.setData(patientVisitlookupBean);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
//		logger.error(e + " Error while geting patient  Visit : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while geting patient admit Card : " + e.getCause().getMessage());
		}

		return response;
	}

}
