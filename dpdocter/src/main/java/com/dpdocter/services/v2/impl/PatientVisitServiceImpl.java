package com.dpdocter.services.v2.impl;

import static com.dpdocter.enums.VisitedFor.CLINICAL_NOTES;
import static com.dpdocter.enums.VisitedFor.PRESCRIPTION;
import static com.dpdocter.enums.VisitedFor.REPORTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.EyePrescription;
import com.dpdocter.beans.PatientVisitLookupBean;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.v2.ClinicalNotes;
import com.dpdocter.beans.v2.Diagram;
import com.dpdocter.beans.v2.DoctorContactsResponse;
import com.dpdocter.beans.v2.PatientCard;
import com.dpdocter.beans.v2.PatientTreatment;
import com.dpdocter.beans.v2.Prescription;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.EyePrescriptionRepository;
import com.dpdocter.response.v2.PatientVisitResponse;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RecordsService;
import com.dpdocter.services.v2.ClinicalNotesService;
import com.dpdocter.services.v2.PatientTreatmentServices;
import com.dpdocter.services.v2.PatientVisitService;
import com.dpdocter.services.v2.PrescriptionServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service(value = "PatientVisitServiceImplV2")
public class PatientVisitServiceImpl implements PatientVisitService {

	private static Logger logger = Logger.getLogger(PatientVisitServiceImpl.class.getName());

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Autowired
	private PatientTreatmentServices patientTreatmentServices;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private RecordsService recordsService;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.visit.a4.fileName}")
	private String visitA4FileName;

	@Value(value = "${jasper.print.visit.clinicalnotes.a4.fileName}")
	private String visitClinicalNotesA4FileName;

	@Value(value = "${jasper.print.visit.prescription.a4.fileName}")
	private String visitPrescriptionA4FileName;

	@Value(value = "${jasper.print.visit.diagrams.a4.fileName}")
	private String visitDiagramsA4FileName;

	@Value(value = "${jasper.print.visit.a5.fileName}")
	private String visitA5FileName;

	@Value(value = "${jasper.print.visit.clinicalnotes.a5.fileName}")
	private String visitClinicalNotesA5FileName;

	@Value(value = "${jasper.print.visit.prescription.a5.fileName}")
	private String visitPrescriptionA5FileName;

	@Value(value = "${jasper.print.visit.diagrams.a5.fileName}")
	private String visitDiagramsA5FileName;

	@Value(value = "${jasper.print.prescription.subreport.a4.fileName}")
	private String prescriptionSubReportA4FileName;

	@Value(value = "${jasper.print.prescription.subreport.a5.fileName}")
	private String prescriptionSubReportA5FileName;

	@Autowired
	private EyePrescriptionRepository eyePrescriptionRepository;

	@Autowired
	private ContactsService contactsService;

	

	@Override
	@Transactional
	public DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page,
			int size, String role) {
		DoctorContactsResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
					.and("isPatientDiscarded").ne(true);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria2.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);

			CustomAggregationOperation redactOperations = null;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(role)) {
					redactOperations = new CustomAggregationOperation(new Document("$redact",
							new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$and", Arrays.asList(
									new BasicDBObject("$eq", Arrays.asList("$patient.locationId", locationObjectId)),
									new BasicDBObject("$eq", Arrays.asList("$patient.hospitalId", hospitalObjectId)),
									new BasicDBObject("$eq",
											Arrays.asList("$patient.consultantDoctorIds", doctorObjectId)))))
													.append("then", "$$KEEP").append("else", "$$PRUNE"))));
					criteria2.and("consultantDoctorIds").is(doctorObjectId);
				} else {
					redactOperations = new CustomAggregationOperation(new Document("$redact",
							new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$and", Arrays.asList(
									new BasicDBObject("$eq", Arrays.asList("$patient.locationId", locationObjectId)),
									new BasicDBObject("$eq", Arrays.asList("$patient.hospitalId", hospitalObjectId)),
									new BasicDBObject("$eq", Arrays.asList("$patient.doctorId", doctorObjectId)))))
											.append("then", "$$KEEP").append("else", "$$PRUNE"))));
					criteria2.and("doctorId").is(doctorObjectId);
				}
			} else {
				redactOperations = new CustomAggregationOperation(new Document("$redact",
						new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$and", Arrays.asList(
								new BasicDBObject("$eq", Arrays.asList("$patient.locationId", locationObjectId)),
								new BasicDBObject("$eq", Arrays.asList("$patient.hospitalId", hospitalObjectId)))))
										.append("then", "$$KEEP").append("else", "$$PRUNE"))));
			}

			CustomAggregationOperation projectOperations = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("patientId", "$patientId").append("userId", "$patient.userId")
							.append("firstName", "$patient.firstName")
							.append("localPatientName", "$patient.localPatientName")
							.append("userName", "$patient.userName").append("emailAddress", "$patient.emailAddress")
							.append("imageUrl", "$patient.imageUrl").append("thumbnailUrl", "$patient.thumbnailUrl")
							.append("bloodGroup", "$patient.bloodGroup").append("PID", "$patient.PID")
							.append("gender", "$patient.gender").append("countryCode", "$patient.countryCode")
							.append("mobileNumber", "$patient.mobileNumber")
							.append("secPhoneNumber", "$patient.secPhoneNumber").append("dob", "$patient.dob")
							.append("dateOfVisit", "$patient.dateOfVisit").append("doctorId", "$patient.doctorId")
							.append("locationId", "$patient.locationId").append("hospitalId", "$patient.hospitalId")
							.append("colorCode", "$user.colorCode").append("user", "$user")
							.append("address", "$patient.address").append("patientId", "$patient.userId")
							.append("profession", "$patient.profession").append("relations", "$patient.relations")
							.append("consultantDoctorIds", "$patient.consultantDoctorIds")
							.append("registrationDate", "$patient.registrationDate")
							.append("createdTime", "$patient.createdTime").append("updatedTime", "$patient.updatedTime")
							.append("createdBy", "$patient.createdBy").append("visitedTime", "$visitedTime")));

			CustomAggregationOperation groupOperations = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("patientId", "$patientId"))
							.append("userId", new BasicDBObject("$first", "$userId"))
							.append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("localPatientName", new BasicDBObject("$first", "$localPatientName"))
							.append("userName", new BasicDBObject("$first", "$userName"))
							.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
							.append("imageUrl", new BasicDBObject("$first", "$imageUrl"))
							.append("thumbnailUrl", new BasicDBObject("$first", "$thumbnailUrl"))
							.append("bloodGroup", new BasicDBObject("$first", "$bloodGroup"))
							.append("PID", new BasicDBObject("$first", "$PID"))
							.append("gender", new BasicDBObject("$first", "$gender"))
							.append("countryCode", new BasicDBObject("$first", "$countryCode"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("secPhoneNumber", new BasicDBObject("$first", "$secPhoneNumber"))
							.append("dob", new BasicDBObject("$first", "$dob"))
							.append("dateOfVisit", new BasicDBObject("$first", "$dateOfVisit"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("colorCode", new BasicDBObject("$first", "$user.colorCode"))
							.append("user", new BasicDBObject("$first", "$user"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("patientId", new BasicDBObject("$first", "$userId"))
							.append("profession", new BasicDBObject("$first", "$profession"))
							.append("relations", new BasicDBObject("$first", "$relations"))
							.append("consultantDoctorIds", new BasicDBObject("$first", "$consultantDoctorIds"))
							.append("registrationDate", new BasicDBObject("$first", "$registrationDate"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.group("$patientId").max("$visitedTime").as("visitedTime"),
						new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("visitedTime", -1))),
						Aggregation.skip(page * size), Aggregation.limit(size),

						Aggregation.lookup("patient_cl", "_id", "userId", "patient"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),

						redactOperations, Aggregation.lookup("user_cl", "_id", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
						projectOperations, groupOperations, new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("visitedTime", -1))));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.group("$patientId").max("$visitedTime").as("visitedTime"),
						new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("visitedTime", -1))),
						Aggregation.lookup("patient_cl", "_id", "userId", "patient"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),

						redactOperations, Aggregation.lookup("user_cl", "_id", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
						projectOperations, groupOperations, new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("visitedTime", -1))));
			}

			List<PatientCard> patientCards = mongoTemplate
					.aggregate(aggregation, PatientVisitCollection.class, PatientCard.class).getMappedResults();

			if (patientCards != null) {
				for (PatientCard patientCard : patientCards) {
					patientCard.setColorCode(patientCard.getUser().getColorCode());
					patientCard.setMobileNumber(patientCard.getUser().getMobileNumber());
					patientCard.setDoctorSepecificPatientId(patientCard.getUserId().toString());
					patientCard.setId(patientCard.getUserId());
					patientCard.setUser(null);
				}
				response = new DoctorContactsResponse();
				response.setPatientCards(patientCards);

				List<PatientVisitCollection> totalPatients = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.group("patientId")),
						PatientVisitCollection.class, PatientVisitCollection.class).getMappedResults();

				response.setTotalSize(totalPatients.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while recently visited patients record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting recently visited patients record : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, int page, int size,
			String role) {
		DoctorContactsResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
					.and("isPatientDiscarded").ne(true);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria2.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);

			CustomAggregationOperation redactOperations = null;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(role)) {
					redactOperations = new CustomAggregationOperation(new Document("$redact",
							new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$and", Arrays.asList(
									new BasicDBObject("$eq", Arrays.asList("$patient.locationId", locationObjectId)),
									new BasicDBObject("$eq", Arrays.asList("$patient.hospitalId", hospitalObjectId)),
									new BasicDBObject("$eq",
											Arrays.asList("$patient.consultantDoctorIds", doctorObjectId)))))
													.append("then", "$$KEEP").append("else", "$$PRUNE"))));
					criteria2.and("consultantDoctorIds").is(doctorObjectId);
				} else {
					redactOperations = new CustomAggregationOperation(new Document("$redact",
							new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$and", Arrays.asList(
									new BasicDBObject("$eq", Arrays.asList("$patient.locationId", locationObjectId)),
									new BasicDBObject("$eq", Arrays.asList("$patient.hospitalId", hospitalObjectId)),
									new BasicDBObject("$eq", Arrays.asList("$patient.doctorId", doctorObjectId)))))
											.append("then", "$$KEEP").append("else", "$$PRUNE"))));
					criteria2.and("doctorId").is(doctorObjectId);
				}
			} else {
				redactOperations = new CustomAggregationOperation(new Document("$redact",
						new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$and", Arrays.asList(
								new BasicDBObject("$eq", Arrays.asList("$patient.locationId", locationObjectId)),
								new BasicDBObject("$eq", Arrays.asList("$patient.hospitalId", hospitalObjectId)))))
										.append("then", "$$KEEP").append("else", "$$PRUNE"))));
			}

			CustomAggregationOperation projectOperations = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("patientId", "$patientId").append("userId", "$patient.userId")
							.append("firstName", "$patient.firstName")
							.append("localPatientName", "$patient.localPatientName")
							.append("userName", "$patient.userName").append("emailAddress", "$patient.emailAddress")
							.append("imageUrl", "$patient.imageUrl").append("thumbnailUrl", "$patient.thumbnailUrl")
							.append("bloodGroup", "$patient.bloodGroup").append("PID", "$patient.PID")
							.append("gender", "$patient.gender").append("countryCode", "$patient.countryCode")
							.append("mobileNumber", "$patient.mobileNumber")
							.append("secPhoneNumber", "$patient.secPhoneNumber").append("dob", "$patient.dob")
							.append("dateOfVisit", "$patient.dateOfVisit").append("doctorId", "$patient.doctorId")
							.append("locationId", "$patient.locationId").append("hospitalId", "$patient.hospitalId")
							.append("colorCode", "$user.colorCode").append("user", "$user")
							.append("address", "$patient.address").append("patientId", "$patient.userId")
							.append("profession", "$patient.profession").append("relations", "$patient.relations")
							.append("consultantDoctorIds", "$patient.consultantDoctorIds")
							.append("registrationDate", "$patient.registrationDate")
							.append("createdTime", "$patient.createdTime").append("updatedTime", "$patient.updatedTime")
							.append("createdBy", "$patient.createdBy").append("count", "$count")));

			CustomAggregationOperation groupOperations = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("patientId", "$patientId"))
							.append("userId", new BasicDBObject("$first", "$userId"))
							.append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("localPatientName", new BasicDBObject("$first", "$localPatientName"))
							.append("userName", new BasicDBObject("$first", "$userName"))
							.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
							.append("imageUrl", new BasicDBObject("$first", "$imageUrl"))
							.append("thumbnailUrl", new BasicDBObject("$first", "$thumbnailUrl"))
							.append("bloodGroup", new BasicDBObject("$first", "$bloodGroup"))
							.append("PID", new BasicDBObject("$first", "$PID"))
							.append("gender", new BasicDBObject("$first", "$gender"))
							.append("countryCode", new BasicDBObject("$first", "$countryCode"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("secPhoneNumber", new BasicDBObject("$first", "$secPhoneNumber"))
							.append("dob", new BasicDBObject("$first", "$dob"))
							.append("dateOfVisit", new BasicDBObject("$first", "$dateOfVisit"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("colorCode", new BasicDBObject("$first", "$user.colorCode"))
							.append("user", new BasicDBObject("$first", "$user"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("patientId", new BasicDBObject("$first", "$userId"))
							.append("profession", new BasicDBObject("$first", "$profession"))
							.append("relations", new BasicDBObject("$first", "$relations"))
							.append("consultantDoctorIds", new BasicDBObject("$first", "$consultantDoctorIds"))
							.append("registrationDate", new BasicDBObject("$first", "$registrationDate"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							.append("count", new BasicDBObject("$first", "$count"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.group("$patientId").count().as("count"),
						// Aggregation.project("total").and("patientId").previousOperation(),
						new CustomAggregationOperation(new Document("$sort", new BasicDBObject("count", -1))),
						Aggregation.skip(page * size), Aggregation.limit(size),

						Aggregation.lookup("patient_cl", "_id", "userId", "patient"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),

						redactOperations, Aggregation.lookup("user_cl", "_id", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
						projectOperations, groupOperations,
						new CustomAggregationOperation(new Document("$sort", new BasicDBObject("count", -1))));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.group("$patientId").count().as("count"),
						new CustomAggregationOperation(new Document("$sort", new BasicDBObject("count", -1))),
						Aggregation.lookup("patient_cl", "_id", "userId", "patient"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),

						redactOperations, Aggregation.lookup("user_cl", "_id", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
						projectOperations, groupOperations,
						new CustomAggregationOperation(new Document("$sort", new BasicDBObject("count", -1))));
			}

			List<PatientCard> patientCards = mongoTemplate
					.aggregate(aggregation, PatientVisitCollection.class, PatientCard.class).getMappedResults();

			if (patientCards != null) {
				for (PatientCard patientCard : patientCards) {
					patientCard.setColorCode(patientCard.getUser().getColorCode());
					patientCard.setMobileNumber(patientCard.getUser().getMobileNumber());
					patientCard.setDoctorSepecificPatientId(patientCard.getUserId().toString());
					patientCard.setId(patientCard.getUserId());
					patientCard.setUser(null);
				}
				response = new DoctorContactsResponse();
				response.setPatientCards(patientCards);

				List<PatientVisitCollection> totalPatients = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.group("patientId")),
						PatientVisitCollection.class, PatientVisitCollection.class).getMappedResults();

				response.setTotalSize(totalPatients.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting most visited patients record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting most visited patients record : " + e.getCause().getMessage());
		}
		return response;
	}

	

	

	@Override
	@Transactional
	public List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, Boolean isOTPVerified, String updatedTime, String visitFor, Boolean discarded) {
		List<PatientVisitResponse> response = null;
		List<PatientVisitLookupBean> patientVisitlookupbeans = null;
		try {
			List<VisitedFor> visitedFors = new ArrayList<VisitedFor>();
			if (visitFor == VisitedFor.ALL.toString() || visitFor == null) {
				visitedFors.add(CLINICAL_NOTES);
				visitedFors.add(PRESCRIPTION);
				visitedFors.add(REPORTS);
			} else if (visitFor.equalsIgnoreCase(VisitedFor.TREATMENT.getVisitedFor())) {
				visitedFors.add(CLINICAL_NOTES);
				visitedFors.add(PRESCRIPTION);
				visitedFors.add(REPORTS);
				visitedFors.add(VisitedFor.TREATMENT);
			} else if (visitFor.equalsIgnoreCase("WEB")) {
				visitedFors.add(CLINICAL_NOTES);
				visitedFors.add(PRESCRIPTION);
				visitedFors.add(VisitedFor.TREATMENT);
				visitedFors.add(VisitedFor.EYE_PRESCRIPTION);
			} else {
				visitedFors.add(VisitedFor.valueOf(visitFor.toUpperCase()));
			}

			long createdTimestamp = Long.parseLong(updatedTime);
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId).and("visitedFor").in(visitedFors).and("isPatientDiscarded").ne(true);

			if (discarded !=null)
				criteria.and("discarded").is(discarded);
			
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
			}
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),

						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),

						new CustomAggregationOperation(
								new Document("$unwind",
										new BasicDBObject("path", "$appointmentRequest")
												.append("preserveNullAndEmptyArrays", true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<PatientVisitLookupBean> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientVisitCollection.class, PatientVisitLookupBean.class);
			patientVisitlookupbeans = aggregationResults.getMappedResults();
			if (patientVisitlookupbeans != null) {
				response = new ArrayList<PatientVisitResponse>();

				for (PatientVisitLookupBean patientVisitlookupBean : patientVisitlookupbeans) {
					PatientVisitResponse patientVisitResponse = new PatientVisitResponse();
					BeanUtil.map(patientVisitlookupBean, patientVisitResponse);

					if (patientVisitlookupBean.getPrescriptionId() != null) {
						List<Prescription> prescriptions = prescriptionServices.getPrescriptionsByIdsForEMR(
								patientVisitlookupBean.getPrescriptionId(), patientVisitlookupBean.getId());
						patientVisitResponse.setPrescriptions(prescriptions);
					}

					if (patientVisitlookupBean.getClinicalNotesId() != null) {
						List<ClinicalNotes> clinicalNotes = new ArrayList<ClinicalNotes>();
						for (ObjectId clinicalNotesId : patientVisitlookupBean.getClinicalNotesId()) {
							ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(clinicalNotesId.toString(),
									patientVisitlookupBean.getId());
							if (clinicalNote != null) {
								if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
									clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
								}
								clinicalNotes.add(clinicalNote);
							}
						}
						patientVisitResponse.setClinicalNotes(clinicalNotes);
					}

					if (patientVisitlookupBean.getRecordId() != null) {
						List<Records> records = recordsService.getRecordsByIds(patientVisitlookupBean.getRecordId(),
								patientVisitlookupBean.getId());
						patientVisitResponse.setRecords(records);
					}

					if (patientVisitlookupBean.getTreatmentId() != null) {
						List<PatientTreatment> patientTreatment = patientTreatmentServices.getPatientTreatmentByIds(
								patientVisitlookupBean.getTreatmentId(), patientVisitlookupBean.getId());
						patientVisitResponse.setPatientTreatment(patientTreatment);
					}

					if (patientVisitlookupBean.getEyePrescriptionId() != null) {
						EyePrescription eyePrescription = prescriptionServices
								.getEyePrescription(String.valueOf(patientVisitlookupBean.getEyePrescriptionId()));
						patientVisitResponse.setEyePrescription(eyePrescription);

					}
					response.add(patientVisitResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while geting patient Visit : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while geting patient Visit : " + e.getCause().getMessage());
		}
		return response;
	}

	private AggregationOperation prescriptionFirstProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
				.append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId").append("doctorId", "$doctorId")
				.append("locationId", "$locationId").append("hospitalId", "$hospitalId")
				.append("visitedTime", "$visitedTime").append("visitedFor", "$visitedFor")
				.append("clinicalNotesId", "$clinicalNotesId").append("treatmentId", "$treatmentId")
				.append("recordId", "$recordId").append("eyePrescriptionId", "$eyePrescriptionId")
				.append("appointmentId", "$appointmentId").append("time", "$time").append("fromDate", "$fromDate")
				.append("discarded", "$discarded").append("appointmentRequest", "$appointmentRequest")
				.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
				.append("createdBy", "$createdBy").append("prescriptionsid", "$prescriptions._id")
				.append("prescriptionsname", "$prescriptions.name")
				.append("prescriptionsuniqueEmrId", "$prescriptions.uniqueEmrId")
				.append("prescriptionslocationId", "$prescriptions.locationId")
				.append("prescriptionshospitalId", "$prescriptions.hospitalId")
				.append("prescriptionsdoctorId", "$prescriptions.doctorId")
				.append("prescriptionsdiscarded", "$prescriptions.discarded")
				.append("prescriptionsinHistory", "$prescriptions.inHistory")
				.append("prescriptionsadvice", "$prescriptions.advice")
				.append("prescriptionstime", "$prescriptions.time")
				.append("prescriptionsfromDate", "$prescriptions.fromDate")
				.append("prescriptionspatientId", "$prescriptions.patientId")
				.append("prescriptionsisFeedbackAvailable", "$prescriptions.isFeedbackAvailable")
				.append("prescriptionsappointmentId", "$prescriptions.appointmentId")
				.append("prescriptionsvisitId", "$_id").append("prescriptionscreatedTime", "$prescriptions.createdTime")
				.append("prescriptionscreatedBy", "$prescriptions.createdBy")
				.append("prescriptionsupdatedTime", "$prescriptions.updatedTime")
				.append("prescriptionsitems.drug", "$drug")
				.append("prescriptionsitems.duration", "$prescriptions.items.duration")
				.append("prescriptionsitems.durationValue", "$prescriptions.items.duration.value")
				.append("prescriptionsitems.durationUnit", "$prescriptions.items.duration.durationUnit.unit")
				.append("prescriptionsitems.drTy", "$prescriptions.items.duration.durationUnit.drugType.type")
				.append("prescriptionsitems.dosage", "$prescriptions.items.dosage")
				.append("prescriptionsitems.dosageTime", "$prescriptions.items.dosageTime")
				.append("prescriptionsitems.direction", "$prescriptions.items.direction")
				.append("prescriptionsitems.instructions", "$prescriptions.items.instructions")
				.append("prescriptionsitems.inventoryQuantity", "$prescriptions.items.inventoryQuantity")

				.append("prescriptionsDiagnosticTestsObject", "$prescriptions.diagnosticTests")));
	}

	private AggregationOperation prescriptionGroupAggregationOperationForDrugs() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", new BasicDBObject("_id", "$_id"))
						.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("prescriptionsid", new BasicDBObject("$first", "$prescriptionsid"))
						.append("prescriptionsname", new BasicDBObject("$first", "$prescriptionsname"))
						.append("prescriptionsuniqueEmrId", new BasicDBObject("$first", "$prescriptionsuniqueEmrId"))
						.append("prescriptionslocationId", new BasicDBObject("$first", "$prescriptionslocationId"))
						.append("prescriptionshospitalId", new BasicDBObject("$first", "$prescriptionshospitalId"))
						.append("prescriptionsdoctorId", new BasicDBObject("$first", "$prescriptionsdoctorId"))
						.append("prescriptionsdiscarded", new BasicDBObject("$first", "$prescriptionsdiscarded"))
						.append("prescriptionsinHistory", new BasicDBObject("$first", "$prescriptionsinHistory"))
						.append("prescriptionsadvice", new BasicDBObject("$first", "$prescriptionsadvice"))
						.append("prescriptionstime", new BasicDBObject("$first", "$prescriptionstime"))
						.append("prescriptionsfromDate", new BasicDBObject("$first", "$prescriptionsfromDate"))
						.append("prescriptionspatientId", new BasicDBObject("$first", "$prescriptionspatientId"))
						.append("prescriptionsisFeedbackAvailable",
								new BasicDBObject("$first", "$prescriptionsisFeedbackAvailable"))
						.append("prescriptionsappointmentId",
								new BasicDBObject("$first", "$prescriptionsappointmentId"))
						.append("prescriptionsvisitId", new BasicDBObject("$first", "$prescriptionsvisitId"))
						.append("prescriptionscreatedTime", new BasicDBObject("$first", "$prescriptionscreatedTime"))
						.append("prescriptionscreatedBy", new BasicDBObject("$first", "$prescriptionscreatedBy"))
						.append("prescriptionsupdatedTime", new BasicDBObject("$first", "$prescriptionsupdatedTime"))
						.append("prescriptionItems", new BasicDBObject("$push", "$prescriptionsitems"))
						.append("prescriptionsDiagnosticTestsObject",
								new BasicDBObject("$first", "$prescriptionsDiagnosticTestsObject"))
						.append("clinicalNotesId", new BasicDBObject("$first", "$clinicalNotesId"))
						.append("treatmentId", new BasicDBObject("$first", "$treatmentId"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation prescriptionProjectAggregationOperationForDiagnosticTests() {
		return new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
				.append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId").append("doctorId", "$doctorId")
				.append("locationId", "$locationId").append("hospitalId", "$hospitalId")
				.append("visitedTime", "$visitedTime").append("visitedFor", "$visitedFor")
				.append("clinicalNotesId", "$clinicalNotesId").append("treatmentId", "$treatmentId")
				.append("recordId", "$recordId").append("eyePrescriptionId", "$eyePrescriptionId")
				.append("appointmentId", "$appointmentId").append("time", "$time").append("fromDate", "$fromDate")
				.append("discarded", "$discarded").append("appointmentRequest", "$appointmentRequest")
				.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
				.append("createdBy", "$createdBy").append("prescriptionsid", "$prescriptions._id")
				.append("prescriptionsname", "$prescriptionsname")
				.append("prescriptionsuniqueEmrId", "$prescriptionsuniqueEmrId")
				.append("prescriptionslocationId", "$prescriptionslocationId")
				.append("prescriptionshospitalId", "$prescriptionshospitalId")
				.append("prescriptionsdoctorId", "$prescriptionsdoctorId")
				.append("prescriptionsdiscarded", "$prescriptionsdiscarded")
				.append("prescriptionsinHistory", "$prescriptionsinHistory")
				.append("prescriptionsadvice", "$prescriptionsadvice").append("prescriptionstime", "$prescriptionstime")
				.append("prescriptionsfromDate", "$prescriptionsfromDate")
				.append("prescriptionspatientId", "$prescriptionspatientId")
				.append("prescriptionsisFeedbackAvailable", "$prescriptionsisFeedbackAvailable")
				.append("prescriptionsappointmentId", "$prescriptionsappointmentId")
				.append("prescriptionsvisitId", "$prescriptionsvisitId")
				.append("prescriptionscreatedTime", "$prescriptionscreatedTime")
				.append("prescriptionscreatedBy", "$prescriptionscreatedBy")
				.append("prescriptionsupdatedTime", "$prescriptionsupdatedTime")
				.append("prescriptionsitems", "$prescriptionItems")

				.append("prescriptionsdiagnosticTests.test", "$diagnosticTests")
				.append("prescriptionsdiagnosticTests.recordId", "$prescriptions.diagnosticTests.recordId")));
	}

	private AggregationOperation prescriptionFirstGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", new BasicDBObject("_id", "$_id"))
						.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("prescriptionsid", new BasicDBObject("$first", "$prescriptionsid"))
						.append("prescriptionsname", new BasicDBObject("$first", "$prescriptionsname"))
						.append("prescriptionsuniqueEmrId", new BasicDBObject("$first", "$prescriptionsuniqueEmrId"))
						.append("prescriptionslocationId", new BasicDBObject("$first", "$prescriptionslocationId"))
						.append("prescriptionshospitalId", new BasicDBObject("$first", "$prescriptionshospitalId"))
						.append("prescriptionsdoctorId", new BasicDBObject("$first", "$prescriptionsdoctorId"))
						.append("prescriptionsdiscarded", new BasicDBObject("$first", "$prescriptionsdiscarded"))
						.append("prescriptionsinHistory", new BasicDBObject("$first", "$prescriptionsinHistory"))
						.append("prescriptionsadvice", new BasicDBObject("$first", "$prescriptionsadvice"))
						.append("prescriptionstime", new BasicDBObject("$first", "$prescriptionstime"))
						.append("prescriptionsfromDate", new BasicDBObject("$first", "$prescriptionsfromDate"))
						.append("prescriptionspatientId", new BasicDBObject("$first", "$prescriptionspatientId"))
						.append("prescriptionsisFeedbackAvailable",
								new BasicDBObject("$first", "$prescriptionsisFeedbackAvailable"))
						.append("prescriptionsappointmentId",
								new BasicDBObject("$first", "$prescriptionsappointmentId"))
						.append("prescriptionsvisitId", new BasicDBObject("$first", "$prescriptionsvisitId"))
						.append("prescriptionscreatedTime", new BasicDBObject("$first", "$prescriptionscreatedTime"))
						.append("prescriptionscreatedBy", new BasicDBObject("$first", "$prescriptionscreatedBy"))
						.append("prescriptionsupdatedTime", new BasicDBObject("$first", "$prescriptionsupdatedTime"))

						.append("prescriptionItems", new BasicDBObject("$first", "$prescriptionsitems"))
						.append("prescriptionsdiagnosticTests",
								new BasicDBObject("$push", "$prescriptionsdiagnosticTests"))
						.append("clinicalNotesId", new BasicDBObject("$first", "$clinicalNotesId"))
						.append("treatmentId", new BasicDBObject("$first", "$treatmentId"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation prescriptionSecondProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId")
						.append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("visitedTime", "$visitedTime")
						.append("visitedFor", "$visitedFor").append("clinicalNotesId", "$clinicalNotesId")
						.append("treatmentId", "$treatmentId").append("recordId", "$recordId")
						.append("eyePrescriptionId", "$eyePrescriptionId").append("appointmentId", "$appointmentId")
						.append("time", "$time").append("fromDate", "$fromDate").append("discarded", "$discarded")
						.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("prescriptions._id", "$prescriptionsid")
						.append("prescriptions.name", "$prescriptionsname")
						.append("prescriptions.uniqueEmrId", "$prescriptionsuniqueEmrId")
						.append("prescriptions.locationId", "$prescriptionslocationId")
						.append("prescriptions.hospitalId", "$prescriptionshospitalId")
						.append("prescriptions.doctorId", "$prescriptionsdoctorId")
						.append("prescriptions.discarded", "$prescriptionsdiscarded")
						.append("prescriptions.inHistory", "$prescriptionsinHistory")
						.append("prescriptions.advice", "$prescriptionsadvice")
						.append("prescriptions.time", "$prescriptionstime")
						.append("prescriptions.fromDate", "$prescriptionsfromDate")
						.append("prescriptions.patientId", "$prescriptionspatientId")
						.append("prescriptions.isFeedbackAvailable", "$prescriptionsisFeedbackAvailable")
						.append("prescriptions.appointmentId", "$prescriptionsappointmentId")
						.append("prescriptions.visitId", "$prescriptionsvisitId")
						.append("prescriptions.createdTime", "$prescriptionscreatedTime")
						.append("prescriptions.createdBy", "$prescriptionscreatedBy")
						.append("prescriptions.updatedTime", "$prescriptionsupdatedTime")
						.append("prescriptions.items", "$prescriptionItems")
						.append("prescriptions.diagnosticTests", "$prescriptionDiagnosticTests")));
	}

	private AggregationOperation prescriptionSecondGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("prescriptions", new BasicDBObject("$push", "$prescriptions"))
						.append("clinicalNotesId", new BasicDBObject("$first", "$clinicalNotesId"))
						.append("treatmentId", new BasicDBObject("$first", "$treatmentId"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation clinicalNotesFirstProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
				.append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId").append("doctorId", "$doctorId")
				.append("locationId", "$locationId").append("hospitalId", "$hospitalId")
				.append("visitedTime", "$visitedTime").append("visitedFor", "$visitedFor")
				.append("treatmentId", "$treatmentId").append("recordId", "$recordId")
				.append("eyePrescriptionId", "$eyePrescriptionId").append("appointmentId", "$appointmentId")
				.append("time", "$time").append("fromDate", "$fromDate").append("discarded", "$discarded")
				.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
				.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
				.append("prescriptions", "$prescriptions").append("clinicalNotes", "$clinicalNotes")
				.append("clinicalNotesid", "$clinicalNotes._id").append("clinicalNotesDiagrams._id", "$diagrams._id")
				.append("clinicalNotesDiagrams.diagramUrl", "$diagrams.diagramUrl")
				.append("clinicalNotesDiagrams.tags", "$diagrams.tags")
				.append("clinicalNotesDiagrams.doctorId", "$diagrams.doctorId")
				.append("clinicalNotesDiagrams.locationId", "$diagrams.locationId")
				.append("clinicalNotesDiagrams.hospitalId", "$diagrams.hospitalId")
				.append("clinicalNotesDiagrams.fileExtension", "$diagrams.fileExtension")
				.append("clinicalNotesDiagrams.discarded", "$diagrams.discarded")
				.append("clinicalNotesDiagrams.speciality", "$diagrams.speciality")
				.append("clinicalNotesDiagrams.clinicalNotesId", "$clinicalNotes._id")));
	}

	private AggregationOperation clinicalNotesFirstGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("prescriptions", new BasicDBObject("$first", "$prescriptions"))
						.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
						.append("clinicalNotesDiagrams", new BasicDBObject("$push", "$clinicalNotesDiagrams"))
						.append("treatmentId", new BasicDBObject("$first", "$treatmentId"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation clinicalNotesSecondProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId")
						.append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("visitedTime", "$visitedTime")
						.append("visitedFor", "$visitedFor").append("treatmentId", "$treatmentId")
						.append("recordId", "$recordId").append("eyePrescriptionId", "$eyePrescriptionId")
						.append("appointmentId", "$appointmentId").append("time", "$time")
						.append("fromDate", "$fromDate").append("discarded", "$discarded")
						.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("prescriptions", "$prescriptions").append("clinicalNotes", "$clinicalNotes")
						.append("clinicalNotesDiagrams", "$clinicalNotesDiagrams")));
	}

	private AggregationOperation clinicalNotesSecondGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("prescriptions", new BasicDBObject("$first", "$prescriptions"))
						.append("clinicalNotes", new BasicDBObject("$push", "$clinicalNotes"))
						.append("clinicalNotesDiagrams", new BasicDBObject("$first", "$clinicalNotesDiagrams"))
						.append("treatmentId", new BasicDBObject("$first", "$treatmentId"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation patientTreatmentFirstProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId")
						.append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("visitedTime", "$visitedTime")
						.append("visitedFor", "$visitedFor").append("prescriptions", "$prescriptions")
						.append("clinicalNotes", "$clinicalNotes")
						.append("clinicalNotesDiagrams", "$clinicalNotesDiagrams").append("recordId", "$recordId")
						.append("eyePrescriptionId", "$eyePrescriptionId").append("appointmentId", "$appointmentId")
						.append("time", "$time").append("fromDate", "$fromDate").append("discarded", "$discarded")
						.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("patientTreatmentid", "$patientTreatment._id")
						.append("patientTreatmentuniqueEmrId", "$patientTreatment.uniqueEmrId")
						.append("patientTreatmentlocationId", "$patientTreatment.locationId")
						.append("patientTreatmenthospitalId", "$patientTreatment.hospitalId")
						.append("patientTreatmentdoctorId", "$patientTreatment.doctorId")
						.append("patientTreatmentdiscarded", "$patientTreatment.discarded")
						.append("patientTreatmentinHistory", "$patientTreatment.inHistory")
						.append("patientTreatmenttotalCost", "$patientTreatment.totalCost")
						.append("patientTreatmenttime", "$patientTreatment.time")
						.append("patientTreatmentfromDate", "$patientTreatment.fromDate")
						.append("patientTreatmentpatientId", "$patientTreatment.patientId")
						.append("patientTreatmenttotalCost", "$patientTreatment.totalCost")
						.append("patientTreatmenttotalDiscount", "$patientTreatment.totalDiscount")
						.append("patientTreatmenttotalgrandTotal", "$patientTreatment.grandTotal")
						.append("patientTreatmentappointmentId", "$patientTreatment.appointmentId")
						.append("patientTreatmentvisitId", "$_id.id")
						.append("patientTreatmentcreatedTime", "$patientTreatment.createdTime")
						.append("patientTreatmentcreatedBy", "$patientTreatment.createdBy")
						.append("patientTreatmentupdatedTime", "$patientTreatment.updatedTime")
						.append("treatments.treatmentService", "$treatmentService")
						.append("treatments.treatmentServiceId", "$patientTreatment.treatments.treatmentServiceId")
						.append("treatments.status", "$patientTreatment.treatments.status")
						.append("treatments.cost", "$patientTreatment.treatments.cost")
						.append("treatments.note", "$patientTreatment.treatments.note")
						.append("treatments.discount", "$patientTreatment.treatments.discount")
						.append("treatments.finalCost", "$patientTreatment.treatments.finalCost")
						.append("treatments.quantity", "$patientTreatment.treatments.quantity")
						.append("treatments.treatmentFields", "$patientTreatment.treatments.treatmentFields")));
	}

	private AggregationOperation patientTreatmentFirstGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
				.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
				.append("patientId", new BasicDBObject("$first", "$patientId"))
				.append("locationId", new BasicDBObject("$first", "$locationId"))
				.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
				.append("doctorId", new BasicDBObject("$first", "$doctorId"))
				.append("discarded", new BasicDBObject("$first", "$discarded"))
				.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
				.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
				.append("prescriptions", new BasicDBObject("$first", "$prescriptions"))
				.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
				.append("clinicalNotesDiagrams", new BasicDBObject("$first", "$clinicalNotesDiagrams"))
				.append("patientTreatmentid", new BasicDBObject("$first", "$patientTreatmentid"))
				.append("patientTreatmentuniqueEmrId", new BasicDBObject("$first", "$patientTreatmentuniqueEmrId"))
				.append("patientTreatmentlocationId", new BasicDBObject("$first", "$patientTreatmentlocationId"))
				.append("patientTreatmenthospitalId", new BasicDBObject("$first", "$patientTreatmenthospitalId"))
				.append("patientTreatmentdoctorId", new BasicDBObject("$first", "$patientTreatmentdoctorId"))
				.append("patientTreatmentdiscarded", new BasicDBObject("$first", "$patientTreatmentdiscarded"))
				.append("patientTreatmentinHistory", new BasicDBObject("$first", "$patientTreatmentinHistory"))
				.append("patientTreatmenttotalCost", new BasicDBObject("$first", "$patientTreatmenttotalCost"))
				.append("patientTreatmenttime", new BasicDBObject("$first", "$patientTreatmenttime"))
				.append("patientTreatmentfromDate", new BasicDBObject("$first", "$patientTreatmentfromDate"))
				.append("patientTreatmentpatientId", new BasicDBObject("$first", "$patientTreatmentpatientId"))
				.append("patientTreatmenttotalCost", new BasicDBObject("$first", "$patientTreatmenttotalCost"))
				.append("patientTreatmenttotalDiscount", new BasicDBObject("$first", "$patientTreatmenttotalDiscount"))
				.append("patientTreatmenttotalgrandTotal", new BasicDBObject("$first", "$patientTreatmentgrandTotal"))
				.append("patientTreatmentappointmentId", new BasicDBObject("$first", "$patientTreatmentappointmentId"))
				.append("patientTreatmentvisitId", new BasicDBObject("$first", "$patientTreatmentvisitId"))
				.append("patientTreatmentcreatedTime", new BasicDBObject("$first", "$patientTreatmentcreatedTime"))
				.append("patientTreatmentcreatedBy", new BasicDBObject("$first", "$patientTreatmentcreatedBy"))
				.append("patientTreatmentupdatedTime", new BasicDBObject("$first", "$patientTreatmentupdatedTime"))
				.append("treatments", new BasicDBObject("$push", "$treatments"))
				.append("recordId", new BasicDBObject("$first", "$recordId"))
				.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
				.append("fromDate", new BasicDBObject("$first", "$fromDate"))
				.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
				.append("createdTime", new BasicDBObject("$first", "$createdTime"))
				.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
				.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation patientTreatmentSecondProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId")
						.append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("visitedTime", "$visitedTime")
						.append("visitedFor", "$visitedFor").append("prescriptions", "$prescriptions")
						.append("clinicalNotes", "$clinicalNotes")
						.append("clinicalNotesDiagrams", "$clinicalNotesDiagrams").append("recordId", "$recordId")
						.append("eyePrescriptionId", "$eyePrescriptionId").append("appointmentId", "$appointmentId")
						.append("time", "$time").append("fromDate", "$fromDate").append("discarded", "$discarded")
						.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("patientTreatment._id", "$patientTreatmentid")
						.append("patientTreatment.uniqueEmrId", "$patientTreatmentuniqueEmrId")
						.append("patientTreatment.locationId", "$patientTreatmentlocationId")
						.append("patientTreatment.hospitalId", "$patientTreatmenthospitalId")
						.append("patientTreatment.doctorId", "$patientTreatmentdoctorId")
						.append("patientTreatment.discarded", "$patientTreatmentdiscarded")
						.append("patientTreatment.inHistory", "$patientTreatmentinHistory")
						.append("patientTreatment.totalCost", "$patientTreatmenttotalCost")
						.append("patientTreatment.time", "$patientTreatmenttime")
						.append("patientTreatment.fromDate", "$patientTreatmentfromDate")
						.append("patientTreatment.patientId", "$patientTreatmentpatientId")
						.append("patientTreatment.totalCost", "$patientTreatmenttotalCost")
						.append("patientTreatment.totalDiscount", "$patientTreatmenttotalDiscount")
						.append("patientTreatment.totalgrandTotal", "$patientTreatmentgrandTotal")
						.append("patientTreatment.appointmentId", "$patientTreatmentappointmentId")
						.append("patientTreatment.visitId", "$patientTreatmentvisitId")
						.append("patientTreatment.createdTime", "$patientTreatmentcreatedTime")
						.append("patientTreatment.createdBy", "$patientTreatmentcreatedBy")
						.append("patientTreatment.updatedTime", "$patientTreatmentupdatedTime")
						.append("patientTreatment.treatments", "$treatments")));
	}

	private AggregationOperation patientTreatmentSecondGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("prescriptions", new BasicDBObject("$first", "$prescriptions"))
						.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
						.append("clinicalNotesDiagrams", new BasicDBObject("$first", "$clinicalNotesDiagrams"))
						.append("patientTreatment", new BasicDBObject("$push", "$patientTreatment"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation recordsProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
				.append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId").append("doctorId", "$doctorId")
				.append("locationId", "$locationId").append("hospitalId", "$hospitalId")
				.append("visitedTime", "$visitedTime").append("visitedFor", "$visitedFor")
				.append("patientTreatment", "$patientTreatment").append("records._id", "$records._id")
				.append("records.visitId", "$_id").append("records.uniqueEmrId", "$records.uniqueEmrId")
				.append("records.doctorId", "$records.doctorId").append("records.locationId", "$records.locationId")
				.append("records.hospitalId", "$records.hospitalId").append("records.patientId", "$records.patientId")
				.append("records.recordsUrl", "$records.recordsUrl")
				.append("records.recordsLabel", "$records.recordsLabel")
				.append("records.recordsType", "$records.recordsType")
				.append("records.shareWithPatient", "$records.shareWithPatient")
				.append("records.recordsState", "$records.recordsState")
				.append("records.isFeedbackAvailable", "$records.isFeedbackAvailable")
				.append("records.diagnosticTestId", "$records.diagnosticTestId")
				.append("records.prescribedByHospitalId", "$records.prescribedByHospitalId")
				.append("records.prescribedByLocationId", "$records.prescribedByLocationId")
				.append("records.prescribedByDoctorId", "$records.prescribedByDoctorId")
				.append("records.prescriptionId", "$records.prescriptionId")
				.append("records.inHistory", "$records.inHistory")
				.append("records.uploadedByLocation", "$records.uploadedByLocation")
				.append("records.discarded", "$records.discarded").append("records.explanation", "$records.explanation")
				.append("eyePrescriptionId", "$eyePrescriptionId").append("appointmentId", "$appointmentId")
				.append("time", "$time").append("fromDate", "$fromDate").append("discarded", "$discarded")
				.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
				.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
				.append("prescriptions", "$prescriptions").append("clinicalNotes", "$clinicalNotes")
				.append("clinicalNotesDiagrams", "$clinicalNotesDiagrams")));
	}

	private AggregationOperation recordsGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("prescriptions", new BasicDBObject("$first", "$prescriptions"))
						.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
						.append("clinicalNotesDiagrams", new BasicDBObject("$first", "$clinicalNotesDiagrams"))
						.append("patientTreatment", new BasicDBObject("$first", "$patientTreatment"))
						.append("records", new BasicDBObject("$push", "$records"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	private List<Diagram> getFinalDiagrams(List<Diagram> diagrams) {
		for (Diagram diagram : diagrams) {
			if (diagram.getDiagramUrl() != null) {
				diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
			}
		}
		return diagrams;
	}


	
}
