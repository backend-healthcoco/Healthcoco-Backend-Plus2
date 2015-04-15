package com.dpdocter.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository extends MongoRepository<DrugCollection, String> {
	@Query("{'id' : ?0, 'drugCode' : ?1")
	DrugCollection findByDrugIdAndDrugCode(String id, UUID drugCode);
}
