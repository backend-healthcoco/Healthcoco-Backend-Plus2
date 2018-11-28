package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.MasterBabyImmunizationCollection;

public interface MasterBabyImmunizationRepository extends MongoRepository<MasterBabyImmunizationCollection, ObjectId>{

	 @Query("{'isChartVaccine': ?0}")
	 List<MasterBabyImmunizationCollection> findAll(Boolean isChartVaccine);
	
}
