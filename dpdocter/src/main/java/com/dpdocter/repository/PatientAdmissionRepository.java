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

	PatientAdmissionCollection findByUserId(String userId);

	List<PatientAdmissionCollection> findDistinctPatientByDoctorId(String doctorId, Pageable pageable);

	@Query("{'patientId':?0,'doctorId':?1}")
	PatientAdmissionCollection findByPatientIdAndDoctorId(String patientId, String doctorId);

}
