package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientCollection;
@Repository
public interface PatientRepository extends MongoRepository<PatientCollection, String>{
	public PatientCollection findByUserId(String userId);
	@Query("{'userId':?0,'doctorId':?1,'locationId':?2,'hospitalId':?3}")
	public PatientCollection findByUserIdDoctorIdLocationIdAndHospitalId(String userId,String doctorId,String locationId,String hospitalId);
}
