package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientCollection;

@Repository
public interface PatientRepository extends MongoRepository<PatientCollection, String> {
    public PatientCollection findByUserId(String userId);

    @Query("{'userId': ?0,'doctorId': ?1,'locationId': ?2,'hospitalId': ?3}")
    public PatientCollection findByUserIdDoctorIdLocationIdAndHospitalId(String userId, String doctorId, String locationId, String hospitalId);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
    List<PatientCollection> findByDoctorId(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}, 'isDeleted':?2}")
    List<PatientCollection> findByDoctorId(String doctorId, Date date, boolean isDeleted, Sort sort);
    
    @Query("{'doctorId': ?0}")
    List<PatientCollection> findByDoctorId(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'isDeleted':?1}")
    List<PatientCollection> findByDoctorId(String doctorId, boolean isdeleted, Sort sort);

    @Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2, 'createdTime': {'$gte': ?3}}")
    List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(String doctorId, String locationId, String hospitalId, Date date, Sort sort);

    @Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted':?4}")
	List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(String doctorId, String locationId,String hospitalId, Date date, boolean isDeleted, Sort sort);
    @Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2")
    List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query("{'doctorId': ?0,'locationId': ?1,'hospitalId': ?2, 'isDeleted':?3")
    List<PatientCollection> findByDoctorIdLocationIdAndHospitalId(String doctorId, String locationId,String hospitalId, boolean isDeleted, Sort sort);
    
    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2,'registrationDate' : {'$gt' : ?3, '$lt' : ?4}}")
    List<PatientCollection> findTodaysRegisteredPatient(String doctorId, String location, String hospitalId, Long startDate, Long endDate);

	
}
