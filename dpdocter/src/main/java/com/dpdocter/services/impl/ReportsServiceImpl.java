package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.TimeDuration;
import com.dpdocter.collections.DeliveryReportsCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.IPDReportsCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OPDReportsCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DeliveryReportsRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.IPDReportsRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OPDReportsRepository;
import com.dpdocter.repository.OTReportsRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DeliveryReportsLookupResponse;
import com.dpdocter.response.DeliveryReportsResponse;
import com.dpdocter.response.IPDReportLookupResponse;
import com.dpdocter.response.IPDReportsResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.OPDReportsLookupResponse;
import com.dpdocter.response.OPDReportsResponse;
import com.dpdocter.response.OTReportsLookupResponse;
import com.dpdocter.response.OTReportsResponse;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.ReportsService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class ReportsServiceImpl implements ReportsService {

	private static Logger logger = Logger.getLogger(ReportsServiceImpl.class.getName());

	@Autowired
	IPDReportsRepository ipdReportsRepository;

	@Autowired
	OPDReportsRepository opdReportsRepository;

	@Autowired
	OTReportsRepository otReportsRepository;

	@Autowired
	DeliveryReportsRepository deliveryReportsRepository;

	@Autowired
	PrescriptionRepository prescriptionRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	HospitalRepository hospitalRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	PrescriptionServices prescriptionServices;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private JasperReportService jasperReportService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.ot.reports.fileName}")
	private String OTReportsFileName;

	@Value(value = "${jasper.print.delivery.reports.fileName}")
	private String deliveryReportsFileName;

	@Override
	@Transactional
	public IPDReports submitIPDReport(IPDReports ipdReports) {
		IPDReports response = null;
		IPDReportsCollection ipdReportsCollection = new IPDReportsCollection();
		UserCollection userCollection = userRepository.findOne(new ObjectId(ipdReports.getDoctorId()));

		if (ipdReports != null) {
			BeanUtil.map(ipdReports, ipdReportsCollection);
			try {

				ipdReportsCollection.setAdminCreatedTime(new Date());
				if (ipdReports.getCreatedTime() == null) {
					ipdReportsCollection.setCreatedTime(new Date());
				}

				ipdReportsCollection.setCreatedBy(
						(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() : "DR.")
								+ " " + userCollection.getFirstName());

				ipdReportsCollection = ipdReportsRepository.save(ipdReportsCollection);

				if (ipdReportsCollection != null) {
					BeanUtil.map(ipdReportsCollection, ipdReports);
					response = new IPDReports();
					response = ipdReports;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error occured while creating IPD Records");
				throw new BusinessException(ServiceError.Unknown, "Error occured while creating IPD Records");
			}
		}
		return response;
	}

	@Override
	@Transactional
	public OPDReports submitOPDReport(OPDReports opdReports) {
		OPDReports response = null;

		OPDReportsCollection opdReportsCollection = null;
		try {
			if (opdReports != null) {
				OPDReportsCollection opdReportsCollectionOld = opdReportsRepository
						.getOPDReportByPrescriptionId(new ObjectId(opdReports.getPrescriptionId()));
				if (opdReportsCollectionOld != null) {
					opdReportsCollectionOld.setAmountReceived(opdReports.getAmountReceived());
					if (opdReports.getReceiptDate() != null) {
						opdReportsCollectionOld.setReceiptDate(opdReports.getReceiptDate());
					} else {
						opdReportsCollectionOld.setReceiptDate(new Date());
					}
					opdReportsCollectionOld.setReceiptNo(opdReports.getReceiptNo());
					opdReportsCollectionOld.setRemarks(opdReports.getRemarks());
					if (opdReports.getCreatedTime() != null) {
						opdReportsCollectionOld.setCreatedTime(opdReports.getCreatedTime());
					}
					opdReportsCollectionOld.setUpdatedTime(new Date());
					opdReportsCollection = opdReportsRepository.save(opdReportsCollectionOld);
				} else {
					opdReportsCollection = new OPDReportsCollection();
					UserCollection userCollection = userRepository.findOne(new ObjectId(opdReports.getDoctorId()));
					BeanUtil.map(opdReports, opdReportsCollection);
					opdReportsCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
							? userCollection.getTitle() : "DR.") + " " + userCollection.getFirstName());
					opdReportsCollectionOld.setAdminCreatedTime(new Date());
					if (opdReports.getCreatedTime() == null) {
						opdReportsCollectionOld.setCreatedTime(new Date());
					}
					opdReportsCollection = opdReportsRepository.save(opdReportsCollection);
				}

				if (opdReportsCollection != null) {
					response = new OPDReports();
					BeanUtil.map(opdReportsCollection, response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while creating OPD Records");
			throw new BusinessException(ServiceError.Unknown, "Error occured while OPD Records");
		}

		return response;
	}

	@Override
	@Transactional
	public OTReports submitOTReport(OTReports otReports) {
		OTReports response = null;
		OTReportsCollection otReportsCollection = new OTReportsCollection();
		UserCollection userCollection = userRepository.findOne(new ObjectId(otReports.getDoctorId()));
		if (otReports != null) {
			BeanUtil.map(otReports, otReportsCollection);
			try {

				if (DPDoctorUtils.anyStringEmpty(otReportsCollection.getUniqueOTId()))
					otReportsCollection
							.setUniqueOTId(UniqueIdInitial.OT_REPORTS.getInitial() + DPDoctorUtils.generateRandomId());

				otReportsCollection.setAdminCreatedTime(new Date());
				if (otReports.getCreatedTime() == null) {
					otReportsCollection.setCreatedTime(new Date());

				}
				otReportsCollection.setCreatedBy(
						(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() : "DR.")
								+ " " + userCollection.getFirstName());

				otReportsCollection = otReportsRepository.save(otReportsCollection);

				if (otReportsCollection != null) {
					BeanUtil.map(otReportsCollection, otReports);
					response = new OTReports();
					response = otReports;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error occured while creating OT Records");
				throw new BusinessException(ServiceError.Unknown, "Error occured while OT Records");
			}
		}
		return response;
	}

	@Override
	@Transactional
	public DeliveryReports submitDeliveryReport(DeliveryReports deliveryReports) {
		DeliveryReports response = null;
		DeliveryReportsCollection deliveryReportsCollection = new DeliveryReportsCollection();
		UserCollection userCollection = userRepository.findOne(new ObjectId(deliveryReports.getDoctorId()));
		if (deliveryReports != null) {
			BeanUtil.map(deliveryReports, deliveryReportsCollection);
			try {

				if (DPDoctorUtils.anyStringEmpty(deliveryReportsCollection.getUniqueDRId()))
					deliveryReportsCollection.setUniqueDRId(
							UniqueIdInitial.DELIVERY_REPORTS.getInitial() + DPDoctorUtils.generateRandomId());

				deliveryReportsCollection.setAdminCreatedTime(new Date());
				if (deliveryReports.getCreatedTime() == null) {
					deliveryReportsCollection.setCreatedTime(new Date());

				}
				deliveryReportsCollection.setCreatedBy(
						(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() : "DR.")
								+ " " + userCollection.getFirstName());

				deliveryReportsCollection = deliveryReportsRepository.save(deliveryReportsCollection);
				if (deliveryReportsCollection != null) {
					BeanUtil.map(deliveryReportsCollection, deliveryReports);
					response = new DeliveryReports();
					response = deliveryReports;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error occured while creating Delivery Records");
				throw new BusinessException(ServiceError.Unknown, "Error occured while Delivery Records");
			}
		}
		return response;
	}

	@Override
	@Transactional
	public IPDReportsResponse getIPDReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		List<IPDReports> response = null;
		IPDReportsResponse ipdReportsResponse = null;
		List<IPDReportLookupResponse> ipdReportLookupResponses = null;
		int count = 0;
		try {

			// long updatedTimeStamp = Long.parseLong(updatedTime);
			// Criteria criteria = new Criteria("updatedTime").gte(new
			// Date(updatedTimeStamp));
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(from) && !DPDoctorUtils.anyStringEmpty(to)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))))
						.lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			} else if (!DPDoctorUtils.anyStringEmpty(from)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				criteria.and("createdTime").lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			}
			if (size > 0)
				ipdReportLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
								Aggregation.unwind("hospital"), Aggregation.skip(page * size), Aggregation.limit(size),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						IPDReportsCollection.class, IPDReportLookupResponse.class).getMappedResults();
			else
				ipdReportLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						IPDReportsCollection.class, IPDReportLookupResponse.class).getMappedResults();

			if (ipdReportLookupResponses != null) {
				response = new ArrayList<IPDReports>();
				for (IPDReportLookupResponse collection : ipdReportLookupResponses) {
					IPDReports ipdReports = new IPDReports();
					BeanUtil.map(collection, ipdReports);
					if (collection.getDoctor() != null) {
						UserCollection doctor = collection.getDoctor();
						if (doctor != null)
							ipdReports.setDoctorName(doctor.getFirstName());
					}
					if (collection.getLocation() != null) {
						LocationCollection locationCollection = collection.getLocation();
						if (locationCollection != null) {
							ipdReports.setLocationName(locationCollection.getLocationName());
						}
					}
					if (collection.getHospitalId() != null) {
						HospitalCollection hospitalCollection = collection.getHospital();
						if (hospitalCollection != null) {
							ipdReports.setHospitalName(hospitalCollection.getHospitalName());
						}
					}
					if (collection.getPatientId() != null) {
						PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
								new ObjectId(collection.getPatientId()), new ObjectId(collection.getLocationId()),
								new ObjectId(collection.getHospitalId()));
						if (patientCollection != null) {
							Patient patient = new Patient();
							BeanUtil.map(patientCollection, patient);
							ipdReports.setPatient(patient);
						}
					}
					response.add(ipdReports);
				}
			}

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria2.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria2.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria2.and("patientId").is(new ObjectId(patientId));
			}

			count = (int) mongoTemplate.count(new Query(criteria2), IPDReportsCollection.class);

			ipdReportsResponse = new IPDReportsResponse();
			ipdReportsResponse.setIpdReports(response);
			ipdReportsResponse.setCount(count);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return ipdReportsResponse;
	}

	@Override
	@Transactional
	public OPDReportsResponse getOPDReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		// TODO Auto-generated method stub
		List<OPDReports> response = null;
		OPDReportsResponse opdReportsResponse = null;
		List<OPDReportsLookupResponse> opdReportsLookupResponses = null;
		int count = 0;
		try {

			// long updatedTimeStamp = Long.parseLong(updatedTime);
			// Criteria criteria = new Criteria("updatedTime").gte(new
			// Date(updatedTimeStamp));
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(from) && !DPDoctorUtils.anyStringEmpty(to)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))))
						.lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			} else if (!DPDoctorUtils.anyStringEmpty(from)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				criteria.and("createdTime").lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			}

			if (size > 0)
				opdReportsLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
								Aggregation.unwind("hospital"),
								Aggregation.lookup("prescription_cl", "prescriptionId", "_id",
										"prescriptionCollection"),
								Aggregation.unwind("prescriptionCollection"), Aggregation.skip(page * size),
								Aggregation.limit(size), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						OPDReportsCollection.class, OPDReportsLookupResponse.class).getMappedResults();
			else
				opdReportsLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
								Aggregation.unwind("hospital"),
								Aggregation.lookup("prescription_cl", "prescriptionId", "_id",
										"prescriptionCollection"),
								Aggregation.unwind("prescriptionCollection"),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						OPDReportsCollection.class, OPDReportsLookupResponse.class).getMappedResults();

			if (opdReportsLookupResponses != null) {
				response = new ArrayList<OPDReports>();
				for (OPDReportsLookupResponse collection : opdReportsLookupResponses) {
					OPDReports opdReports = new OPDReports();

					BeanUtil.map(collection, opdReports);
					if (collection.getDoctorId() != null) {
						UserCollection doctor = collection.getDoctor();
						if (doctor != null)
							opdReports.setDoctorName(doctor.getFirstName());
					}
					if (collection.getLocationId() != null) {
						LocationCollection locationCollection = collection.getLocation();
						if (locationCollection != null) {
							opdReports.setLocationName(locationCollection.getLocationName());
						}
					}
					if (collection.getHospitalId() != null) {
						HospitalCollection hospitalCollection = collection.getHospital();
						if (hospitalCollection != null) {
							opdReports.setHospitalName(hospitalCollection.getHospitalName());
						}
					}
					if (collection.getPatientId() != null) {
						PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
								new ObjectId(collection.getPatientId()), new ObjectId(collection.getLocationId()),
								new ObjectId(collection.getHospitalId()));
						if (patientCollection != null) {
							Patient patient = new Patient();
							BeanUtil.map(patientCollection, patient);
							opdReports.setPatient(patient);
						}
					}

					if (collection.getPrescriptionCollection() != null) {
						Prescription prescription = new Prescription();
						List<TestAndRecordData> tests = collection.getPrescriptionCollection().getDiagnosticTests();
						collection.getPrescriptionCollection().setDiagnosticTests(null);

						List<PrescriptionItem> items = collection.getPrescriptionCollection().getItems();
						collection.getPrescriptionCollection().setItems(null);

						BeanUtil.map(collection.getPrescriptionCollection(), prescription);
						if (items != null && !items.isEmpty()) {
							List<PrescriptionItemDetail> prescriptionItemDetails = new ArrayList<PrescriptionItemDetail>();
							for (PrescriptionItem prescriptionItem : items) {
								PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
								BeanUtil.map(prescriptionItem, prescriptionItemDetail);
								DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
								if (drugCollection != null) {
									Drug drug = new Drug();
									BeanUtil.map(drugCollection, drug);
									prescriptionItemDetail.setDrug(drug);
								}
								prescriptionItemDetails.add(prescriptionItemDetail);
							}
							prescription.setItems(prescriptionItemDetails);
						}
						PatientVisitCollection patientVisitCollection = patientVisitRepository
								.findByPrescriptionId(collection.getPrescriptionCollection().getId());
						if (patientVisitCollection != null)
							prescription.setVisitId(patientVisitCollection.getId().toString());

						if (tests != null && !tests.isEmpty()) {
							List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
							for (TestAndRecordData data : tests) {
								if (data.getTestId() != null) {
									DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
											.findOne(data.getTestId());
									DiagnosticTest diagnosticTest = new DiagnosticTest();
									if (diagnosticTestCollection != null) {
										BeanUtil.map(diagnosticTestCollection, diagnosticTest);
									}
									if (!DPDoctorUtils.anyStringEmpty(data.getRecordId())) {
										diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
												data.getRecordId().toString()));
									} else {
										diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest, null));
									}

								}
							}
							prescription.setDiagnosticTests(diagnosticTests);
						}
						if (prescription != null) {
							opdReports.setPrescription(prescription);
						}
					}
					response.add(opdReports);
				}
			}

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria2.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria2.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria2.and("patientId").is(new ObjectId(patientId));
			}

			count = (int) mongoTemplate.count(new Query(criteria2), OPDReportsCollection.class);

			opdReportsResponse = new OPDReportsResponse();
			opdReportsResponse.setOpdReports(response);
			opdReportsResponse.setCount(count);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return opdReportsResponse;
	}

	@Override
	@Transactional
	public OTReportsResponse getOTReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		// TODO Auto-generated method stub
		List<OTReports> response = null;
		OTReportsResponse otReportsResponse = null;
		List<OTReportsLookupResponse> otReportsLookupResponses = null;
		int count = 0;
		try {

			// long updatedTimeStamp = Long.parseLong(updatedTime);
			// Criteria criteria = new Criteria("updatedTime").gte(new
			// Date(updatedTimeStamp));
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(from) && !DPDoctorUtils.anyStringEmpty(to)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))))
						.lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			} else if (!DPDoctorUtils.anyStringEmpty(from)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				criteria.and("createdTime").lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			}

			if (size > 0)
				otReportsLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
								Aggregation.unwind("hospital"), Aggregation.skip(page * size), Aggregation.limit(size),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						OTReportsCollection.class, OTReportsLookupResponse.class).getMappedResults();
			else
				otReportsLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						OTReportsCollection.class, OTReportsLookupResponse.class).getMappedResults();

			if (otReportsLookupResponses != null) {
				response = new ArrayList<OTReports>();
				for (OTReportsLookupResponse collection : otReportsLookupResponses) {
					OTReports otReports = new OTReports();
					BeanUtil.map(collection, otReports);
					if (collection.getDoctorId() != null) {
						UserCollection doctor = collection.getDoctor();
						if (doctor != null)
							otReports.setDoctorName(doctor.getFirstName());
					}
					LocationCollection locationCollection = collection.getLocation();
					if (locationCollection != null) {
						otReports.setLocationName(locationCollection.getLocationName());

					}
					HospitalCollection hospitalCollection = collection.getHospital();
					if (hospitalCollection != null) {
						otReports.setHospitalName(hospitalCollection.getHospitalName());
					}
					if (collection.getPatientId() != null) {
						PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
								new ObjectId(collection.getPatientId()), new ObjectId(collection.getLocationId()),
								new ObjectId(collection.getHospitalId()));
						if (patientCollection != null) {
							Patient patient = new Patient();
							BeanUtil.map(patientCollection, patient);
							otReports.setPatient(patient);
						}
					}
					if (collection.getSurgery().getEndTime() != null
							&& collection.getSurgery().getStartTime() != null) {
						Long diff = collection.getSurgery().getStartTime() - collection.getSurgery().getEndTime();
						TimeDuration timeDuration = new TimeDuration();

						Long diffSeconds = diff / 1000 % 60;
						Long diffMinutes = diff / (60 * 1000) % 60;
						Long diffHours = diff / (60 * 60 * 1000);
						Integer diffInDays = (int) ((collection.getSurgery().getEndTime()
								- collection.getSurgery().getStartTime()) / (1000 * 60 * 60 * 24));

						timeDuration.setSeconds(diffSeconds.intValue());
						timeDuration.setMinutes(diffMinutes.intValue());
						timeDuration.setHours(diffHours.intValue());
						timeDuration.setDays(diffInDays);

						otReports.setTimeDuration(timeDuration);
					}

					response.add(otReports);
				}
			}

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria2.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria2.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria2.and("patientId").is(new ObjectId(patientId));
			}

			count = (int) mongoTemplate.count(new Query(criteria2), OTReportsCollection.class);
			otReportsResponse = new OTReportsResponse();
			otReportsResponse.setOtReports(response);
			otReportsResponse.setCount(count);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return otReportsResponse;
	}

	@Override
	@Transactional
	public DeliveryReportsResponse getDeliveryReportsList(String locationId, String doctorId, String patientId,
			String from, String to, int page, int size, String updatedTime) {
		// TODO Auto-generated method stub
		List<DeliveryReports> response = null;
		DeliveryReportsResponse deliveryReportsResponse = null;
		List<DeliveryReportsLookupResponse> deliveryReportsLookupResponses = null;
		int count = 0;
		try {

			// long updatedTimeStamp = Long.parseLong(updatedTime);
			// Criteria criteria = new Criteria("updatedTime").gte(new
			// Date(updatedTimeStamp));
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(from) && !DPDoctorUtils.anyStringEmpty(to)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))))
						.lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			} else if (!DPDoctorUtils.anyStringEmpty(from)) {

				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				criteria.and("createdTime").lte(DPDoctorUtils.getEndTime(new Date(Long.parseLong(to))));
			}

			if (size > 0)
				deliveryReportsLookupResponses = mongoTemplate
						.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
								Aggregation.unwind("hospital"), Aggregation.skip(page * size), Aggregation.limit(size),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
								DeliveryReportsCollection.class, DeliveryReportsLookupResponse.class)
						.getMappedResults();
			else
				deliveryReportsLookupResponses = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
										Aggregation.unwind("doctor"),
										Aggregation.lookup("location_cl", "locationId", "_id", "location"),
										Aggregation.unwind("location"),
										Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
										Aggregation.unwind("hospital"),
										Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
								DeliveryReportsCollection.class, DeliveryReportsLookupResponse.class)
						.getMappedResults();

			if (deliveryReportsLookupResponses != null) {
				response = new ArrayList<DeliveryReports>();
				for (DeliveryReportsLookupResponse collection : deliveryReportsLookupResponses) {
					DeliveryReports deliveryReports = new DeliveryReports();
					BeanUtil.map(collection, deliveryReports);
					if (collection.getDoctorId() != null) {
						UserCollection doctor = collection.getDoctor();
						if (doctor != null)
							deliveryReports.setDoctorName(doctor.getFirstName());
					}
					LocationCollection locationCollection = collection.getLocation();
					if (locationCollection != null) {
						deliveryReports.setLocationName(locationCollection.getLocationName());

					}
					HospitalCollection hospitalCollection = collection.getHospital();
					if (hospitalCollection != null) {
						deliveryReports.setHospitalName(hospitalCollection.getHospitalName());
					}
					if (collection.getPatientId() != null) {
						PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
								new ObjectId(collection.getPatientId()), new ObjectId(collection.getLocationId()),
								new ObjectId(collection.getHospitalId()));
						if (patientCollection != null) {
							Patient patient = new Patient();
							BeanUtil.map(patientCollection, patient);
							deliveryReports.setPatient(patient);
						}
					}

					response.add(deliveryReports);
				}
			}

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria2.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria2.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria2.and("patientId").is(new ObjectId(patientId));
			}

			count = (int) mongoTemplate.count(new Query(criteria2), DeliveryReportsCollection.class);
			deliveryReportsResponse = new DeliveryReportsResponse();
			deliveryReportsResponse.setDeliveryReports(response);
			deliveryReportsResponse.setCount(count);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return deliveryReportsResponse;
	}

	@Override
	public Boolean addPrescriptionOPDReports() {
		Boolean response = false;
		try {
			List<PrescriptionCollection> prescriptionCollections = prescriptionRepository.findAll();
			for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {
				OPDReports opdReports = new OPDReports(String.valueOf(prescriptionCollection.getPatientId()),
						String.valueOf(prescriptionCollection.getId()),
						String.valueOf(prescriptionCollection.getDoctorId()),
						String.valueOf(prescriptionCollection.getLocationId()),
						String.valueOf(prescriptionCollection.getHospitalId()), prescriptionCollection.getCreatedTime(),
						prescriptionCollection.getUpdatedTime());
				opdReports = submitOPDReport(opdReports);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public OPDReports getOPDReportByVisitId(String visitId) {
		OPDReports response = null;
		OPDReportsCollection opdReportsCollection = opdReportsRepository.getOPDReportByVisitId(new ObjectId(visitId));
		if (opdReportsCollection != null) {
			response = new OPDReports();
			BeanUtil.map(opdReportsCollection, response);
		}
		return response;
	}

	@Override
	public String getOTReportsFile(String otId) {
		String response = null;
		try {
			List<OTReportsLookupResponse> otReportsLookupResponses = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(otId))),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patientCollection"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$patientCollection").append("preserveNullAndEmptyArrays",
											true))),
							new CustomAggregationOperation(new BasicDBObject("$redact",
									new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$patientCollection.locationId",
																	"$locationId"))).append("then", "$$KEEP")
																			.append("else", "$$PRUNE")))),

							Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
							Aggregation.unwind("patientUser")), OTReportsCollection.class,
							OTReportsLookupResponse.class)
					.getMappedResults();

			if (otReportsLookupResponses != null) {
				OTReportsLookupResponse otReportsLookupResponse = otReportsLookupResponses.get(0);
				PatientCollection patient = otReportsLookupResponse.getPatientCollection();
				UserCollection user = otReportsLookupResponse.getPatientUser();

				JasperReportResponse jasperReportResponse = createOTReportsJasper(otReportsLookupResponse, patient,
						user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private JasperReportResponse createOTReportsJasper(OTReportsLookupResponse otReportsLookupResponse,
			PatientCollection patient, UserCollection user) throws NumberFormatException, IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				new ObjectId(otReportsLookupResponse.getDoctorId()),
				new ObjectId(otReportsLookupResponse.getLocationId()),
				new ObjectId(otReportsLookupResponse.getHospitalId()), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		if (otReportsLookupResponse.getOperationDate() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			parameters.put("operationDate", sdf.format(otReportsLookupResponse.getOperationDate()));
		}

		parameters.put("anaesthesiaType", (otReportsLookupResponse.getAnaesthesiaType() != null)
				? otReportsLookupResponse.getAnaesthesiaType().getAnaesthesiaType() : null);

		if (otReportsLookupResponse.getSurgery() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			String startTime = "";
			if (otReportsLookupResponse.getSurgery().getStartTime() != null)
				startTime = sdf.format(new Date(otReportsLookupResponse.getSurgery().getStartTime()));

			String endTime = "";
			if (otReportsLookupResponse.getSurgery().getEndTime() != null)
				endTime = sdf.format(new Date(otReportsLookupResponse.getSurgery().getEndTime()));

			if (!DPDoctorUtils.anyStringEmpty(startTime, endTime))
				parameters.put("dateAndTimeOfSurgery", startTime + " to " + endTime);
			else if (!DPDoctorUtils.anyStringEmpty(startTime))
				parameters.put("dateAndTimeOfSurgery", startTime);
		}

		if (otReportsLookupResponse.getTimeDuration() != null) {
			TimeDuration timeDuration = otReportsLookupResponse.getTimeDuration();
			String duration = "";
			if (timeDuration.getDays() != null && timeDuration.getDays() > 0)
				duration = timeDuration.getDays() + " days";
			if (timeDuration.getHours() != null && timeDuration.getHours() > 0) {
				duration = (!DPDoctorUtils.anyStringEmpty(duration) ? duration + " " : "") + timeDuration.getHours()
						+ " hrs";
			}
			if (timeDuration.getMinutes() != null && timeDuration.getMinutes() > 0) {
				duration = (!DPDoctorUtils.anyStringEmpty(duration) ? duration + " " : "") + timeDuration.getMinutes()
						+ " mins";
			}
			if (timeDuration.getSeconds() != null && timeDuration.getSeconds() > 0) {
				duration = (!DPDoctorUtils.anyStringEmpty(duration) ? duration + " " : "") + timeDuration.getSeconds()
						+ " secs";
			}

			if (!DPDoctorUtils.anyStringEmpty(duration))
				parameters.put("durationOfSurgery", duration);
		}

		parameters.put("provisionalDiagnosis", otReportsLookupResponse.getProvisionalDiagnosis());
		parameters.put("finalDiagnosis", otReportsLookupResponse.getFinalDiagnosis());
		parameters.put("operatingSurgeon", otReportsLookupResponse.getOperatingSurgeon());
		parameters.put("anaesthetist", otReportsLookupResponse.getAnaesthetist());
		parameters.put("materialForHPE",
				otReportsLookupResponse.getMaterialForHPE() != null && otReportsLookupResponse.getMaterialForHPE()
						? "YES" : "NO");
		parameters.put("remarks", otReportsLookupResponse.getRemarks());
		parameters.put("operationalNotes", otReportsLookupResponse.getOperationalNotes());
		parameters.put("otReportsId", otReportsLookupResponse.getId());

		patientVisitService
				.generatePatientDetails(
						(printSettings != null && printSettings.getHeaderSetup() != null
								? printSettings.getHeaderSetup().getPatientDetails() : null),
						patient,
						"<b>OT-ID: </b>" + (!DPDoctorUtils.anyStringEmpty(otReportsLookupResponse.getUniqueOTId())
								? otReportsLookupResponse.getUniqueOTId() : "--"),
						patient.getLocalPatientName(), user.getMobileNumber(),
						parameters, otReportsLookupResponse.getUpdatedTime() != null
								? otReportsLookupResponse.getUpdatedTime() : new Date(),
						printSettings.getHospitalUId());

		patientVisitService.generatePrintSetup(parameters, printSettings,
				new ObjectId(otReportsLookupResponse.getDoctorId()));
		String pdfName = (user != null ? user.getFirstName() : "") + "OTREPORTS-"
				+ (!DPDoctorUtils.anyStringEmpty(otReportsLookupResponse.getUniqueOTId())
						? otReportsLookupResponse.getUniqueOTId() : "")
				+ new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.OT_REPORTS, parameters, OTReportsFileName, layout,
				pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public String getDeliveryReportsFile(String reportId) {
		String response = null;
		try {
			List<DeliveryReportsLookupResponse> deliveryReportsLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(reportId))),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patientCollection"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$patientCollection").append("preserveNullAndEmptyArrays",
											true))),
							new CustomAggregationOperation(new BasicDBObject("$redact",
									new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$patientCollection.locationId",
																	"$locationId"))).append("then", "$$KEEP")
																			.append("else", "$$PRUNE")))),

							Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
							Aggregation.unwind("patientUser")),
					DeliveryReportsCollection.class, DeliveryReportsLookupResponse.class).getMappedResults();

			if (deliveryReportsLookupResponses != null) {
				DeliveryReportsLookupResponse deliveryReportsLookupResponse = deliveryReportsLookupResponses.get(0);
				PatientCollection patient = deliveryReportsLookupResponse.getPatientCollection();
				UserCollection user = deliveryReportsLookupResponse.getPatientUser();

				JasperReportResponse jasperReportResponse = createDeliveryReportsJasper(deliveryReportsLookupResponse,
						patient, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private JasperReportResponse createDeliveryReportsJasper(
			DeliveryReportsLookupResponse deliveryReportsLookupResponse, PatientCollection patient, UserCollection user)
			throws NumberFormatException, IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				new ObjectId(deliveryReportsLookupResponse.getDoctorId()),
				new ObjectId(deliveryReportsLookupResponse.getLocationId()),
				new ObjectId(deliveryReportsLookupResponse.getHospitalId()), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		parameters.put("deliveryReportsId", deliveryReportsLookupResponse.getId());
		if (deliveryReportsLookupResponse.getDeliveryDate() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			parameters.put("deliveryDate", sdf.format(deliveryReportsLookupResponse.getDeliveryDate()));
		}

		if (deliveryReportsLookupResponse.getDeliveryTime() != null
				&& deliveryReportsLookupResponse.getDeliveryTime() != 0) {
			parameters.put("deliveryTime",
					String.format("%02d:%02d", deliveryReportsLookupResponse.getDeliveryTime() / 60,
							deliveryReportsLookupResponse.getDeliveryTime() % 60));
		}
		parameters.put("babyGender", deliveryReportsLookupResponse.getBabyGender());

		parameters.put("deliveryType", deliveryReportsLookupResponse.getDeliveryType());
		parameters.put("formNo", deliveryReportsLookupResponse.getFormNo());
		parameters.put("remarks", deliveryReportsLookupResponse.getRemarks());

		patientVisitService
				.generatePatientDetails(
						(printSettings != null && printSettings.getHeaderSetup() != null
								? printSettings.getHeaderSetup().getPatientDetails() : null),
						patient,
						"<b>DR-ID: </b>" + (!DPDoctorUtils.anyStringEmpty(deliveryReportsLookupResponse.getUniqueDRId())
								? deliveryReportsLookupResponse.getUniqueDRId() : "--"),
						patient.getLocalPatientName(), user.getMobileNumber(), parameters,
						deliveryReportsLookupResponse.getUpdatedTime() != null
								? deliveryReportsLookupResponse.getUpdatedTime() : new Date(),
						printSettings.getHospitalUId());

		patientVisitService.generatePrintSetup(parameters, printSettings,
				new ObjectId(deliveryReportsLookupResponse.getDoctorId()));
		String pdfName = (user != null ? user.getFirstName() : "") + "DELIVERYREPORTS-"
				+ (!DPDoctorUtils.anyStringEmpty(deliveryReportsLookupResponse.getUniqueDRId())
						? deliveryReportsLookupResponse.getUniqueDRId() : "")
				+ new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.DELIVERY_REPORTS, parameters, deliveryReportsFileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		return response;
	}

}
