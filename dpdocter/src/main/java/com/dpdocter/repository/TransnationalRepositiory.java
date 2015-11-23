package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.TransactionalCollection;

public interface TransnationalRepositiory extends MongoRepository<TransactionalCollection, String> {

    @Query("{'resourceId': ?0, 'resource': ?1}")
    TransactionalCollection findByResourceIdAndResource(String resourceId, String resource);

    @Query("{'resource': ?0, 'isCached': ?1}")
    List<TransactionalCollection> findByResourceAndIsCached(String resource, boolean isCached);

    @Query("{'isCached': ?0}")
    List<TransactionalCollection> findByIsCached(boolean isCached);

}
