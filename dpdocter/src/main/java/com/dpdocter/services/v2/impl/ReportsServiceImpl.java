package com.dpdocter.services.v2.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.dpdocter.beans.TimeDuration;
import com.dpdocter.beans.v2.DeliveryReports;
import com.dpdocter.beans.v2.IPDReports;
import com.dpdocter.beans.v2.OTReports;
import com.dpdocter.beans.v2.PatientCard;
import com.dpdocter.collections.DeliveryReportsCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.IPDReportsCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DeliveryReportsRepository;
import com.dpdocter.repository.EquipmentLogAMCAndServicingRegisterRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.IPDReportsRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OPDReportsRepository;
import com.dpdocter.repository.OTReportsRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RepairRecordsOrComplianceBookRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DeliveryReportsLookupResponse;
import com.dpdocter.response.IPDReportLookupResponse;
import com.dpdocter.response.OTReportsLookupResponse;
import com.dpdocter.response.v2.DeliveryReportsResponse;
import com.dpdocter.response.v2.IPDReportsResponse;
import com.dpdocter.response.v2.OTReportsResponse;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.v2.ReportsService;

import common.util.web.DPDoctorUtils;

@Service(value = "ReportsServiceImplV2")
public class ReportsServiceImpl implements ReportsService {

	private static Logger logger = Logger.getLogger(ReportsServiceImpl.class.getName());

	@Autowired
	RepairRecordsOrComplianceBookRepository repairRecordsOrComplianceBookRepository;

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

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.ot.reports.fileName}")
	private String OTReportsFileName;

	@Value(value = "${jasper.print.delivery.reports.fileName}")
	private String deliveryReportsFileName;

	

	@Override
	@Transactional
	public IPDReportsResponse getIPDReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, Boolean discarded) {
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
			
			if (!discarded)
				criteria.and("discarded").is(discarded);
			
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
							PatientCard patient = new PatientCard();
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
	public OTReportsResponse getOTReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, Boolean discarded) {
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

			if (!discarded)
				criteria.and("discarded").is(discarded);
			
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
							PatientCard patient = new PatientCard();
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
			String from, String to, int page, int size, String updatedTime, Boolean discarded) {
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

			if (!discarded)
				criteria.and("discarded").is(discarded);
			
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
							PatientCard patient = new PatientCard();
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

	
}
