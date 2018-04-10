package com.dpdocter.repository;
 
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.beans.PatientInfo;
import com.dpdocter.collections.PatientInfoCollection;


@Repository
public interface PatientInfoRepository extends MongoRepository<PatientInfoCollection, ObjectId> {
 
	@Query("{'patientId':?0}")
	PatientInfoCollection getBypatientId(ObjectId patientId);
	
	@Query("{'doctorId':?0}")
	List<PatientInfoCollection> find(ObjectId doctorId);
	
	

	//@Query("{'doctorId': ?0, 'updatedTime': {'$gt': ?1}}")
	//List<PatientInfoCollection> findByDoctorId(String doctorId, Date date, Sort sort);
	
    
}
