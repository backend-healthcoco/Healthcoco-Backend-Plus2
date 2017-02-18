package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
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

	@Query("{'category': ?0,'discarded': false}")
	List<BlogCollection> findByCatogery(String category, Sort sort);

	@Query("{'slugURL': ?0}")
	BlogCollection findBySlugURL(String slugURL);

	@Query("{'discarded': false,'category':?0}")
	List<BlogCollection> findAll(Pageable pageable, String category);

	@Query(value = "{'category':?0}", count = true)
	Integer getCount(String category);
}