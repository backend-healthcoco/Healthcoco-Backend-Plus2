package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPatientDocument;

public interface ESPatientRepository extends ElasticsearchRepository<ESPatientDocument, String>{
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND (firstName:?3* OR emailAddress:?3* OR mobileNumber:?3* OR PID:?3*)")
//    List<ESPatientDocument> find(String doctorId, String locationId, String hospitalId, String searchTerm, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND (firstName:?3* OR emailAddress:?3* OR mobileNumber:?3* OR PID:?3*)")
//    List<ESPatientDocument> find(String doctorId, String locationId, String hospitalId, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND firstName:?3*")
//    List<ESPatientDocument> findByFirstName(String doctorId, String locationId, String hospitalId, String searchValue, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND firstName:?3*")
//    List<ESPatientDocument> findByFirstName(String doctorId, String locationId, String hospitalId, String searchValue);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND PID:?3*")
//    List<ESPatientDocument> findByPID(String doctorId, String locationId, String hospitalId, String searchValue, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND PID:?3*")
//    List<ESPatientDocument> findByPID(String doctorId, String locationId, String hospitalId, String searchValue, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND mobileNumber:?3*")
//    List<ESPatientDocument> findByMobileNumber(String doctorId, String locationId, String hospitalId, String searchValue, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND mobileNumber:?3*")
//    List<ESPatientDocument> findByMobileNumber(String doctorId, String locationId, String hospitalId, String searchValue, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND emailAddress:?3*")
//    List<ESPatientDocument> findByEmailAddress(String doctorId, String locationId, String hospitalId, String searchValue, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND emailAddress:?3*")
//    List<ESPatientDocument> findByEmailAddress(String doctorId, String locationId, String hospitalId, String searchValue, Sort sort);
//
//    @Query("userName:?0*")
//    ESPatientDocument findByUserName(String username);
//
//    @Query(value = "doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND (firstName:?3* OR PID:?3* OR mobileNumber:?3* OR emailAddress:?3*)")
//    List<ESPatientDocument> count(String doctorId, String locationId, String hospitalId, String searchTerm);
//
}
