package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.EmailTrackCollection;

public interface EmailTrackRepository extends MongoRepository<EmailTrackCollection, ObjectId>, PagingAndSortingRepository<EmailTrackCollection, ObjectId> {

	@Query("{'type':{$in: ?0}}")
    List<EmailTrackCollection> findByType(String[] type, Pageable pageRequest);

	@Query("{'type':{$in: ?0}}")
    List<EmailTrackCollection> findByType(String[] type, Sort sort);

    @Query("{'doctorId': ?0}")
    List<EmailTrackCollection> findAll(ObjectId doctorId, Pageable pageRequest);

    @Query("{'doctorId': ?0}")
    List<EmailTrackCollection> findAll(ObjectId doctorId, Sort sort);

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'patientId': ?2, 'type':{$in: ?3}}")
    List<EmailTrackCollection> findByLocationHospitalPatientId(ObjectId locationId, ObjectId hospitalId, ObjectId patientId, String[] type, Pageable pageRequest);

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'patientId': ?2, 'type':{$in: ?3}}")
    List<EmailTrackCollection> findByLocationHospitalPatientId(ObjectId locationId, ObjectId hospitalId, ObjectId patientId, String[] type, Sort sort);

    @Query("{'doctorId': ?0, 'patientId': ?1, 'type':{$in: ?2}}")
    List<EmailTrackCollection> findByDoctorPatient(ObjectId doctorId, ObjectId patientId, String[] type, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'patientId': ?1, 'type':{$in: ?2}}")
    List<EmailTrackCollection> findByDoctorPatient(ObjectId doctorId, ObjectId patientId, String[] type, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3, 'type':{$in: ?4}}")
    List<EmailTrackCollection> findByDoctorLocationHospitalPatient(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId, String[] type, Pageable pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'patientId': ?3, 'type':{$in: ?4}}")
    List<EmailTrackCollection> findByDoctorLocationHospitalPatient(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId, String[] type, Sort sort);

}
