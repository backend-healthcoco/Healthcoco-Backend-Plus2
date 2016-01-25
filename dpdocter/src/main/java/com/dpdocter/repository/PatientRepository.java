package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientCollection;

@Repository
public interface PatientRepository extends MongoRepository<PatientCollection, String> {
    @Query("{'userId': ?0}")
    List<PatientCollection> findByUserId(String userId);

    @Query("{'userId': {'$in': ?0}}")
    List<PatientCollection> findByUserId(List<String> userIds);

    @Query("{'userId': ?0,'doctorId': ?1,'locationId': ?2,'hospitalId': ?3}")
    PatientCollection findByUserIdDoctorIdLocationIdAndHospitalId(String userId, String doctorId, String locationId, String hospitalId);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}}")
    List<PatientCollection> findByDoctorId(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2, 'updatedTime': {'$gt': ?3}}")
    List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2,'createdTime' : {'$gt' : ?3, '$lte' : ?4}}")
    List<PatientCollection> findTodaysRegisteredPatient(String doctorId, String location, String hospitalId, DateTime start, DateTime end);

    @Query("{'userId': {'$in': ?0}, 'doctorId':?1}")
    List<PatientCollection> findByUserIdDoctorId(Collection<String> patientIds, String doctorId, Sort sort);

    @Query("{'userId': {'$in': ?0}, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3}")
    List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(Collection<String> patientIds, String doctorId, String locationId, String hospitalId, Pageable pageRequest);

    @Query("{'userId': {'$in': ?0}, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3}")
    List<PatientCollection> findByUserIdDoctorIdLocationIdHospitalId(Collection<String> patientIds, String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'userId': {'$in': ?0}, 'doctorId':?1}")
    List<PatientCollection> findByUserIdDoctorId(Collection<String> patientIds, String doctorId, Pageable pageRequest);

    @Query("{'userId': ?0, 'doctorId':?1}")
    PatientCollection findByUserIdDoctorId(String patientId, String doctorId);

    // @Query("{'userId': ?0, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3}")
    // PatientCollection findByUserIdDoctorIdLocationIdHospitalId(String
    // patientId, String doctorId, String locationId, String hospitalId,
    // Pageable pageRequest);
    //
    // @Query("{'userId': ?0, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3}")
    // PatientCollection findByUserIdDoctorIdLocationIdHospitalId(String
    // patientId, String doctorId, String locationId, String hospitalId, Sort
    // sort);

    @Query(value = "{'doctorId':?0, 'locationId':?1, 'registrationDate' : {'$gt' : ?2, '$lt' : ?3}}", count = true)
	Integer findTodaysRegisteredPatient(String doctorId, String locationId, Long from, Long to);

    @Query(value = "{'doctorId':?0, 'locationId':?1, 'PID':?2}", count = true)
    Integer findPatientByPID(String doctorId, String locationId, String generatedId);

//    @Query("{'userId': ?0, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3}")
//	PatientCollection findByUserIdDoctorIdLocationIdHospitalId(String patientId, String doctorId, String locationId, String hospitalId, Pageable pageRequest);
//
//    @Query("{'userId': ?0, 'doctorId':?1,'locationId': ?2,'hospitalId': ?3}")
//	PatientCollection findByUserIdDoctorIdLocationIdHospitalId(String patientId, String doctorId, String locationId, String hospitalId, Sort sort);

}
