package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ClinicalNotesCollection;

@Repository
public interface ClinicalNotesRepository extends MongoRepository<ClinicalNotesCollection, String>, PagingAndSortingRepository<ClinicalNotesCollection, String> {

	@Query(value = "{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2}", count = true)
	Integer getClinicalNotesCount(String doctorId, String hospitalId, String locationId);

}
