package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ClinicalNotesCollection;

@Repository
public interface ClinicalNotesRepository extends MongoRepository<ClinicalNotesCollection, String>,PagingAndSortingRepository<ClinicalNotesCollection, String>{

}
