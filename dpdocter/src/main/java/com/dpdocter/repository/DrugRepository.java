package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository extends MongoRepository<DrugCollection, String> {
	@Query("{'id' : ?0, 'drugCode' : ?1")
	DrugCollection findByDrugIdAndDrugCode(String id, String drugCode);

	@Query("{'doctorId': ?0, 'createdTime': {'$gte': ?1}}")
	List<DrugCollection> getDrugs(String doctorId, Date date, Sort sort);

	@Query("{'doctorId': ?0}")
	List<DrugCollection> getDrugs(String doctorId, Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?1}}")
	List<DrugCollection> getDrugs(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<DrugCollection> getDrugs(String doctorId, String hospitalId, String locationId, Sort sort);
}
