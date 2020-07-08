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
public interface SMSTrackRepository extends MongoRepository<SMSTrackDetail, ObjectId>, PagingAndSortingRepository<SMSTrackDetail, ObjectId> {

	//@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'smsDetails.userId': ?3, 'type':{$in: ?4}, createdTime:{$gte :?5, $lte :?6}}")
	List<SMSTrackDetail> findByDoctorIdAndLocationIdAndHospitalIdAndSmsDetailsUserIdAndTypeInAndCreatedTimeBetween(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, ObjectId patientId, String[] type, Date time, Date date, Pageable pageRequest);

	List<SMSTrackDetail> findByLocationIdAndHospitalIdAndTypeIn(ObjectId locationId, ObjectId hospitalId, String[] type,
			Pageable pageable);

	List<SMSTrackDetail> findByLocationIdAndHospitalIdAndTypeIn(ObjectId locationId, ObjectId hospitalId, String[] type, Sort sort);

	List<SMSTrackDetail> findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			String[] type, Pageable pageable);

	List<SMSTrackDetail> findByDoctorIdAndLocationIdAndHospitalIdAndTypeIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			String[] type, Sort sort);

	@Query(value = "{'doctorId' : ?0, 'locationId': ?1, 'hospitalId' : ?2}", count = true)
	Integer getDoctorsSMSCount(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	SMSTrackDetail findByResponseId(String responseId);

	SMSTrackDetail findByDoctorIdAndLocationIdAndCreatedTime(ObjectId doctorId, ObjectId locationId,Date date);

}
