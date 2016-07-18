package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ResumeCollection;

public interface ResumeRepository extends MongoRepository<ResumeCollection, ObjectId>, PagingAndSortingRepository<ResumeCollection, ObjectId> {

	@Query("{'type':?0}")
	List<ResumeCollection> find(String type, PageRequest pageRequest);

	@Query("{'type':?0}")
	List<ResumeCollection> find(String type, Sort sort);

}
