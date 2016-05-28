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
	List<LocationCollection> findClinics(String hospitalId, boolean isClinic, Pageable pageRequest);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findClinics(String hospitalId, boolean isClinic, Sort sort);

	@Query("{'isClinic':?0}")
	List<LocationCollection> findClinics(boolean isClinic, Pageable pageRequest);

	@Query("{'isClinic':?0}")
	List<LocationCollection> findClinics(boolean isClinic, Sort sort);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findLabs(String hospitalId, boolean isLab, Pageable pageRequest);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findLabs(String hospitalId, boolean isLab, Sort sort);

	@Query("{'isLab':?0}")
	List<LocationCollection> findLabs(boolean isLab, Pageable pageRequest);

	@Query("{'isLab':?0}")
	List<LocationCollection> findLabs(boolean isLab, Sort sort);

}
