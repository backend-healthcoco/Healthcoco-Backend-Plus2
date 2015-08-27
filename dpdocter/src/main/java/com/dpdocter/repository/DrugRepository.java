package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository extends MongoRepository<DrugCollection, String>, PagingAndSortingRepository<DrugCollection, String>{
    @Query("{'id' : ?0, 'drugCode' : ?1")
    DrugCollection findByDrugIdAndDrugCode(String id, String drugCode);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2,'isDeleted': ?3}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, boolean isDeleted, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
    List<DrugCollection> getCustomDrugs(String doctorId, String hospitalId, String locationId, Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null}")
	List<DrugCollection> getGlobalDrugs(Sort sort, PageRequest pageRequest);

    @Query("{'doctorId': null, 'isDeleted': ?1}")
	List<DrugCollection> getGlobalDrugs(boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}}")
	List<DrugCollection> getGlobalDrugs(Date date, Sort sort, PageRequest pageRequest);

	@Query("{'doctorId': null, 'createdTime': {'$gte': ?0}, 'isDeleted': ?2}")
	List<DrugCollection> getGlobalDrugs(Date date, boolean isDeleted, Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
	List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Sort sort,	PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'isDeleted': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'isDeleted': ?3}]}")
	List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, boolean isDeleted,	Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3}}]}")
	List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date,	Sort sort, PageRequest pageRequest);

	@Query("{'$or': [{'doctorId': ?0,  'locationId': ?1, 'hospitalId': ?2, 'createdTime': {'$gte': ?3}, 'isDeleted': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'createdTime': {'$gte': ?3},'isDeleted': ?4}]}")
	List<DrugCollection> getCustomGlobalDrugs(String doctorId, String hospitalId, String locationId, Date date,	boolean isDeleted, Sort sort, PageRequest pageRequest);

}
