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
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.ClinicalnoteLookupBean;
import com.dpdocter.beans.ClinicalnotesComplaintField;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
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


			Criteria criteria = new Criteria("patientId")
					.is(patientObjectId);
					//.and("isPatientDiscarded").ne(true);
			

			Aggregation aggregation = null;

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), 
						Aggregation.limit(1));
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

}
