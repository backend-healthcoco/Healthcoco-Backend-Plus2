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
    PatientCollection findByUserIdDoctorIdLocationIdAndHospitalId(ObjectId userId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

    @Query(value="{'userId': ?0,'doctorId': ?1,'locationId': ?2,'hospitalId': ?3}", count = true)
    Integer findCount(ObjectId userId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}}")
    List<PatientCollection> findByDoctorId(ObjectId doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2, 'updatedTime': {'$gt': ?3}}")
    List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    @Query(value ="{'doctorId':?0, 'locationId':?1, 'hospitalId':?2, 'createdTime' : {'$gt' : ?3, '$lte' : ?4}}", count = true)
    Integer findTodaysRegisteredPatient(ObjectId doctorId, ObjectId location, ObjectId hospitalId, DateTime start, DateTime end);
    
//    @Query(value ="{'locationId':?0, 'hospitalId':?1, 'createdTime' : {'$gt' : ?2, '$lte' : ?3}}", count = true)
//    Integer findTodaysRegisteredPatient(ObjectId locationId, ObjectId hospitalId, DateTime start, DateTime end);

    @Query("{'userId': {'$in': ?0}, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded':{'$in': ?4}}")
    List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(Collection<ObjectId> patientIds, ObjectId locationId, ObjectId hospitalId,
	    Date date, boolean[] discards, Pageable pageRequest);

    @Query("{'userId': {'$in': ?0}, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded':{'$in': ?4}}")
    List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(Collection<ObjectId> patientIds, ObjectId locationId, ObjectId hospitalId,
    		Date date, boolean[] discards, Sort sort);

    @Query("{'userId': {'$in': ?0}, 'doctorId':?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}")
    List<PatientCollection> findByUserIdDoctorId(Collection<ObjectId> patientIds, ObjectId doctorId, Date date, boolean[] discards, Pageable pageRequest);

    @Query("{'userId': {'$in': ?0}, 'doctorId':?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}")
    List<PatientCollection> findByUserIdDoctorId(Collection<ObjectId> patientIds, ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}")
    List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(ObjectId locationId, ObjectId hospitalId,
	    Date date, boolean[] discards, Pageable pageRequest);

    @Query("{'locationId': ?0,'hospitalId': ?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}")
    List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(ObjectId locationId, ObjectId hospitalId,
    		Date date, boolean[] discards, Sort sort);

    @Query("{'doctorId':?0, 'updatedTime': {'$gt': ?1}, 'discarded':{'$in': ?2}}")
    List<PatientCollection> findByUserIdDoctorId(ObjectId doctorId, Date date, boolean[] discards, Pageable pageRequest);

    @Query("{'doctorId':?0, 'updatedTime': {'$gt': ?1}, 'discarded':{'$in': ?2}}")
    List<PatientCollection> findByUserIdDoctorId(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

    @Query("{'userId': ?0, 'doctorId':?1}")
    PatientCollection findByUserIdDoctorId(ObjectId patientId, ObjectId doctorId);

    @Query(value = "{'doctorId':?0, 'locationId':?1, 'PID':?2}", count = true)
    Integer findPatientByPID(ObjectId doctorId, ObjectId locationId, String generatedId);

    @Query(value = "{'userId': {'$in': ?0}, 'locationId': ?1, 'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded':{'$in': ?4}}", count = true)
	Integer findByUserIdDoctorIdLocationIdHospitalId(Collection<ObjectId> patientIds, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards);

    @Query(value = "{'userId': {'$in': ?0}, 'doctorId':?1, 'updatedTime': {'$gt': ?2}, 'discarded':{'$in': ?3}}", count = true)
	Integer findByUserIdDoctorId(Collection<ObjectId> patientIds, ObjectId doctorId, Date date, boolean[] discards);

    @Query(value = "{'doctorId':?0,'locationId': ?1,'hospitalId': ?2, 'updatedTime': {'$gt': ?3}, 'discarded':{'$in': ?4}}", count = true)
	Integer findByUserIdDoctorIdLocationIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards);

    @Query(value = "{'doctorId':?0, 'updatedTime': {'$gt': ?1}, 'discarded':{'$in': ?2}}", count = true)
	Integer findByUserIdDoctorId(ObjectId doctorId, Date date, boolean[] discards);

  }
