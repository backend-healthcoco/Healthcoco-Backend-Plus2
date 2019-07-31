package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DrugInfoCollection;

public interface DrugInfoRepository extends MongoRepository<DrugInfoCollection, ObjectId>{

	DrugInfoCollection findByDrugCode(String drugCode);
	
	List<DrugInfoCollection> findByDrugCodeIn(List<String> drugCode);
	
}
