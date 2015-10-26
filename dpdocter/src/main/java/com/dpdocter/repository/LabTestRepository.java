package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.LabTestCollection;

public interface LabTestRepository extends MongoRepository<LabTestCollection, String>, PagingAndSortingRepository<LabTestCollection, String> {

    @Override
    Page<LabTestCollection> findAll(Pageable pageable);

    @Query("{'doctorId': null}")
	List<LabTestCollection> getGlobalLabTests(Pageable pageable);

    @Query("{'doctorId': null}")
	List<LabTestCollection> getGlobalLabTests(Sort sort);

    @Query("{'doctorId': null, 'discarded': ?0}")
	List<LabTestCollection> getGlobalLabTests(Boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?0}")
	List<LabTestCollection> getGlobalLabTests(Boolean discarded, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
	List<LabTestCollection> getGlobalLabTests(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
	List<LabTestCollection> getGlobalLabTests(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<LabTestCollection> getGlobalLabTests(Date date, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<LabTestCollection> getGlobalLabTests(Date date, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Boolean discarded,	Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Boolean discarded,	Sort sort);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Date date, Pageable pageable);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Date date, Sort sort);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Date date, Boolean discarded, Pageable pageable);

	@Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2}")
	List<LabTestCollection> getCustomLabTests(String doctorId, Date date, Boolean discarded, Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date,	Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, Boolean discarded, Pageable pageable);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4}")
	List<LabTestCollection> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, Boolean discarded, Sort sort);

	@Query("{'updatedTime': {'$gte': ?0}}")
	List<LabTestCollection> getCustomGlobalLabTests(Date date, Pageable pageable);

	@Query("{'updatedTime': {'$gte': ?0}}")
	List<LabTestCollection> getCustomGlobalLabTests(Date date, Sort sort);

	@Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<LabTestCollection> getCustomGlobalLabTests(Date date, Boolean discarded, Pageable pageable);

	@Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<LabTestCollection> getCustomGlobalLabTests(Date date, Boolean discarded, Sort sort);

	@Query("{'discarded': ?0}")
	List<LabTestCollection> getCustomGlobalLabTests(Boolean discarded, Pageable pageable);

	@Query("{'discarded': ?0}")
	List<LabTestCollection> getCustomGlobalLabTests(Boolean discarded, Sort sort);

	@Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Boolean discarded, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Boolean discarded, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Sort sort);

	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Boolean discarded, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Boolean discarded, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Date date, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Date date, Sort sort);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Date date, Boolean discarded, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, Date date, Boolean discarded, Sort sort);
	
	@Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, Boolean discarded, Pageable pageable);

	@Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
	List<LabTestCollection> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, Boolean discarded, Sort sort);

}
