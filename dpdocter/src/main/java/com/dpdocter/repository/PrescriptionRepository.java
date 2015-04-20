package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrescriptionCollection;

public interface PrescriptionRepository extends MongoRepository<PrescriptionCollection, String>, PagingAndSortingRepository<PrescriptionCollection, String> {
	@Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3,'isDeleted' : ?4}")
	List<PrescriptionCollection> getPrescription(String doctorId, String hospitalId, String locationId, String patientId,Boolean isDeleted ,Sort sort);
}
