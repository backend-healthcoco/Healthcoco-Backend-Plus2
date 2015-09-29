package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, String>, PagingAndSortingRepository<DrugTypeCollection, String> {

    @Query("{'doctorId': null}")
    List<DrugTypeCollection> getGlobalDrugType(Sort sort);

    @Query("{'doctorId': null, 'discarded': ?0}")
    List<DrugTypeCollection> getGlobalDrugType(boolean discarded, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, Sort sort);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Sort sort);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, boolean discarded, Sort sort);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, Sort sort);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, boolean discarded, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Date date, Sort sort);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("{'doctorId': null}")
    List<DrugTypeCollection> getGlobalDrugType(Pageable pageable);

    @Query("{'doctorId': null, 'discarded': ?0}")
    List<DrugTypeCollection> getGlobalDrugType(boolean discarded, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, Pageable pageable);

    @Query("{'doctorId': null, 'updatedTime': {'$gte': ?0}, 'discarded': ?2}")
    List<DrugTypeCollection> getGlobalDrugType(Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2,'discarded': ?3}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3},'discarded': ?4}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2} , {'doctorId': null, 'locationId': null, 'hospitalId': null}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'discarded': ?3},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'discarded': ?3}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}} , {'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0,  'hospitalId': ?1, 'locationId': ?2, 'updatedTime': {'$gte': ?3}, 'discarded': ?4},{'doctorId': null, 'locationId': null, 'hospitalId': null, 'updatedTime': {'$gte': ?3},'discarded': ?4}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Pageable pageable);

    @Query("{'doctorId': ?0, 'discarded': ?1}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'doctorId': ?0,'updatedTime': {'$gte': ?1}}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, Pageable pageable);

    @Query("{'doctorId': ?0, 'updatedTime': {'$gte': ?1},'discarded': ?2}")
    List<DrugTypeCollection> getCustomDrugType(String doctorId, Date date, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0},{'doctorId': null}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'discarded': ?1},{'doctorId': null,'discarded': ?1}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, boolean discarded, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}},{'doctorId': null, 'updatedTime': {'$gte': ?1}}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Date date, Pageable pageable);

    @Query("{'$or': [{'doctorId': ?0, 'updatedTime': {'$gte': ?1}, 'discarded': ?2},{'doctorId': null, 'updatedTime': {'$gte': ?1},'discarded': ?2}]}")
    List<DrugTypeCollection> getCustomGlobalDrugType(String doctorId, Date date, boolean discarded, Pageable pageable);

    Page<DrugTypeCollection> findAll(Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}}")
	List<DrugTypeCollection> getCustomGlobalDrugType(Date date, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}}")
	List<DrugTypeCollection> getCustomGlobalDrugType(Date date, Sort sort);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<DrugTypeCollection> getCustomGlobalDrugType(Date date, boolean discarded, Pageable pageable);

    @Query("{'updatedTime': {'$gte': ?0}, 'discarded': ?1}")
	List<DrugTypeCollection> getCustomGlobalDrugType(Date date, boolean discarded, Sort sort);

    @Query("{'discarded': ?0}")
	List<DrugTypeCollection> getCustomGlobalDrugType(boolean discarded, Pageable pageable);

    @Query("{'discarded': ?0}")
	List<DrugTypeCollection> getCustomGlobalDrugType(boolean discarded, Sort sort);

}
