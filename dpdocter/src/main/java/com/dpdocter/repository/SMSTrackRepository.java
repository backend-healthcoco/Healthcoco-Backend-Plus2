package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SMSTrackDetail;

@Repository
public interface SMSTrackRepository
		extends MongoRepository<SMSTrackDetail, ObjectId>, PagingAndSortingRepository<SMSTrackDetail, ObjectId> {

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'type':{$in: ?2}}")
	List<SMSTrackDetail> findByLocationHospitalId(ObjectId locationId, ObjectId hospitalId, String[] type,
			Pageable pageable);

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'type':{$in: ?2}}")
	List<SMSTrackDetail> findByLocationHospitalId(ObjectId locationId, ObjectId hospitalId, String[] type, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'type':{$in: ?3}}")
	List<SMSTrackDetail> findByDoctorLocationHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			String[] type, Pageable pageable);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'type':{$in: ?3}}")
	List<SMSTrackDetail> findByDoctorLocationHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			String[] type, Sort sort);

	@Query(value = "{'doctorId' : ?0, 'locationId': ?1, 'hospitalId' : ?2}", count = true)
	Integer getDoctorsSMSCount(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	@Query("{'type':{$in: ?0}}")
	List<SMSTrackDetail> findByType(String[] type, Pageable pageRequest);

	@Query("{'type':{$in: ?0}}")
	List<SMSTrackDetail> findByType(String[] type, Sort sort);

	@Query("{'doctorId': ?0}")
	List<SMSTrackDetail> findAll(ObjectId doctorId, Pageable pageRequest);

	@Query("{'doctorId': ?0}")
	List<SMSTrackDetail> findAll(ObjectId doctorId, Sort sort);

	@Query("{'responseId': ?0}")
	SMSTrackDetail findByResponseId(String requestId);

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'smsDetails.userId': ?2, 'type':{$in: ?3}}")
	List<SMSTrackDetail> findByLocationHospitalPatientId(ObjectId locationId, ObjectId hospitalId, ObjectId patientId,
			String[] type, Pageable pageable);

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'smsDetails.userId': ?2, 'type':{$in: ?3}}")
	List<SMSTrackDetail> findByLocationHospitalPatientId(ObjectId locationId, ObjectId hospitalId, ObjectId patientId,
			String[] type, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'smsDetails.userId': ?3, 'type':{$in: ?4}}")
	List<SMSTrackDetail> findByDoctorLocationHospitalPatient(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, ObjectId patientId, String[] type, Pageable pageable);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'smsDetails.userId': ?3, 'type':{$in: ?4}}")
	List<SMSTrackDetail> findByDoctorLocationHospitalPatient(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, ObjectId patientId, String[] type, Sort sort);

	@Query("{'doctorId': ?0, 'smsDetails.userId': ?1, 'type':{$in: ?2}}")
	List<SMSTrackDetail> findByDoctorPatient(ObjectId doctorId, ObjectId patientId, String[] type, Pageable pageable);

	@Query("{'doctorId': ?0, 'smsDetails.userId': ?1, 'type':{$in: ?2}}")
	List<SMSTrackDetail> findByDoctorPatient(ObjectId doctorId, ObjectId patientId, String[] type, Sort sort);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'smsDetails.userId': ?3, 'type':{$in: ?4}, createdTime:{$gte :?5, $lte :?6}}")
	List<SMSTrackDetail> findByDoctorLocationHospitalPatient(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, ObjectId patientId, String[] type, Date time, Date date, Pageable pageRequest);

}
