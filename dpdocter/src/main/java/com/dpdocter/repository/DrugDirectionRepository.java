package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugDirectionCollection;

public interface DrugDirectionRepository extends MongoRepository<DrugDirectionCollection, String> {

	@Query(value = "{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}", fields = "{'dcotorId': 0, 'hospitalId': 0, 'locationId': 0, 'isDeleted': 0}")
	List<DrugDirectionCollection> findByDoctorIdAndLocationIdAndHospitalIdCustomDrugdirection(String doctorId,String locationId, String hospitalId);

}
