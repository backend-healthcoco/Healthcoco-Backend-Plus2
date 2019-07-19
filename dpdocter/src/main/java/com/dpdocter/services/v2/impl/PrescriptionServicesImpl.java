package com.dpdocter.services.v2.impl;

import java.util.ArrayList;
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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.EyePrescription;
import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.v2.DiagnosticTest;
import com.dpdocter.beans.v2.Drug;
import com.dpdocter.beans.v2.Prescription;
import com.dpdocter.beans.v2.PrescriptionItemDetail;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EyePrescriptionCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.EyePrescriptionRepository;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.response.PrescriptionInventoryBatchResponse;
import com.dpdocter.response.v2.TestAndRecordDataResponse;
import com.dpdocter.services.InventoryService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.v2.PrescriptionServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service(value = "PrescriptionServicesImplV2")
public class PrescriptionServicesImpl implements PrescriptionServices {

	private static Logger logger = Logger.getLogger(PrescriptionServicesImpl.class.getName());

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private EyePrescriptionRepository eyePrescriptionRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${Prescription.checkPrescriptionExists}")
	private String checkPrescriptionExists;

	@Value(value = "${jasper.print.prescription.a4.fileName}")
	private String prescriptionA4FileName;

	@Value(value = "${jasper.print.prescription.subreport.a4.fileName}")
	private String prescriptionSubReportA4FileName;

	@Value(value = "${jasper.print.prescription.a5.fileName}")
	private String prescriptionA5FileName;

	@Value(value = "${jasper.print.prescription.subreport.a5.fileName}")
	private String prescriptionSubReportA5FileName;

	@Value(value = "${prescription.add.patient.download.app.message}")
	private String downloadAppMessageToPatient;

	@Value(value = "${prescription.add.patient.download.app.message.hindi}")
	private String downloadAppMessageToPatientInHindi;

	@Value("${send.sms}")
	private Boolean sendSMS;

	@Value(value = "${update.generic.codes.data.file}")
	private String UPDATE_GENERIC_CODES_DATA_FILE;

	@Value(value = "${drug.company.data.file}")
	private String DRUG_COMPANY_LIST;

	@Value(value = "${upload.drugs.file}")
	private String UPLOAD_DRUGS;

	@Value(value = "${update.drug.interaction.file}")
	private String UPDATE_DRUG_INTERACTION_DATA_FILE;
	
	@Autowired
	private DrugRepository drugRepository;

	/*
	 * LoadingCache<String, List<Code>> Cache =
	 * CacheBuilder.newBuilder().maximumSize(100) // maximum 100 records can be
	 * cached .expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after 30
	 * minutes of access .build(new CacheLoader<String, List<Code>>() { // build the
	 * // cacheloader
	 * 
	 * @Override public List<Code> load(String id) throws Exception { if
	 * (getDataFromElasticSearch(id) != null) return getDataFromElasticSearch(id);
	 * else return new ArrayList<Code>(); }
	 * 
	 * public Map<String, List<Code>> loadAll(Iterable<? extends String> keys) {
	 * return loadDataFromElasticSearch(keys); }
	 * 
	 * });
	 */

