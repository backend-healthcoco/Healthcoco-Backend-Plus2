package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientCollection;

@Repository
public interface PatientRepository extends MongoRepository<PatientCollection, ObjectId> {
	
	List<PatientCollection> findByUserId(ObjectId userId);

	PatientCollection findByUserIdAndDoctorIdAndLocationIdAndHospitalId(ObjectId userId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	PatientCollection findByUserIdAndLocationIdAndHospitalId(ObjectId userId, ObjectId locationId, ObjectId hospitalId);

	PatientCollection findByUserIdAndLocationIdAndHospitalIdAndDiscarded(ObjectId userId, ObjectId locationId, ObjectId hospitalId,Boolean discarded);

	List<PatientCollection> findByDoctorIdAndUpdatedTimeGreaterThan(ObjectId doctorId, Date date, Sort sort);
	
	PatientCollection findByUserIdAndDoctorId(ObjectId patientId, ObjectId doctorId);
	
	List<PatientCollection> findByLocationIdAndHospitalIdAndPNUMNull(ObjectId locationId, ObjectId hospitalId);
	
	@Query("{'locationId': ?0, 'hospitalId': ?1, 'PNUM': ?2}")
	PatientCollection findByLocationIdAndHospitalIdAndPNUM(ObjectId locationId, ObjectId hospitalId, String pnum);

	List<PatientCollection> findByLocationIdAndHospitalIdAndRegistrationDateBetween(ObjectId locationId, ObjectId hospitalId, Long startTimeinMillis, Long endTimeinMillis, Pageable pageRequest);
	
	@Query("{'locationId': ?0, 'hospitalId': ?1, 'PNUM': {$ne: null}}")
	PatientCollection findByLocationIdAndHospitalIdAndPNUMNotNull(ObjectId locationObjectId, ObjectId hospitalObjectId, Sort sort);

	@Query("{'locationId': ?0, 'hospitalId': ?1, 'PNUM': {$ne: null}}")
	List<PatientCollection> findByLocationIdAndHospitalIdAndPNUMNotNull(ObjectId locationObjectId, ObjectId hospitalObjectId, Pageable pageRequest);
	
	@Query(value = "{'userId': ?0, 'locationId': ?1, 'hospitalId': ?2}", count = true)
	Integer findCount(ObjectId userId, ObjectId locationId, ObjectId hospitalId);

	@Query(value = "{'locationId':?0, 'hospitalId':?1, 'registrationDate' : {'$gt' : ?2, '$lte' : ?3}}", count = true)
	Integer findTodaysRegisteredPatient(ObjectId locationId, ObjectId hospitalId, Long start, Long end);

	@Query(value = "{'doctorId':?0, 'locationId':?1, 'hospitalId': ?2, 'PID':?3}", count = true)
	Integer findPatientByPID(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String generatedId);

	@Query(value = "{'doctorId':?0, 'locationId':?1, 'hospitalId': ?2, 'PID':?3, 'userId': {'$ne' : ?4}}", count = true)
	Integer findPatientByPID(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String generatedId, ObjectId userId);
	
	@Query(value = "{'locationId': ?0, 'hospitalId': ?1, 'pNUM': {'$ne' : ?2}}", count = true)
	Integer findCountByLocationIDHospitalIDAndNotPNUM(ObjectId locationId, ObjectId hospitalId, String pnum);

	List<PatientCollection> findByHealthIdAndLocalPatientNameAndGender(String patientId, String patientName, String gender);

	PatientCollection findByHealthId(String id);

}
