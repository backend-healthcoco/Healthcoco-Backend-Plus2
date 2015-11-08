package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsTagsCollection;

@Repository
public interface RecordsTagsRepository extends MongoRepository<RecordsTagsCollection, String> {
	@Query("{'tagsId': ?0}")
	List<RecordsTagsCollection> findByTagsId(String tagsId, Pageable pageRequest);

    @Query("{'doctorId': ?0}")
    List<RecordsTagsCollection> findAll(String doctorId, Sort sort);

    @Query("{'tagsId': ?0}")
	List<RecordsTagsCollection> findByTagsId(String tagId, Sort sort);

}
