package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiagnosisCollection;

public interface DiagnosisRepository extends MongoRepository<DiagnosisCollection, String>, PagingAndSortingRepository<DiagnosisCollection, String> {
	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
	List<DiagnosisCollection> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, boolean isDeleted, PageRequest pageRequest);
}
