package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DiseasesCollection;

public interface DiseasesRepository extends MongoRepository<DiseasesCollection, String> {

	@Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2}")
	List<DiseasesCollection> findDiseases(String doctorId, String locationId, String hospitalId);

}
