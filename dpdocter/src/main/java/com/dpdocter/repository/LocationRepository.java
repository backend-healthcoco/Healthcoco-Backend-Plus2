package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.LocationCollection;

@Repository
public interface LocationRepository extends MongoRepository<LocationCollection, ObjectId> {

	List<LocationCollection> findByHospitalId(ObjectId hospitalId, Sort sort);

	@Override
    Page<LocationCollection> findAll(Pageable pageable);

	LocationCollection findByIdAndHospitalId(ObjectId id, ObjectId hospitalId);

}
