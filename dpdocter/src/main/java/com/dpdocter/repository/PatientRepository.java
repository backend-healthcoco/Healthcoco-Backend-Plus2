package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientCollection;

@Repository
public interface PatientRepository extends MongoRepository<PatientCollection, ObjectId> {
	@Query("{'userId': ?0}")
	List<PatientCollection> findByUserId(ObjectId userId);

	@Query("{'userId': {'$in': ?0}}")
	List<PatientCollection> findByUserId(List<ObjectId> userIds);

	@Query("{'userId': ?0,'doctorId': ?1,'locationId': ?2,'hospitalId': ?3}")
	PatientCollection findByUserIdDoctorIdLocationIdAndHospitalId(ObjectId userId, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);

	@Query("{'userId': ?0,'locationId': ?1,'hospitalId': ?2}")
	PatientCollection findByUserIdLocationIdAndHospitalId(ObjectId userId, ObjectId locationId, ObjectId hospitalId);
	
	PatientCollection findByUserIdAndLocationIdAndHospitalId(ObjectId userId, ObjectId locationId, ObjectId hospitalId);

	@Query("{'userId': ?0,'locationId': ?1,'hospitalId': ?2,'discarded': ?3}")
	PatientCollection findByUserIdLocationIdAndHospitalId(ObjectId userId, ObjectId locationId, ObjectId hospitalId,Boolean discarded);

