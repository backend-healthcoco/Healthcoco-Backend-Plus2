package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.LocationCollection;

@Repository
public interface LocationRepository extends MongoRepository<LocationCollection, String> {

	@Query("{'hospitalId':?0}")
	List<LocationCollection> find(String hospitalId, Pageable pageRequest);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> find(String hospitalId, Sort sort);

}
