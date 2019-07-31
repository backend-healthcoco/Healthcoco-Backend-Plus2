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

	List<BlogCollection> findByCategoryAndDiscardedIsFalse(String category, Sort sort);

	BlogCollection findBySlugURL(String slugURL);

	List<BlogCollection> findByDiscardedIsFalseAndCategory(Pageable pageable, String category);

	@Query(value = "{'category':?0}", count = true)
	Integer getCount(String category);
}