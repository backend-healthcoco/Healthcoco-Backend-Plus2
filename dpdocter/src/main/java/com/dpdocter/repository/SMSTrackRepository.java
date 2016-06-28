package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.SMSTrackDetail;

public interface SMSTrackRepository extends MongoRepository<SMSTrackDetail, String>, PagingAndSortingRepository<SMSTrackDetail, String> {

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'type':{$in: ?2}}")
    List<SMSTrackDetail> findByLocationHospitalId(String locationId, String hospitalId, String[] type, Pageable pageable);

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'type':{$in: ?2}}")
    List<SMSTrackDetail> findByLocationHospitalId(String locationId, String hospitalId, String[] type, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'type':{$in: ?3}}")
    List<SMSTrackDetail> findByDoctorLocationHospitalId(String doctorId, String locationId, String hospitalId, String[] type, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'type':{$in: ?3}}")
    List<SMSTrackDetail> findByDoctorLocationHospitalId(String doctorId, String locationId, String hospitalId, String[] type, Sort sort);

    @Query(value = "{'doctorId' : ?0, 'locationId': ?1, 'hospitalId' : ?2}", count = true)
    Integer getDoctorsSMSCount(String doctorId, String locationId, String hospitalId);

    @Query("{'type':{$in: ?0}}")
    List<SMSTrackDetail> findByType(String[] type, Pageable pageRequest);

    @Query("{'type':{$in: ?0}}")
    List<SMSTrackDetail> findByType(String[] type, Sort sort);
    
    @Query("{'doctorId': ?0}")
    List<SMSTrackDetail> findAll(String doctorId, Pageable pageRequest);

    @Query("{'doctorId': ?0}")
    List<SMSTrackDetail> findAll(String doctorId, Sort sort);

    @Query("{'responseId': ?0}")
    SMSTrackDetail findByResponseId(String requestId);

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'smsDetails.userId': ?2, 'type':{$in: ?3}}")
    List<SMSTrackDetail> findByLocationHospitalPatientId(String locationId, String hospitalId, String patientId, String[] type, Pageable pageable);

    @Query("{'locationId': ?0, 'hospitalId': ?1, 'smsDetails.userId': ?2, 'type':{$in: ?3}}")
    List<SMSTrackDetail> findByLocationHospitalPatientId(String locationId, String hospitalId, String patientId, String[] type, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'smsDetails.userId': ?3, 'type':{$in: ?4}}")
    List<SMSTrackDetail> findByDoctorLocationHospitalPatient(String doctorId, String locationId, String hospitalId, String patientId, String[] type, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'smsDetails.userId': ?3, 'type':{$in: ?4}}")
    List<SMSTrackDetail> findByDoctorLocationHospitalPatient(String doctorId, String locationId, String hospitalId, String patientId, String[] type, Sort sort);

    @Query("{'doctorId': ?0, 'smsDetails.userId': ?1, 'type':{$in: ?2}}")
    List<SMSTrackDetail> findByDoctorPatient(String doctorId, String patientId, String[] type, Pageable pageable);

    @Query("{'doctorId': ?0, 'smsDetails.userId': ?1, 'type':{$in: ?2}}")
    List<SMSTrackDetail> findByDoctorPatient(String doctorId, String patientId, String[] type, Sort sort);

}
