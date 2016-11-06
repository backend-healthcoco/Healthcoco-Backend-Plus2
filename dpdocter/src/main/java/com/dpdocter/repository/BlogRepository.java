package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.BlogCollection;

@Repository
public interface BlogRepository
		extends MongoRepository<BlogCollection, ObjectId>, PagingAndSortingRepository<BlogCollection, ObjectId> {

	@Query("{'discarded': true}")
	List<BlogCollection> findAll();

	@Query("{'category': ?0,'discarded': true}")
	List<BlogCollection> findByCatogery(String category, Sort sort);

	@Query("{'id': ?0}")
	BlogCollection findOne(ObjectId id);

	@Query("{'discarded': true}")
	Page<BlogCollection> findAll(Pageable pageable);

	@Query("{'discarded': true,'category':?0}")
	List<BlogCollection> findAll(Pageable pageable, String category);

}
