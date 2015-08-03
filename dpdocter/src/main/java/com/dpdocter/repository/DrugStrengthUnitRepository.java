package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DrugStrengthUnitCollection;

public interface DrugStrengthUnitRepository extends MongoRepository<DrugStrengthUnitCollection, String> {

	@Query("{'isDeleted': ?0}")
	List<DrugStrengthUnitCollection> getDrugStrengthUnit(boolean isDeleted, Sort sort);

	@Query(value = "{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2}")
	List<DrugStrengthUnitCollection> getDrugStrengthUnit(String doctorId, String hospitalId, String locationId,Sort sort);

	@Query(value = "{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'isDeleted': ?3}")
	List<DrugStrengthUnitCollection> getDrugStrengthUnit(String doctorId, String hospitalId, String locationId,	boolean isDeleted, Sort sort);

	@Query("{'createdTime': {'$gte': ?0}}")
	List<DrugStrengthUnitCollection> getDrugStrengthUnit(Date date, Sort sort);

	@Query("{'createdTime': {'$gte': ?0},'isDeleted': ?1}")
	List<DrugStrengthUnitCollection> getDrugStrengthUnit(Date date, boolean isDeleted, Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3}}")
	List<DrugStrengthUnitCollection> getDrugStrengthUnit(String doctorId, String hospitalId, String locationId,	Date date, Sort sort);

	@Query("{'doctorId': ?0, 'hospitalId': ?1, 'locationId': ?2, 'createdTime': {'$gte': ?3},'isDeleted': ?4}")
	List<DrugStrengthUnitCollection> getDrugStrengthUnit(String doctorId, String hospitalId, String locationId,	Date date, boolean isDeleted, Sort sort);

}
