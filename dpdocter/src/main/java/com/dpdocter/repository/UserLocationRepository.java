package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserLocationCollection;

public interface UserLocationRepository extends MongoRepository<UserLocationCollection, ObjectId>, PagingAndSortingRepository<UserLocationCollection, ObjectId> {
    @Query("{'userId' : ?0, 'isActivate' : true}")
    List<UserLocationCollection> findByUserIdAndIsActivate(ObjectId userId);

    @Query("{'locationId':?0}")
    List<UserLocationCollection> findByLocationId(ObjectId id);

    @Query("{'userId':?0, 'locationId':?1}")
    UserLocationCollection findByUserIdAndLocationId(ObjectId doctorId, ObjectId locationId);

    @Query("{'locationId' : ?0}")
    List<UserLocationCollection> findByLocationId(ObjectId locationId, Pageable pageable);

    @Query("{'locationId' : ?0}")
    List<UserLocationCollection> findByLocationId(ObjectId locationId, Sort sort);

    @Query("{'userId':{$in: ?0}, 'locationId':?1}")
    List<UserLocationCollection> findByUserIdAndLocationId(List<ObjectId> doctorId, String ObjectId);

    @Query("{'userId' : ?0}")
	List<UserLocationCollection> findByUserId(ObjectId userId);

}
