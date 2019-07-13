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

import com.dpdocter.beans.BrokenAppointment;
import com.dpdocter.beans.ClinicalIndicator;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.DoctorAndCost;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.EquipmentLogAMCAndServicingRegister;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.RepairRecordsOrComplianceBook;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.TimeDuration;
import com.dpdocter.collections.BroakenAppointmentCollection;
import com.dpdocter.collections.ClinicalIndicatorCollection;
import com.dpdocter.collections.DeliveryReportsCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EquipmentLogAMCAndServicingRegisterCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.IPDReportsCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OPDReportsCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.RepairRecordsOrComplianceBookCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FieldAlign;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BrokenAppointmentRepository;
import com.dpdocter.repository.ClinicalIndicatorRepository;
import com.dpdocter.repository.DeliveryReportsRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.EquipmentLogAMCAndServicingRegisterRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.IPDReportsRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OPDReportsRepository;
import com.dpdocter.repository.OTReportsRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.RepairRecordsOrComplianceBookRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DeliveryReportsLookupResponse;
import com.dpdocter.response.DeliveryReportsResponse;
import com.dpdocter.response.DurationResponse;
import com.dpdocter.response.GenericCodeResponse;
import com.dpdocter.response.IPDReportLookupResponse;
import com.dpdocter.response.IPDReportsResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.OPDDiagnosticTestResponse;
import com.dpdocter.response.OPDPrescriptionItemResponse;
import com.dpdocter.response.OPDPrescriptionResponse;
import com.dpdocter.response.OPDReportCustomResponse;
import com.dpdocter.response.OPDReportsLookupResponse;
import com.dpdocter.response.OPDReportsResponse;
import com.dpdocter.response.OTReportsLookupResponse;
import com.dpdocter.response.OTReportsResponse;
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
	RepairRecordsOrComplianceBookRepository repairRecordsOrComplianceBookRepository;
	@Autowired
	private ClinicalIndicatorRepository clinicalIndicatorRepository;

	@Autowired
	EquipmentLogAMCAndServicingRegisterRepository equipmentLogAMCAndServicingRegisterRepository;

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
	private BrokenAppointmentRepository brokenAppointmentRepository;

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
					opdReportsCollection.setCreatedBy(
							(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle()
									: "DR.") + " " + userCollection.getFirstName());
					opdReportsCollection.setAdminCreatedTime(new Date());
					if (opdReports.getCreatedTime() == null) {
						opdReportsCollection.setCreatedTime(new Date());
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
			Criteria criteria = new Criteria("isPatientDiscarded").ne(true);
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
				ipdReportLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip(page * size), Aggregation.limit(size)), IPDReportsCollection.class,
						IPDReportLookupResponse.class).getMappedResults();
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
		List<OPDReportCustomResponse> response = null;
		OPDReportsResponse opdReportsResponse = null;
		List<OPDReportsLookupResponse> opdReportsLookupResponses = null;
		try {

			// long updatedTimeStamp = Long.parseLong(updatedTime);
			// Criteria criteria = new Criteria("updatedTime").gte(new
			// Date(updatedTimeStamp));
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null)
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
								Aggregation.unwind("prescriptionCollection"),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip(page * size), Aggregation.limit(size)),
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
				response = new ArrayList<OPDReportCustomResponse>();
				for (OPDReportsLookupResponse collection : opdReportsLookupResponses) {
					OPDReportCustomResponse opdReports = new OPDReportCustomResponse();

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
						OPDPrescriptionResponse prescription = new OPDPrescriptionResponse();
						List<TestAndRecordData> tests = collection.getPrescriptionCollection().getDiagnosticTests();
						collection.getPrescriptionCollection().setDiagnosticTests(null);

						List<PrescriptionItem> items = collection.getPrescriptionCollection().getItems();
						collection.getPrescriptionCollection().setItems(null);

						BeanUtil.map(collection.getPrescriptionCollection(), prescription);
						if (items != null && !items.isEmpty()) {
							List<OPDPrescriptionItemResponse> prescriptionItemDetails = new ArrayList<OPDPrescriptionItemResponse>();
							for (PrescriptionItem prescriptionItem : items) {
								OPDPrescriptionItemResponse prescriptionItemDetail = new OPDPrescriptionItemResponse();
								BeanUtil.map(prescriptionItem, prescriptionItemDetail);
								DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
								if (drugCollection != null) {
									Drug drug = new Drug();
									BeanUtil.map(drugCollection, drug);
									prescriptionItemDetail.setId(drug.getId());

									prescriptionItemDetail.setDrugName(drug.getDrugName());
									if (drug.getDuration() != null) {
										DurationResponse duration = new DurationResponse();
										duration.setDurationUnit(drug.getDuration().getDurationUnit() != null
												? drug.getDuration().getDurationUnit().getUnit()
												: "");
										duration.setValue(drug.getDuration().getValue());
										prescriptionItemDetail.setDuration(duration);
									}
									if (drug.getDrugType() != null) {
										prescriptionItemDetail.setDrugType(drug.getDrugType().getType());
									}
									if (drug.getDirection() != null && !drug.getDirection().isEmpty()) {
										prescriptionItemDetail.setDirection(new ArrayList<String>());
										for (DrugDirection direction : drug.getDirection()) {
											prescriptionItemDetail.getDirection().add(direction.getDirection());
										}
									}

									if (drug.getGenericNames() != null && !drug.getGenericNames().isEmpty()) {
										prescriptionItemDetail.setGenericNames(new ArrayList<GenericCodeResponse>());
										for (GenericCode genericCode : drug.getGenericNames()) {
											GenericCodeResponse genericCodeResponse = new GenericCodeResponse();
											BeanUtil.map(genericCode, genericCodeResponse);
											prescriptionItemDetail.getGenericNames().add(genericCodeResponse);
										}
									}
									prescriptionItemDetails.add(prescriptionItemDetail);
								}
								prescription.setItems(prescriptionItemDetails);
							}
						} else {
							prescription.setItems(new ArrayList<OPDPrescriptionItemResponse>());
						}

						PatientVisitCollection patientVisitCollection = patientVisitRepository
								.findByPrescriptionId(collection.getPrescriptionCollection().getId());
						if (patientVisitCollection != null)
							prescription.setVisitId(patientVisitCollection.getId().toString());

						if (tests != null && !tests.isEmpty()) {
							List<OPDDiagnosticTestResponse> diagnosticTests = new ArrayList<OPDDiagnosticTestResponse>();
							for (TestAndRecordData data : tests) {
								if (data.getTestId() != null) {
									DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
											.findOne(data.getTestId());
									DiagnosticTest diagnosticTest = new DiagnosticTest();
									if (diagnosticTestCollection != null) {
										BeanUtil.map(diagnosticTestCollection, diagnosticTest);
									}
									if (!DPDoctorUtils.anyStringEmpty(data.getRecordId())) {
										diagnosticTests.add(new OPDDiagnosticTestResponse(diagnosticTest.getId(),
												diagnosticTest.getTestName(), data.getRecordId().toString()));
									} else {
										diagnosticTests.add(new OPDDiagnosticTestResponse(diagnosticTest.getId(),
												diagnosticTest.getTestName(), null));
									}

								}
							}
							prescription.setDiagnosticTests(diagnosticTests);
						} else {
							prescription.setDiagnosticTests(new ArrayList<OPDDiagnosticTestResponse>());
						}

						if (collection.getPrescriptionCollection().getAdvice() == null) {
							prescription.setAdvice("");
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
			int count = (int) mongoTemplate.count(new Query(criteria2), OPDReportsCollection.class);
			opdReportsResponse = new OPDReportsResponse();
			opdReportsResponse.setOpdReports(response);
			opdReportsResponse.setCount(count);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return opdReportsResponse;}

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
			Criteria criteria = new Criteria("isPatientDiscarded").ne(true);
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
				otReportsLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip(page * size), Aggregation.limit(size)), OTReportsCollection.class,
						OTReportsLookupResponse.class).getMappedResults();
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
			Criteria criteria = new Criteria("isPatientDiscarded").ne(true);
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
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
										Aggregation.unwind("doctor"),
										Aggregation.lookup("location_cl", "locationId", "_id", "location"),
										Aggregation.unwind("location"),
										Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
										Aggregation.unwind("hospital"),
										Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
										Aggregation.skip(page * size), Aggregation.limit(size)),
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
	@Transactional
	public ClinicalIndicator addClinicalIndicator(ClinicalIndicator request) {
		ClinicalIndicator response = null;
		try {
			ClinicalIndicatorCollection clinicalIndicatorCollection = null;
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
				}
				request.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				request.setCreatedTime(new Date());
				request.setUpdatedTime(new Date());

			} else {
				ClinicalIndicatorCollection oldclinicalIndicatorCollection = clinicalIndicatorRepository
						.findOne(new ObjectId(request.getId()));
				request.setCreatedBy(oldclinicalIndicatorCollection.getCreatedBy());
				request.setCreatedTime(oldclinicalIndicatorCollection.getCreatedTime());
				request.setUpdatedTime(new Date());
			}
			clinicalIndicatorCollection = new ClinicalIndicatorCollection();
			response = new ClinicalIndicator();
			BeanUtil.map(request, clinicalIndicatorCollection);
			clinicalIndicatorRepository.save(clinicalIndicatorCollection);
			BeanUtil.map(clinicalIndicatorCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while adding clinical Indicator ");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicalIndicator getClinicalIndicatorById(String indicatorId) {
		ClinicalIndicator response = null;
		try {
			ClinicalIndicatorCollection clinicalIndicatorCollection = clinicalIndicatorRepository
					.findOne(new ObjectId(indicatorId));
			response = new ClinicalIndicator();
			BeanUtil.map(clinicalIndicatorCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while getting by Id clinical Indicator ");
		}
		return response;
	}

	@Override
	@Transactional
	public List<ClinicalIndicator> getClinicalIndicators(int size, int page, String doctorId, String locationId,
			String hospitalId, boolean discarded, String type) {
		List<ClinicalIndicator> response = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId").is(locationId)
					.and(hospitalId).is(new ObjectId(hospitalId));
			criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("indicatortype").is(type.toUpperCase());
			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, ClinicalIndicatorCollection.class, ClinicalIndicator.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while getting clinical Indicators ");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicalIndicator discardClinicalIndicators(String indicatorId, boolean discarded) {
		ClinicalIndicator response = null;
		try {
			ClinicalIndicatorCollection clinicalIndicatorCollection = clinicalIndicatorRepository
					.findOne(new ObjectId(indicatorId));
			if (clinicalIndicatorCollection == null) {
				throw new BusinessException(ServiceError.NoRecord,
						"No any ClinicalIndicator record found for given indicatorId ");
			}
			clinicalIndicatorCollection.setDiscarded(discarded);
			clinicalIndicatorCollection.setUpdatedTime(new Date());
			clinicalIndicatorRepository.save(clinicalIndicatorCollection);
			response = new ClinicalIndicator();
			BeanUtil.map(clinicalIndicatorCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while discarding clinical Indicator ");
		}
		return response;
	}

	@Override
	@Transactional
	public EquipmentLogAMCAndServicingRegister addEquipmentLogAMCAndServicingRegister(
			EquipmentLogAMCAndServicingRegister request) {
		EquipmentLogAMCAndServicingRegister response = null;
		try {
			EquipmentLogAMCAndServicingRegisterCollection equipmentLogAMCAndServicingRegisterCollection = null;
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
				}
				request.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				request.setCreatedTime(new Date());
				request.setUpdatedTime(new Date());

			} else {
				EquipmentLogAMCAndServicingRegisterCollection oldequipmentLogAMCAndServicingRegisterCollection = equipmentLogAMCAndServicingRegisterRepository
						.findOne(new ObjectId(request.getId()));
				request.setCreatedBy(oldequipmentLogAMCAndServicingRegisterCollection.getCreatedBy());
				request.setCreatedTime(oldequipmentLogAMCAndServicingRegisterCollection.getCreatedTime());
				request.setUpdatedTime(new Date());
			}
			equipmentLogAMCAndServicingRegisterCollection = new EquipmentLogAMCAndServicingRegisterCollection();
			response = new EquipmentLogAMCAndServicingRegister();
			BeanUtil.map(request, equipmentLogAMCAndServicingRegisterCollection);
			equipmentLogAMCAndServicingRegisterRepository.save(equipmentLogAMCAndServicingRegisterCollection);
			BeanUtil.map(equipmentLogAMCAndServicingRegisterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while adding Equipment Log AMC And Servicing Register ");
		}
		return response;
	}

	@Override
	@Transactional
	public EquipmentLogAMCAndServicingRegister getEquipmentLogAMCAndServicingRegisterById(String registerid) {
		EquipmentLogAMCAndServicingRegister response = null;
		try {
			EquipmentLogAMCAndServicingRegisterCollection equipmentLogAMCAndServicingRegisterCollection = equipmentLogAMCAndServicingRegisterRepository
					.findOne(new ObjectId(registerid));
			response = new EquipmentLogAMCAndServicingRegister();
			BeanUtil.map(equipmentLogAMCAndServicingRegisterCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while getting by Id clinical Indicator ");
		}
		return response;
	}

	@Override
	@Transactional
	public List<EquipmentLogAMCAndServicingRegister> getEquipmentLogAMCAndServicingRegisters(int size, int page,
			String docterId, String locationId, String hospitalId, boolean discarded) {
		List<EquipmentLogAMCAndServicingRegister> response = null;
		try {
			Criteria criteria = new Criteria("docterId").is(new ObjectId(docterId)).and("locationId").is(locationId)
					.and(hospitalId).is(new ObjectId(hospitalId));
			criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, EquipmentLogAMCAndServicingRegisterCollection.class,
					EquipmentLogAMCAndServicingRegister.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while getting clinical Indicators ");
		}
		return response;
	}

	@Override
	@Transactional
	public EquipmentLogAMCAndServicingRegister discardEquipmentLogAMCAndServicingRegister(String registerId,
			boolean discarded) {
		EquipmentLogAMCAndServicingRegister response = null;
		try {
			EquipmentLogAMCAndServicingRegisterCollection equipmentLogAMCAndServicingRegisterCollection = equipmentLogAMCAndServicingRegisterRepository
					.findOne(new ObjectId(registerId));
			if (equipmentLogAMCAndServicingRegisterCollection == null) {
				throw new BusinessException(ServiceError.NoRecord,
						"No any Equipment Log AMC And Servicing Register found for given registerId ");
			}
			equipmentLogAMCAndServicingRegisterCollection.setDiscarded(discarded);
			equipmentLogAMCAndServicingRegisterCollection.setUpdatedTime(new Date());
			response = new EquipmentLogAMCAndServicingRegister();
			equipmentLogAMCAndServicingRegisterRepository.save(equipmentLogAMCAndServicingRegisterCollection);
			BeanUtil.map(equipmentLogAMCAndServicingRegisterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while discarding clinical Indicator ");
		}
		return response;
	}

	@Override
	@Transactional
	public RepairRecordsOrComplianceBook addRepairRecordsOrComplianceBook(RepairRecordsOrComplianceBook request) {
		RepairRecordsOrComplianceBook response = null;
		try {
			RepairRecordsOrComplianceBookCollection bookCollection = null;
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
				}
				request.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				request.setCreatedTime(new Date());
				request.setUpdatedTime(new Date());

			} else {
				RepairRecordsOrComplianceBookCollection oldComplianceBookCollection = repairRecordsOrComplianceBookRepository
						.findOne(new ObjectId(request.getId()));
				request.setCreatedBy(oldComplianceBookCollection.getCreatedBy());
				request.setCreatedTime(oldComplianceBookCollection.getCreatedTime());
				request.setUpdatedTime(new Date());
			}
			bookCollection = new RepairRecordsOrComplianceBookCollection();
			response = new RepairRecordsOrComplianceBook();
			BeanUtil.map(request, bookCollection);
			repairRecordsOrComplianceBookRepository.save(bookCollection);
			BeanUtil.map(bookCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while adding Repair Records Or Compliance Book");
		}
		return response;
	}

	@Override
	@Transactional
	public RepairRecordsOrComplianceBook getRepairRecordsOrComplianceBookById(String bookid) {
		RepairRecordsOrComplianceBook response = null;
		try {
			RepairRecordsOrComplianceBookCollection bookCollection = repairRecordsOrComplianceBookRepository
					.findOne(new ObjectId(bookid));
			response = new RepairRecordsOrComplianceBook();
			BeanUtil.map(bookCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while getting by Id Repair Records Or Compliance Book ");
		}
		return response;
	}

	@Override
	@Transactional
	public List<RepairRecordsOrComplianceBook> getRepairRecordsOrComplianceBooks(int size, int page, String docterId,
			String locationId, String hospitalId, boolean discarded) {
		List<RepairRecordsOrComplianceBook> response = null;
		try {
			Criteria criteria = new Criteria("docterId").is(new ObjectId(docterId)).and("locationId").is(locationId)
					.and(hospitalId).is(new ObjectId(hospitalId));
			criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, RepairRecordsOrComplianceBookCollection.class,
					RepairRecordsOrComplianceBook.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while getting Repair Records Or Compliance Books ");
		}
		return response;
	}

	@Override
	@Transactional
	public RepairRecordsOrComplianceBook discardrepairRecordsOrComplianceBook(String bookId, boolean discarded) {
		RepairRecordsOrComplianceBook response = null;
		try {
			RepairRecordsOrComplianceBookCollection repairRecordsOrComplianceBookCollection = repairRecordsOrComplianceBookRepository
					.findOne(new ObjectId(bookId));
			if (repairRecordsOrComplianceBookCollection == null) {
				throw new BusinessException(ServiceError.NoRecord,
						"No any Repair Records Or Compliance Book found for given registerId ");
			}
			response = new RepairRecordsOrComplianceBook();
			repairRecordsOrComplianceBookCollection.setDiscarded(discarded);
			repairRecordsOrComplianceBookCollection.setUpdatedTime(new Date());
			repairRecordsOrComplianceBookRepository.save(repairRecordsOrComplianceBookCollection);
			BeanUtil.map(repairRecordsOrComplianceBookCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while discarding Repair Records Or Compliance Book");
		}
		return response;
	}

	@Override
	@Transactional
	public BrokenAppointment addBrokenAppointment(BrokenAppointment request) {
		BrokenAppointment response = null;
		try {
			BroakenAppointmentCollection appointmentCollection = null;
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
				}
				request.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				request.setCreatedTime(new Date());
				request.setUpdatedTime(new Date());

			} else {
				BroakenAppointmentCollection oldroakenAppointmentCollection = brokenAppointmentRepository
						.findOne(new ObjectId(request.getId()));
				request.setCreatedBy(oldroakenAppointmentCollection.getCreatedBy());
				request.setCreatedTime(oldroakenAppointmentCollection.getCreatedTime());
				request.setUpdatedTime(new Date());
			}
			appointmentCollection = new BroakenAppointmentCollection();
			response = new BrokenAppointment();
			BeanUtil.map(request, appointmentCollection);
			brokenAppointmentRepository.save(appointmentCollection);
			BeanUtil.map(appointmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while adding Broken Appointment");
		}
		return response;
	}

	@Override
	@Transactional
	public BrokenAppointment getBrokenAppointment(String appointmentId) {
		BrokenAppointment response = null;
		try {
			BroakenAppointmentCollection appointmentCollection = brokenAppointmentRepository
					.findOne(new ObjectId(appointmentId));
			response = new BrokenAppointment();
			BeanUtil.map(appointmentCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Exception occure while getting by Id Broken Appointment ");
		}
		return response;
	}

	@Override
	@Transactional
	public List<BrokenAppointment> getBrokenAppointments(int size, int page, String docterId, String locationId,
			String hospitalId, boolean discarded) {
		List<BrokenAppointment> response = null;
		try {
			Criteria criteria = new Criteria("docterId").is(new ObjectId(docterId)).and("locationId").is(locationId)
					.and(hospitalId).is(new ObjectId(hospitalId));
			criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, BroakenAppointmentCollection.class, BrokenAppointment.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while getting Broken Appointment List");
		}
		return response;
	}

	@Override
	@Transactional
	public BrokenAppointment discardBrokenAppointment(String appointmentId, boolean discarded) {
		BrokenAppointment response = null;
		try {
			BroakenAppointmentCollection appointmentCollection = brokenAppointmentRepository
					.findOne(new ObjectId(appointmentId));
			if (appointmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord,
						"No any Broken Appointment found for given appointmentId ");
			}
			response = new BrokenAppointment();
			appointmentCollection.setDiscarded(discarded);
			appointmentCollection.setUpdatedTime(new Date());
			brokenAppointmentRepository.save(appointmentCollection);
			BeanUtil.map(appointmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occure while discarding  Broken Appointment");
		}
		return response;
	}

	@Override
	public String getOTReportsFile(String otId) {
		String response = null;
		try {
			List<OTReportsLookupResponse> otReportsLookupResponses = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(otId))),
									Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
									Aggregation.unwind("doctor"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("patient_cl", "patientId", "userId", "patientCollection"),
									new CustomAggregationOperation(new BasicDBObject("$unwind",
											new BasicDBObject("path", "$patientCollection")
													.append("preserveNullAndEmptyArrays", true))),
									new CustomAggregationOperation(
											new BasicDBObject("$redact",
													new BasicDBObject("$cond",
															new BasicDBObject("if", new BasicDBObject("$eq",
																	Arrays.asList("$patientCollection.locationId",
																			"$locationId"))).append("then", "$$KEEP")
																					.append("else", "$$PRUNE")))),

									Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
									Aggregation.unwind("patientUser")),
							OTReportsCollection.class, OTReportsLookupResponse.class)
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
		String nameAndCost = "";
		List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
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

		parameters.put("anaesthesiaType",
				(otReportsLookupResponse.getAnaesthesiaType() != null)
						? otReportsLookupResponse.getAnaesthesiaType().getAnaesthesiaType()
						: null);

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
		parameters.put("surgeryTitle",
				otReportsLookupResponse.getSurgery() != null ? otReportsLookupResponse.getSurgery().getTitle() : "");
		parameters.put("finalDiagnosis", otReportsLookupResponse.getFinalDiagnosis());
		if (otReportsLookupResponse.getOperatingSurgeonAndCost() != null) {
			nameAndCost = !DPDoctorUtils
					.anyStringEmpty(otReportsLookupResponse.getOperatingSurgeonAndCost().getDoctor())
							? otReportsLookupResponse.getOperatingSurgeonAndCost().getDoctor() 
							: null;
			if (otReportsLookupResponse.getOperatingSurgeonAndCost().getCost() > 0)
				nameAndCost = nameAndCost + "(Rs. "
						+ otReportsLookupResponse.getOperatingSurgeonAndCost().getCost().intValue() + ")";

		}
		parameters.put("operatingSurgeon", nameAndCost);
		nameAndCost = "";
		if (otReportsLookupResponse.getAnaesthetistAndCost() != null) {

			nameAndCost = !DPDoctorUtils.anyStringEmpty(otReportsLookupResponse.getAnaesthetistAndCost().getDoctor())
					? otReportsLookupResponse.getAnaesthetistAndCost().getDoctor() 
					: null;
			if (otReportsLookupResponse.getAnaesthetistAndCost().getCost() > 0)
				nameAndCost = nameAndCost + "(Rs. "
						+ otReportsLookupResponse.getAnaesthetistAndCost().getCost().intValue() + ")";

		}
		parameters.put("anaesthetist", nameAndCost);
		nameAndCost = "";
		if (otReportsLookupResponse.getAssitingDoctorsAndCost() != null) {
			for (DoctorAndCost doctorAndCost : otReportsLookupResponse.getAssitingDoctorsAndCost()) {
				if (DPDoctorUtils.anyStringEmpty(nameAndCost)) {
					nameAndCost = !DPDoctorUtils.anyStringEmpty(doctorAndCost.getDoctor())
							? doctorAndCost.getDoctor() 
							: null;
					if (doctorAndCost.getCost() > 0)
						nameAndCost = nameAndCost + "(Rs. " + doctorAndCost.getCost().intValue() + ")";

				} else {

					nameAndCost = nameAndCost + (!DPDoctorUtils.anyStringEmpty(doctorAndCost.getDoctor())
							? " , " + doctorAndCost.getDoctor() 
							: null);
					if (doctorAndCost.getCost() > 0)
						nameAndCost = nameAndCost + "(Rs. " + doctorAndCost.getCost().intValue() + ")";

				}
			}
		}

		parameters.put("assistingDoctor", nameAndCost);

		nameAndCost = "";
		if (otReportsLookupResponse.getAssitingNursesAndCost() != null) {
			for (DoctorAndCost doctorAndCost : otReportsLookupResponse.getAssitingNursesAndCost()) {
				if (DPDoctorUtils.anyStringEmpty(nameAndCost)) {
					nameAndCost = !DPDoctorUtils.anyStringEmpty(doctorAndCost.getDoctor())
							? doctorAndCost.getDoctor()
							: null;
					if (doctorAndCost.getCost() > 0)
						nameAndCost = nameAndCost + "(Rs. " + doctorAndCost.getCost().intValue() + ")";

				} else {

					nameAndCost = nameAndCost + (!DPDoctorUtils.anyStringEmpty(doctorAndCost.getDoctor())
							? " , " + doctorAndCost.getDoctor() 
							: null);
					if (doctorAndCost.getCost() > 0)
						nameAndCost = nameAndCost + "(Rs. " + doctorAndCost.getCost() + ")";

				}
			}
		}

		int no = 0;
		Boolean showIntructions = false, showDirection = false, showDrugQty = false;
		
		if (otReportsLookupResponse.getPostOperativeOrder() != null && !otReportsLookupResponse.getPostOperativeOrder().isEmpty())
			for (PrescriptionItemDetail prescriptionItem : otReportsLookupResponse.getPostOperativeOrder()) {
				if (prescriptionItem != null && prescriptionItem.getDrug() != null) {
					DrugCollection drug = drugRepository.findOne(new ObjectId(prescriptionItem.getDrug().getId()));
					if (drug != null) {
						String drugType = drug.getDrugType() != null
								? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() + " " : "")
								: "";
						String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
						String genericName = "";
						if (printSettings.getShowDrugGenericNames() && drug.getGenericNames() != null
								&& !drug.getGenericNames().isEmpty()) {
							for (GenericCode genericCode : drug.getGenericNames()) {
								if (DPDoctorUtils.anyStringEmpty(genericName))
									genericName = genericCode.getName();
								else
									genericName = genericName + "+" + genericCode.getName();
							}
							genericName = "<br><font size='1'><i>" + genericName + "</i></font>";
						}
						if (drug.getDrugTypePlacement() != null) {
							if (drug.getDrugTypePlacement().equalsIgnoreCase("PREFIX")) {
								drugName = (drugType + drugName) == "" ? "--"
										: drugType + " " + drugName + genericName;
							} else if (drug.getDrugTypePlacement().equalsIgnoreCase("SUFFIX")) {
								drugName = (drugType + drugName) == "" ? "--"
										: drugName + " " + drugType + genericName;
							}
						} else {
							drugName = (drugType + drugName) == "" ? "--" : drugType + " " + drugName + genericName;
						}
						// drugName = (drugType + drugName) == "" ? "--" : drugType + " " + drugName +
						// genericName;
						String durationValue = prescriptionItem.getDuration() != null
								? (prescriptionItem.getDuration().getValue() != null
										? prescriptionItem.getDuration().getValue()
										: "")
								: "";
						String durationUnit = prescriptionItem.getDuration() != null
								? (prescriptionItem.getDuration().getDurationUnit() != null
										? (!DPDoctorUtils.anyStringEmpty(
												prescriptionItem.getDuration().getDurationUnit().getUnit())
														? prescriptionItem.getDuration().getDurationUnit().getUnit()
														: "")
										: "")
								: "";

						String directions = "";
						if (prescriptionItem.getDirection() != null && !prescriptionItem.getDirection().isEmpty()) {
							showDirection = true;
							if (prescriptionItem.getDirection().get(0).getDirection() != null) {
								if (directions == "")
									directions = directions
											+ (prescriptionItem.getDirection().get(0).getDirection());
								else
									directions = directions + ","
											+ (prescriptionItem.getDirection().get(0).getDirection());
							}
						}
						if (!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())) {
							if (printSettings.getContentSetup() != null) {
								if (printSettings.getContentSetup().getInstructionAlign() != null && printSettings
										.getContentSetup().getInstructionAlign().equals(FieldAlign.HORIZONTAL)) {
									prescriptionItem.setInstructions(
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? "<b>Instruction </b>: " + prescriptionItem.getInstructions()
													: null);
								} else {
									prescriptionItem.setInstructions(
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? prescriptionItem.getInstructions()
													: null);
								}
							} else {
								prescriptionItem.setInstructions(
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
												? prescriptionItem.getInstructions()
												: null);
							}

							showIntructions = true;
						}
						String duration = "";
						if (durationValue == "" && durationValue == "")
							duration = "--";
						else
							duration = durationValue + " " + durationUnit;

						PrescriptionJasperDetails prescriptionJasperDetails = null;
						if (printSettings.getContentSetup() != null) {
							if (printSettings.getContentSetup().getInstructionAlign() != null && printSettings
									.getContentSetup().getInstructionAlign().equals(FieldAlign.HORIZONTAL)) {

								prescriptionJasperDetails = new PrescriptionJasperDetails(no, drugName,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
												? prescriptionItem.getDosage()
												: "--",
										duration, directions.isEmpty() ? "--" : directions,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
												? prescriptionItem.getInstructions()
												: null,
										genericName);
							} else {
								prescriptionJasperDetails = new PrescriptionJasperDetails(no, drugName,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
												? prescriptionItem.getDosage()
												: "--",
										duration, directions.isEmpty() ? "--" : directions,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
												? prescriptionItem.getInstructions()
												: "--",
										genericName);
							}
						} else {
							prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
									!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
											? prescriptionItem.getDosage()
											: "--",
									duration, directions.isEmpty() ? "--" : directions,
									!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
											? prescriptionItem.getInstructions()
											: "--",
									genericName);
						}
						if (prescriptionItem.getDrugQuantity() == null) {
							prescriptionJasperDetails.setDrugQuantity("0");
						} else {
							showDrugQty = true;
							prescriptionJasperDetails
									.setDrugQuantity(prescriptionItem.getDrugQuantity().toString());
						}
						prescriptionItems.add(prescriptionJasperDetails);
					}
				}
			}

		parameters.put("showIntructions", showIntructions);
		parameters.put("showDirection", showDirection);
		parameters.put("prescriptionItems", prescriptionItems);
		parameters.put("assistingNurse", nameAndCost);
		parameters.put("materialForHPE",
				otReportsLookupResponse.getMaterialForHPE() != null && otReportsLookupResponse.getMaterialForHPE()
						? "YES"
						: "NO");
		parameters.put("remarks", otReportsLookupResponse.getRemarks());
		parameters.put("operationalNotes", otReportsLookupResponse.getOperationalNotes());
		parameters.put("otReportsId", otReportsLookupResponse.getId());
		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails((printSettings != null
				&& printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>OT-ID: </b>" + (!DPDoctorUtils.anyStringEmpty(otReportsLookupResponse.getUniqueOTId())
						? otReportsLookupResponse.getUniqueOTId()
						: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				otReportsLookupResponse.getUpdatedTime() != null ? otReportsLookupResponse.getUpdatedTime()
						: new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());

		patientVisitService.generatePrintSetup(parameters, printSettings,
				new ObjectId(otReportsLookupResponse.getDoctorId()));
		String pdfName = (user != null ? user.getFirstName() : "") + "OTREPORTS-"
				+ (!DPDoctorUtils.anyStringEmpty(otReportsLookupResponse.getUniqueOTId())
						? otReportsLookupResponse.getUniqueOTId()
						: "")
				+ new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
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
									new BasicDBObject("$cond", new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$patientCollection.locationId", "$locationId")))
															.append("then", "$$KEEP").append("else", "$$PRUNE")))),

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
		patientVisitService.generatePatientDetails((printSettings != null
				&& printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>DR-ID: </b>" + (!DPDoctorUtils.anyStringEmpty(deliveryReportsLookupResponse.getUniqueDRId())
						? deliveryReportsLookupResponse.getUniqueDRId()
						: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				deliveryReportsLookupResponse.getUpdatedTime() != null ? deliveryReportsLookupResponse.getUpdatedTime()
						: new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());

		patientVisitService.generatePrintSetup(parameters, printSettings,
				new ObjectId(deliveryReportsLookupResponse.getDoctorId()));
		String pdfName = (user != null ? user.getFirstName() : "") + "DELIVERYREPORTS-"
				+ (!DPDoctorUtils.anyStringEmpty(deliveryReportsLookupResponse.getUniqueDRId())
						? deliveryReportsLookupResponse.getUniqueDRId()
						: "")
				+ new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.DELIVERY_REPORTS, parameters, deliveryReportsFileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		return response;
	}

	@Override
	@Transactional
	public Boolean updateOTReports() {
		Boolean response = false;
		try {
			List<OTReportsCollection> otReportsCollections = otReportsRepository.findAll();
			for (OTReportsCollection otReportsCollection : otReportsCollections) {
				if (otReportsCollection.getAssitingDoctorsAndCost() == null) {
					if (otReportsCollection.getAssitingDoctors() != null) {
						List<DoctorAndCost> doctorAndCosts = new ArrayList<>();
						for (String assistingDoctors : otReportsCollection.getAssitingDoctors()) {
							DoctorAndCost doctorAndCost = new DoctorAndCost();
							doctorAndCost.setDoctor(assistingDoctors);
							doctorAndCosts.add(doctorAndCost);
						}
						otReportsCollection.setAssitingDoctorsAndCost(doctorAndCosts);
					}
				}

				if (otReportsCollection.getAssitingNursesAndCost() == null) {
					if (otReportsCollection.getAssitingNurses() != null) {
						List<DoctorAndCost> doctorAndCosts = new ArrayList<>();
						for (String assistingDoctors : otReportsCollection.getAssitingNurses()) {
							DoctorAndCost doctorAndCost = new DoctorAndCost();
							doctorAndCost.setDoctor(assistingDoctors);
							doctorAndCosts.add(doctorAndCost);
						}
						otReportsCollection.setAssitingNursesAndCost(doctorAndCosts);
					}
				}

				if (otReportsCollection.getAnaesthetistAndCost() == null) {
					if (otReportsCollection.getAnaesthetist() != null) {
						DoctorAndCost doctorAndCost = new DoctorAndCost();
						doctorAndCost.setDoctor(otReportsCollection.getAnaesthetist());
						otReportsCollection.setAnaesthetistAndCost(doctorAndCost);
					}
				}

				if (otReportsCollection.getOperatingSurgeonAndCost() == null) {
					if (otReportsCollection.getOperatingSurgeon() != null) {
						DoctorAndCost doctorAndCost = new DoctorAndCost();
						doctorAndCost.setDoctor(otReportsCollection.getOperatingSurgeon());
						otReportsCollection.setOperatingSurgeonAndCost(doctorAndCost);
					}
				}

			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}
