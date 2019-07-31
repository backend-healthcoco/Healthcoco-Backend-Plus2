package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MasterBabyImmunizationCollection;

public interface MasterBabyImmunizationRepository extends MongoRepository<MasterBabyImmunizationCollection, ObjectId>{

	 List<MasterBabyImmunizationCollection> findByIsChartVaccine(Boolean isChartVaccine);
	
}
