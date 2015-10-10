package com.dpdocter.sms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.beans.SMSTrackDetail;

public interface SMSTrackRepository extends MongoRepository<SMSTrackDetail, String>, PagingAndSortingRepository<SMSTrackDetail, String> {

    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    List<SMSTrackDetail> findAll(String locationId, String hospitalId, Pageable pageable);

    @Query("{'locationId': ?0, 'hospitalId': ?1}")
    List<SMSTrackDetail> findAll(String locationId, String hospitalId, Sort sort);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<SMSTrackDetail> findAll(String doctorId, String locationId, String hospitalId, Pageable pageable);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<SMSTrackDetail> findAll(String doctorId, String locationId, String hospitalId, Sort sort);

    @Query(value = "{'doctorId' : ?0, 'locationId': ?1, 'hospitalId' : ?2}", count = true)
    Integer getDoctorsSMSCount(String doctorId, String locationId, String hospitalId);

    @Override
    Page<SMSTrackDetail> findAll(Pageable pageRequest);

    @Query("{'doctorId': ?0}")
    List<SMSTrackDetail> findAll(String doctorId, Pageable pageRequest);

    @Query("{'doctorId': ?0}")
    List<SMSTrackDetail> findAll(String doctorId, Sort sort);

    @Query("{'responseId': ?0}")
    SMSTrackDetail findByResponseId(String requestId);

}