	@Query(value = "{'userId': ?0, 'locationId': ?1, 'hospitalId': ?2}", count = true)
	Integer findCount(ObjectId userId, ObjectId locationId, ObjectId hospitalId);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}}")
	List<PatientCollection> findByDoctorId(ObjectId doctorId, Date date, Sort sort);

	@Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2, 'updatedTime': {'$gt': ?3}}")
	List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, Date date, Sort sort);

	@Query(value = "{'locationId':?0, 'hospitalId':?1, 'registrationDate' : {'$gt' : ?2, '$lte' : ?3}}", count = true)
	Integer findTodaysRegisteredPatient(ObjectId locationId, ObjectId hospitalId, DateTime start, DateTime end);

	@Query(value = "{'locationId':?0, 'hospitalId':?1, 'registrationDate' : {'$gt' : ?2, '$lte' : ?3}}", count = true)
	Integer findTodaysRegisteredPatient(ObjectId locationId, ObjectId hospitalId, Long start, Long end);

	@Query("{'doctorId':?0,'locationId': ?1, 'discarded':'true','hospitalId':?2}")
	List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

	@Query("{'userId': {'$in': ?0}, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded':{'$in': ?5}}")
	List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(Collection<ObjectId> patientIds, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId);

	@Query("{'userId': {'$in': ?0}, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded':{'$in': ?5}}")
	List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(Collection<ObjectId> patientIds, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

	@Query("{'userId': {'$in': ?0}, 'doctorId':?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}")
	List<PatientCollection> findByUserIdDoctorId(Collection<ObjectId> patientIds, ObjectId doctorId, Date date,
			boolean[] discards, Pageable pageRequest);

	@Query("{'userId': {'$in': ?0}, 'doctorId':?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}")
	List<PatientCollection> findByUserIdDoctorId(Collection<ObjectId> patientIds, ObjectId doctorId, Date date,
			boolean[] discards, Sort sort);

	@Query("{'doctorId':?0,'locationId': ?1,'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded':{'$in': ?4}}")
	List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, Date date, boolean[] discards, Pageable pageRequest);

	@Query("{'doctorId':?0,'locationId': ?1,'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded':{'$in': ?4}}")
	List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, Date date, boolean[] discards, Sort sort);

	@Query("{'doctorId':?0, 'updatedTime': {'$gt': ?1}, 'discarded':{'$in': ?2}}")
	List<PatientCollection> findByUserIdDoctorId(ObjectId doctorId, Date date, boolean[] discards,
			Pageable pageRequest);

	@Query("{'doctorId':?0, 'updatedTime': {'$gt': ?1}, 'discarded':{'$in': ?2}}")
	List<PatientCollection> findByUserIdDoctorId(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	@Query("{'userId': ?0, 'doctorId':?1}")
	PatientCollection findByUserIdDoctorId(ObjectId patientId, ObjectId doctorId);

	@Query(value = "{'doctorId':?0, 'locationId':?1, 'hospitalId': ?2, 'PID':?3}", count = true)
	Integer findPatientByPID(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String generatedId);

	@Query(value = "{'doctorId':?0, 'locationId':?1, 'hospitalId': ?2, 'PID':?3, 'userId': {'$ne' : ?4}}", count = true)
	Integer findPatientByPID(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String generatedId, ObjectId userId);
	
	@Query(value = "{'userId': {'$in': ?0}, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3, 'updatedTime': {'$gt': ?4}, 'discarded':{'$in': ?5}}", count = true)
	Integer findByUserIdDoctorIdLocationIdHospitalId(Collection<ObjectId> patientIds, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards);

	@Query(value = "{'userId': {'$in': ?0}, 'doctorId':?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}", count = true)
	Integer findByUserIdDoctorId(Collection<ObjectId> patientIds, ObjectId doctorId, Date date, boolean[] discards);

	@Query(value = "{'doctorId':?0,'locationId': ?1,'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded':{'$in': ?4}}", count = true)
	Integer findByUserIdDoctorIdLocationIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			Date date, boolean[] discards);

	@Query(value = "{'doctorId':?0, 'updatedTime': {'$gt': ?1}, 'discarded':{'$in': ?2}}", count = true)
	Integer findByUserIdDoctorId(ObjectId doctorId, Date date, boolean[] discards);

	@Query("{'PID':?0}")
	PatientCollection getByPID(String PID);

	@Query("{'createdTime':{'$gte': ?0}}")
	List<PatientCollection> findbyRegistrationDate(Date createdTime, Sort sort);

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'PNUM': null}")
	List<PatientCollection> findByLocationIDHospitalIDAndNullPNUM(ObjectId locationId, ObjectId hospitalId);

	@Query(value = "{'locationId': ?0, 'hospitalId': ?1, 'PNUM': {'$ne' : ?2}}", count = true)
	Integer findCountByLocationIDHospitalIDAndNotPNUM(ObjectId locationId, ObjectId hospitalId, String pnum);

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'PNUM': ?2}")
	PatientCollection findByLocationIDHospitalIDAndPNUM(ObjectId locationId, ObjectId hospitalId, String pnum);

	@Query(value = "{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}", count = true)
	Integer findCountByDoctorIdLocationIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			boolean discarded);

	@Query(value = "{'consultantDoctorIds': ?0, 'locationId': ?1, 'hospitalId': ?2, 'discarded': ?3}", count = true)
	Integer findCountByConsultantDoctorIdLocationIdHospitalId(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, boolean discarded);

	@Query("{'locationId':?0, 'hospitalId':?1, 'registrationDate' : {'$gt' : ?2, '$lte' : ?3}}")
	List<PatientCollection> findTodaysRegisteredPatient(ObjectId locationId, ObjectId hospitalId, Long startTimeinMillis, Long endTimeinMillis, Pageable pageRequest);
	
	@Query("{'locationId':?0, 'hospitalId':?1, 'registrationDate' : {'$gt' : ?2, '$lte' : ?3}}")
	List<PatientCollection> findTodaysRegisteredPatient(ObjectId locationId, ObjectId hospitalId, Long startTimeinMillis, Long endTimeinMillis, Sort sort);
	
	@Query(value = "{'locationId':?0, 'hospitalId':?1}", count = true)
	Integer countRegisteredPatient(ObjectId locationId, ObjectId hospitalId);

	@Query("{'locationId':?0, 'hospitalId':?1, 'PNUM':{'$ne': null}}")
	PatientCollection findLastRegisteredPatientWithPNUM(ObjectId locationObjectId, ObjectId hospitalObjectId, Sort sort);

}
