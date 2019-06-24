package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugInfoCollection;

public interface DrugInfoRepository extends MongoRepository<DrugInfoCollection, ObjectId>{

	@Query("{'drugCode': ?0}")
	DrugInfoCollection findByDrugCode(String drugCode);
	
	@Query("{'drugCode': {$in: ?0}}")
	List<DrugInfoCollection> findByDrugCodes(List<String> drugCode);
	
}
