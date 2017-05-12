package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.dpdocter.collections.UserCollection;
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
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DeliveryReportsLookupResponse;
import com.dpdocter.response.DeliveryReportsResponse;
import com.dpdocter.response.IPDReportLookupResponse;
import com.dpdocter.response.IPDReportsResponse;
import com.dpdocter.response.OPDReportsLookupResponse;
import com.dpdocter.response.OPDReportsResponse;
import com.dpdocter.response.OTReportsLookupResponse;
import com.dpdocter.response.OTReportsResponse;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.ReportsService;

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

	@Override
	@Transactional
	public IPDReports submitIPDReport(IPDReports ipdReports) {
		IPDReports response = null;
		IPDReportsCollection ipdReportsCollection = new IPDReportsCollection();
		UserCollection userCollection = userRepository.findOne(new ObjectId(ipdReports.getDoctorId()));

		if (ipdReports != null) {
			BeanUtil.map(ipdReports, ipdReportsCollection);
			try {
				ipdReportsCollection.setCreatedTime(new Date());
				ipdReportsCollection.setCreatedBy(userCollection.getFirstName() + " " + userCollection.getLastName());
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
		OPDReportsCollection opdReportsCollection = new OPDReportsCollection();
		
		if (opdReports != null) {
			OPDReportsCollection opdReportsCollectionOld = opdReportsRepository
					.getOPDReportByPrescriptionId(new ObjectId(opdReports.getPrescriptionId()));
			if (opdReportsCollectionOld != null) {
				BeanUtil.map(opdReportsCollectionOld, opdReportsCollection);
				opdReportsCollection.setAmountReceived(opdReports.getAmountReceived());
				if(opdReports.getReceiptDate() != null){
					opdReportsCollection.setReceiptDate(new Date(opdReports.getReceiptDate()));	
				}else{
					opdReportsCollection.setReceiptDate(new Date());
				}
				opdReportsCollection.setReceiptNo(opdReports.getReceiptNo());
				opdReportsCollection.setRemarks(opdReports.getRemarks());
				opdReportsCollection.setUpdatedTime(new Date());
			} else {
				UserCollection userCollection = userRepository.findOne(new ObjectId(opdReports.getDoctorId()));
				BeanUtil.map(opdReports, opdReportsCollection);
				opdReportsCollection.setCreatedBy(userCollection.getFirstName() + " " + userCollection.getLastName());
				opdReportsCollection.setCreatedTime(new Date());
			}
			
			opdReportsCollection = opdReportsRepository.save(opdReportsCollection);
			try {

				if (opdReportsCollection != null) {
					response = new OPDReports();
					BeanUtil.map(opdReportsCollection, response);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error occured while creating OPD Records");
				throw new BusinessException(ServiceError.Unknown, "Error occured while OPD Records");
			}
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
				otReportsCollection.setCreatedTime(new Date());
				otReportsCollection.setCreatedBy(userCollection.getFirstName() + " " + userCollection.getLastName());
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
				deliveryReportsCollection.setCreatedTime(new Date());
				deliveryReports.setCreatedBy(userCollection.getFirstName() + " " + userCollection.getLastName());
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

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);

				criteria.and("createdTime").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);

				criteria.and("createdTime").lte(toTime);
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
			int count = ipdReportsRepository.getReportsCount(new ObjectId(locationId), new ObjectId(doctorId));
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

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);

				criteria.and("createdTime").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);

				criteria.and("createdTime").lte(toTime);
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
			int count = opdReportsRepository.getReportsCount(new ObjectId(locationId), new ObjectId(doctorId));
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

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);

				criteria.and("createdTime").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);

				criteria.and("createdTime").lte(toTime);
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
			int count = otReportsRepository.getReportsCount(new ObjectId(locationId), new ObjectId(doctorId));
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

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);

				criteria.and("createdTime").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);

				criteria.and("createdTime").lte(toTime);
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
			int count = deliveryReportsRepository.getReportsCount(new ObjectId(locationId),
					new ObjectId(doctorId));
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
	public OPDReports getOPDReportByVisitId(String visitId)
	{
		OPDReports response = null;
		OPDReportsCollection opdReportsCollection = opdReportsRepository.getOPDReportByVisitId(new ObjectId(visitId));
		if(opdReportsCollection != null)
		{
			response = new OPDReports();
			BeanUtil.map(opdReportsCollection, response);
		}
		return response;
	}
}
