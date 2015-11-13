package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientAdmissionCollection;

@Repository
public interface PatientAdmissionRepository extends MongoRepository<PatientAdmissionCollection, String>,
	PagingAndSortingRepository<PatientAdmissionCollection, String> {

	@Query("{'userId':?0}")
    PatientAdmissionCollection findByUserId(String userId);

    @Query("{'doctorId':?0}")
    List<PatientAdmissionCollection> findDistinctPatientByDoctorId(String doctorId, Pageable pageable);

    @Query("{'patientId':?0,'doctorId':?1}")
    PatientAdmissionCollection findByPatientIdAndDoctorId(String patientId, String doctorId);

    @Query("{'userId':?0,'doctorId':?1}")
    PatientAdmissionCollection findByUserIdAndDoctorId(String userId, String doctorId);

}
