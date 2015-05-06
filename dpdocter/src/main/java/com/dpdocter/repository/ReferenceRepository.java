package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.ReferencesCollection;

@Repository
public interface ReferenceRepository extends MongoRepository<ReferencesCollection, String> {
	@Query("{'$and': [ {'$or': [ {'doctorId': 'D12345'}, {'doctorId': ''} ]}, {'$or': [ {'locationId': 'L12345'}, {'locationId': ''} ]}, {'$or': [ {'hospitalId': 'H12345'}, {'hospitalId': ''} ]}, {'isDeleted': false} ]}")
	List<ReferencesCollection> findByDoctorIdAndLocationIdAndHospitalId(String doctorId, String locationId, String hospitalId, boolean isDeleted);

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3}")
	List<ReferencesCollection> findByDoctorIdAndLocationIdAndHospitalIdCustomReferences(String doctorId, String locationId, String hospitalId,
			boolean isDeleted);

}