	@Override
	@Transactional
	public List<Prescription> getPrescriptions(int page, int size, String doctorId, String hospitalId,
			String locationId, String patientId, String updatedTime, boolean isOTPVerified, boolean discarded,
			boolean inHistory) {
		List<Prescription> prescriptions = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

		try {
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

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);

			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
			} else {
				pushNotificationServices.notifyUser(patientId, "Global records", null, null, null);
			}

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("advice", "$advice"), Fields.field("appointmentRequest", "$appointmentRequest"),
					Fields.field("time", "$time"), Fields.field("fromDate", "$fromDate"),
					Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.genericNames", "$items.genericNames"),
					Fields.field("tests", "$diagnosticTests"), Fields.field("locationName", "$location.locationName")));
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						new CustomAggregationOperation(
								new Document("$unwind",
										new BasicDBObject("path",
												"$visit").append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")

								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$push", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("tests", new BasicDBObject("$first", "$tests"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));

			} else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						new CustomAggregationOperation(
								new Document("$unwind",
										new BasicDBObject("path",
												"$visit").append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$push", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("tests", new BasicDBObject("$first", "$tests"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					PrescriptionCollection.class, Prescription.class);
			prescriptions = aggregationResults.getMappedResults();

			if (prescriptions != null && !prescriptions.isEmpty()) {

				for (Prescription prescription : prescriptions) {
					if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
						List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
						for (TestAndRecordData data : prescription.getTests()) {
							if (data.getTestId() != null) {
								DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
										.findById(data.getTestId()).orElse(null);
								DiagnosticTest diagnosticTest = new DiagnosticTest();
								if (diagnosticTestCollection != null) {
									BeanUtil.map(diagnosticTestCollection, diagnosticTest);
								}
								diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
										(!DPDoctorUtils.anyStringEmpty(data.getRecordId())
												? data.getRecordId().toString()
												: null)));
							}
						}
						prescription.setTests(null);
						prescription.setDiagnosticTests(diagnosticTests);
					}

					if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
						for (PrescriptionItemDetail prescriptionItemDetail : prescription.getItems()) {
							InventoryItem inventoryItem = null;
							if (prescriptionItemDetail.getDrug() != null
									&& !DPDoctorUtils.anyStringEmpty(prescriptionItemDetail.getDrug().getDrugCode())) {
								inventoryItem = inventoryService.getInventoryItemByResourceId(
										prescription.getLocationId(), prescription.getHospitalId(),
										prescriptionItemDetail.getDrug().getDrugCode());
							}
							if (inventoryItem != null) {
								InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
										.getInventoryItem(inventoryItem.getId());
								prescriptionItemDetail.setTotalStock(inventoryItemLookupResposne.getTotalStock());
								List<PrescriptionInventoryBatchResponse> inventoryBatchs = null;
								if (inventoryItemLookupResposne.getInventoryBatchs() != null) {
									inventoryBatchs = new ArrayList<>();
									for (InventoryBatch inventoryBatch : inventoryItemLookupResposne
											.getInventoryBatchs()) {
										PrescriptionInventoryBatchResponse response = new PrescriptionInventoryBatchResponse();
										BeanUtil.map(inventoryBatch, response);
										inventoryBatchs.add(response);

									}
									prescriptionItemDetail.setInventoryBatchs(inventoryBatchs);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@Override
	@Transactional
	public List<Prescription> getPrescriptionsByIds(List<ObjectId> prescriptionIds, ObjectId visitId) {
		List<Prescription> prescriptions = null;
		try {
			Criteria criteria = new Criteria("_id").in(prescriptionIds).and("isPatientDiscarded").ne(true);
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("advice", "$advice"), Fields.field("time", "$time"),
					Fields.field("fromDate", "$fromDate"), Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.drugQuantity", "$items.drugQuantity"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("items.genericNames", "$items.genericNames"),
					Fields.field("tests", "$diagnosticTests")));
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex1"))),
					Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
					Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex3"))),
					new CustomAggregationOperation(
							new Document("$unwind",
									new BasicDBObject("path", "$visit")
											.append("preserveNullAndEmptyArrays", true).append("includeArrayIndex",
													"arrayIndex5"))),
					projectList,
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$_id").append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("tests", new BasicDBObject("$first", "$tests"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))));
			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);
			prescriptions = aggregationResults.getMappedResults();
			if (prescriptions != null && !prescriptions.isEmpty()) {
				for (Prescription prescription : prescriptions) {
					/*if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
						for (PrescriptionItemDetail itemDetail : prescription.getItems()) {
							if (itemDetail.getDrug() != null) {
								itemDetail.getDrug().setDosage(itemDetail.getDosage());
								itemDetail.getDrug().setDirection(itemDetail.getDirection());
								itemDetail.getDrug().setDuration(itemDetail.getDuration());
							}

							itemDetail.setDuration(null);
							itemDetail.setDosage(null);
							itemDetail.setDirection(null);

						}
					}*/

					if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
						List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
						for (TestAndRecordData data : prescription.getTests()) {
							if (data.getTestId() != null) {
								DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
										.findById(data.getTestId()).orElse(null);
								DiagnosticTest diagnosticTest = new DiagnosticTest();
								if (diagnosticTestCollection != null) {
									BeanUtil.map(diagnosticTestCollection, diagnosticTest);
								}
								diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
										(!DPDoctorUtils.anyStringEmpty(data.getRecordId())
												? data.getRecordId().toString()
												: null)));
							}
						}
						prescription.setTests(null);
						prescription.setDiagnosticTests(diagnosticTests);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@Override
	@Transactional
	public EyePrescription getEyePrescription(String id) {
		EyePrescription response = null;
		EyePrescriptionCollection eyePrescriptionCollection = eyePrescriptionRepository.findById(new ObjectId(id)).orElse(null);
		if (eyePrescriptionCollection == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Record not found");
		}
		response = new EyePrescription();
		BeanUtil.map(eyePrescriptionCollection, response);
		return response;
	}

	@Override
	@Transactional
	public Prescription getPrescriptionById(String prescriptionId) {
		Prescription prescription = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("appointmentRequest", "$appointmentRequest"), Fields.field("advice", "$advice"),
					Fields.field("time", "$time"), Fields.field("fromDate", "$fromDate"),
					Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.drugQuantity", "$items.drugQuantity"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("items.genericNames", "$items.genericNames"),
					Fields.field("tests", "$diagnosticTests")));
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(new Criteria("_id").is(new ObjectId(prescriptionId))),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
							Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
									"appointmentRequest"),
							Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$appointmentRequest")
											.append("preserveNullAndEmptyArrays", true))),

							new CustomAggregationOperation(
									new Document("$unwind",
											new BasicDBObject("path", "$visit").append("preserveNullAndEmptyArrays",
													true))),
							projectList,
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
									.append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("tests", new BasicDBObject("$first", "$tests"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))));
			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);
			List<Prescription> prescriptions = aggregationResults.getMappedResults();

			if (prescriptions != null && !prescriptions.isEmpty()) {
				prescription = prescriptions.get(0);

				if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
					for (PrescriptionItemDetail itemDetail : prescription.getItems()) {
						if (itemDetail.getDrug() != null) {
							itemDetail.getDrug().setDosage(itemDetail.getDosage());
							itemDetail.getDrug().setDirection(itemDetail.getDirection());
							itemDetail.getDrug().setDuration(itemDetail.getDuration());
						}

						itemDetail.setDuration(null);
						itemDetail.setDosage(null);
						itemDetail.setDirection(null);

					}
				}
				if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
					List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
					for (TestAndRecordData data : prescription.getTests()) {
						if (data.getTestId() != null) {
							DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
									.findById(data.getTestId()).orElse(null);
							DiagnosticTest diagnosticTest = new DiagnosticTest();
							if (diagnosticTestCollection != null) {
								BeanUtil.map(diagnosticTestCollection, diagnosticTest);
							}
							diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
									(!DPDoctorUtils.anyStringEmpty(data.getRecordId()) ? data.getRecordId().toString()
											: null)));
						}
					}
					prescription.setTests(null);
					prescription.setDiagnosticTests(diagnosticTests);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting prescription : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting prescription : " + e.getCause().getMessage());
		}
		return prescription;
	}

	@Override
	@Transactional
	public List<Drug> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded, String searchTerm) {
		List<Drug> response = null;
		try {
			AggregationResults<Drug> results = mongoTemplate.aggregate(getDrugCustomGlobalAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm), DrugCollection.class,
					Drug.class);
			response = results.getMappedResults();

			if (response != null && !response.isEmpty()) {
				for (Drug drug : response) {

					if (!DPDoctorUtils.anyStringEmpty(drug.getLocationId(), drug.getHospitalId(), drug.getDrugCode())) {
						InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
								drug.getLocationId(), drug.getHospitalId(), drug.getDrugCode());
						if (inventoryItem != null) {
							InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
									.getInventoryItem(inventoryItem.getId());
							drug.setTotalStock(inventoryItemLookupResposne.getTotalStock());
							List<PrescriptionInventoryBatchResponse> inventoryBatchs = null;
							if (inventoryItemLookupResposne.getInventoryBatchs() != null) {
								inventoryBatchs = new ArrayList<>();
								for (InventoryBatch inventoryBatch : inventoryItemLookupResposne.getInventoryBatchs()) {
									PrescriptionInventoryBatchResponse batchResponse = new PrescriptionInventoryBatchResponse();
									BeanUtil.map(inventoryBatch, batchResponse);
									inventoryBatchs.add(batchResponse);
								}

								drug.setInventoryBatchs(inventoryBatchs);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	@Override
	@Transactional
	public Long countCustomGlobalDrugs(String doctorId, String locationId, String hospitalId, String updatedTime,
			boolean discarded, String searchTerm) {
		Long response = (long) 0;
		Long createdTimeStamp = (long) 0;
		try {
			if (DPDoctorUtils.anyStringEmpty(updatedTime)) {
				createdTimeStamp = Long.parseLong(updatedTime);
			}
			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
					criteria.orOperator(
							new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
									.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)),
							new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
				} else {
					criteria.orOperator(new Criteria("doctorId").is(new ObjectId(doctorId)),
							new Criteria("doctorId").is(null));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
								.is(new ObjectId(hospitalId)),
						new Criteria("locationId").is(null).and("hospitalId").is(null));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria.and("drugName").regex("^" + searchTerm, "i");

			response = mongoTemplate.count(new Query(criteria), DrugCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While count Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While count Drugs");
		}
		return response;
	}

	private Aggregation getDrugCustomGlobalAggregation(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm) {
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);

		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
								.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)),
						new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
			} else {
				criteria.orOperator(new Criteria("doctorId").is(new ObjectId(doctorId)),
						new Criteria("doctorId").is(null));
			}
		} else if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			criteria.orOperator(new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
					.is(new ObjectId(hospitalId)), new Criteria("locationId").is(null).and("hospitalId").is(null));
		}

		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("drugName").regex("^" + searchTerm, "i");

		Aggregation aggregation = null;

		if (size > 0)
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
					Aggregation.limit(size));
		else
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

		return aggregation;

	}
	
	@Override
	@Transactional
	public List<Prescription> getPrescriptionsForEMR(int page, int size, String doctorId, String hospitalId,
			String locationId, String patientId, String updatedTime, boolean isOTPVerified, boolean discarded,
			boolean inHistory) {
		List<Prescription> prescriptions = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

		try {
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

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);

			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
			} else {
				pushNotificationServices.notifyUser(patientId, "Global records", null, null, null);
			}

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("advice", "$advice"), /*Fields.field("appointmentRequest", "$appointmentRequest"),*/
					Fields.field("time", "$time"), Fields.field("fromDate", "$fromDate"),
					Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					/*Fields.field("appointmentId", "$appointmentId"), *//*Fields.field("visitId", "$visit._id"),*/
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), /*Fields.field("items.drug", "$drug"),*/
					Fields.field("items.drugId", "$items.drugId"), 
					Fields.field("items.drugName", "$items.drugName"), 
					Fields.field("items.drugType", "$items.drugType"), 
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("items.drugQuantity", "$items.drugQuantity"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.genericNames", "$items.genericNames"),
					Fields.field("tests", "$diagnosticTests"), Fields.field("locationName", "$location.locationName")));
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						/*Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),*/
					/*	Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),*/
						/*Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),*/
						/*new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),*/
						/*new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path",
												"$visit").append("preserveNullAndEmptyArrays",
														true))),*/
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								/*.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))*/
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$push", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("tests", new BasicDBObject("$first", "$tests"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("drugName", new BasicDBObject("$first", "$drugName"))
								.append("drugType", new BasicDBObject("$first", "$drugType"))
								.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
							/*	.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))*/
								/*.append("visitId", new BasicDBObject("$first", "$visitId"))*/
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));

			} else
			{
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						/*Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),*/
						/*Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),*/
						/*Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),*/
						/*Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),*/
					/*	new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),*/
						/*new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),*/
						/*new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path",
												"$visit").append("preserveNullAndEmptyArrays",
														true))),*/
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								/*.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))*/
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$push", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("tests", new BasicDBObject("$first", "$tests"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
								.append("drugName", new BasicDBObject("$first", "$drugName"))
								.append("drugType", new BasicDBObject("$first", "$drugType"))
								/*.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))*/
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}

			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);
			prescriptions = aggregationResults.getMappedResults();

			if (prescriptions != null && !prescriptions.isEmpty()) {
				for (Prescription prescription : prescriptions) {

					if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
						List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
						for (TestAndRecordData data : prescription.getTests()) {
							if (data.getTestId() != null) {
								DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
										.findById(data.getTestId()).orElse(null);
								DiagnosticTest diagnosticTest = new DiagnosticTest();
								if (diagnosticTestCollection != null) {
									BeanUtil.map(diagnosticTestCollection, diagnosticTest);
								}
								diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
										(!DPDoctorUtils.anyStringEmpty(data.getRecordId())
												? data.getRecordId().toString()
												: null)));
							}
						}
						prescription.setTests(null);
						prescription.setDiagnosticTests(diagnosticTests);
					}

					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}
	
	
	@Override
	@Transactional
	public List<Prescription> getPrescriptionsByIdsForEMR(List<ObjectId> prescriptionIds, ObjectId visitId) {
		List<Prescription> prescriptions = null;
		try {
			Criteria criteria = new Criteria("_id").in(prescriptionIds).and("isPatientDiscarded").ne(true);
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("advice", "$advice"), Fields.field("time", "$time"),
					Fields.field("fromDate", "$fromDate"), Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.drugId", "$items.drugId"), 
					Fields.field("items.drugName", "$items.drugName"), 
					Fields.field("items.drugType", "$items.drugType"), 
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("items.drugQuantity", "$items.drugQuantity"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.genericNames", "$items.genericNames"),
					Fields.field("tests", "$diagnosticTests"), Fields.field("locationName", "$location.locationName")));
			
			
			/*Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex1"))),
					Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
					Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex3"))),
					new CustomAggregationOperation(
							new BasicDBObject("$unwind",
									new BasicDBObject("path", "$visit")
											.append("preserveNullAndEmptyArrays", true).append("includeArrayIndex",
													"arrayIndex5"))),
					projectList,
					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", "$_id").append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("tests", new BasicDBObject("$first", "$tests"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))));
			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);*/
			
			
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex1"))),
					/*Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),*/
					/*Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),*/
					/*Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),*/
					/*Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"),*/
				/*	new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),*/
					/*new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
									true))),*/
					/*new CustomAggregationOperation(
							new BasicDBObject("$unwind",
									new BasicDBObject("path",
											"$visit").append("preserveNullAndEmptyArrays",
													true))),*/
					projectList,
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("name", new BasicDBObject("$first", "$name"))
							.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							/*.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))*/
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("items", new BasicDBObject("$push", "$items"))
							.append("inHistory", new BasicDBObject("$first", "$inHistory"))
							.append("advice", new BasicDBObject("$first", "$advice"))
							.append("tests", new BasicDBObject("$first", "$tests"))
							.append("time", new BasicDBObject("$first", "$time"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
							.append("drugName", new BasicDBObject("$first", "$drugName"))
							.append("drugType", new BasicDBObject("$first", "$drugType"))
							/*.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
							.append("visitId", new BasicDBObject("$first", "$visitId"))*/
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			
			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);
			
			
			prescriptions = aggregationResults.getMappedResults();
			if (prescriptions != null && !prescriptions.isEmpty()) {
				for (Prescription prescription : prescriptions) {
				
					if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
						List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
						for (TestAndRecordData data : prescription.getTests()) {
							if (data.getTestId() != null) {
								DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
										.findById(data.getTestId()).orElse(null);
								DiagnosticTest diagnosticTest = new DiagnosticTest();
								if (diagnosticTestCollection != null) {
									BeanUtil.map(diagnosticTestCollection, diagnosticTest);
								}
								diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
										(!DPDoctorUtils.anyStringEmpty(data.getRecordId())
												? data.getRecordId().toString()
												: null)));
							}
						}
						prescription.setTests(null);
						prescription.setDiagnosticTests(diagnosticTests);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}
	
	
	@Override
	@Transactional
	public Drug getDrugByDrugCode(String drugCode) {
		Drug drugAddEditResponse = null;
		try {
			DrugCollection drugCollection = drugRepository.findByDrugCode(drugCode);
			if (drugCollection != null) {
				drugAddEditResponse = new Drug();
				BeanUtil.map(drugCollection, drugAddEditResponse);
			} else {
				logger.warn("Drug not found. Please check Drug Id");
				throw new BusinessException(ServiceError.NoRecord, "Drug not found. Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return drugAddEditResponse;
	}
}