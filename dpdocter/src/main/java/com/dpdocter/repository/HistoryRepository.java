package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.HistoryCollection;

public interface HistoryRepository extends MongoRepository<HistoryCollection, String>{
	@Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2,'patientId':?3}")
	HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientId(String doctorId,String locationId,String hospitalId,String patientId);
}
