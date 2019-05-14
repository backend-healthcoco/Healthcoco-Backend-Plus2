package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;

public interface ESDoctorRepository extends ElasticsearchRepository<ESDoctorDocument, String>,
		PagingAndSortingRepository<ESDoctorDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match\": {\"userId\": \"?0\"}},{\"match\": {\"locationId\": \"?1\"}}]}}")
	ESDoctorDocument findByUserIdAndLocationId(String userId, String locationId);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"userId\": \"?0\"}}]}}")
	List<ESDoctorDocument> findByUserId(String userId);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"userId\": \"?0\"}}]}}")
	List<ESDoctorDocument> findByUserId(String userId, Pageable pageable);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"locationId\": \"?0\"}}]}}")
	List<ESDoctorDocument> findByLocationId(String locationId);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"firstName\": \"?0*\"}}, {\"match\": {\"isDoctorListed\": \"?1\"}}]}}")
	List<ESDoctorDocument> findByFirstName(String searchTerm, Boolean isDoctorListed, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
			+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}},"
			+ "{\"match_phrase_prefix\": {\"firstName\": \"?2*\"}}, {\"match\": {\"isDoctorListed\": \"?3\"}}, {\"match\": {\"isActive\": \"?4\"}}, {\"match\": {\"isActivate\": \"?4\"}}]}}")
	List<ESDoctorDocument> findByCityLocation(String city, String location, String searchTerm, Boolean isDoctorListed,
			Boolean isActive, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
			+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}},"
			+ "{\"match_phrase_prefix\": {\"firstName\": \"?2*\"}}, {\"match\": {\"isDoctorListed\": \"?3\"}}]}}")
	List<ESDoctorDocument> findByCityLocation(String city, String location, String searchTerm, Boolean isDoctorListed,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match_phrase_prefix\": {\"firstName\": \"?1*\"}}, {\"match\": {\"isDoctorListed\": \"?2\"}},"
			+ "{\"match\": {\"isActive\": \"?3\"}}, {\"match\": {\"isActivate\": \"?3\"}}]}}")
	List<ESDoctorDocument> findByCity(String city, String searchTerm, Boolean isDoctorListed, Boolean isActive,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match_phrase_prefix\": {\"firstName\": \"?1*\"}}, {\"match\": {\"isDoctorListed\": \"?2\"}}]}}")
	List<ESDoctorDocument> findByCity(String city, String searchTerm, Boolean isDoctorListed, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}},"
			+ "{\"match_phrase_prefix\": {\"firstName\": \"?1*\"}}, {\"match\": {\"isDoctorListed\": \"?2\"}}, {\"match\": {\"isActive\": \"?3\"}}, {\"match\": {\"isActivate\": \"?3\"}}]}}")
	List<ESDoctorDocument> findByLocation(String location, String searchTerm, Boolean isDoctorListed, Boolean isActive,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}},"
			+ "{\"match_phrase_prefix\": {\"firstName\": \"?1*\"}}, {\"match\": {\"isDoctorListed\": \"?2\"}}]}}")
	List<ESDoctorDocument> findByLocation(String location, String searchTerm, Boolean isDoctorListed,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
			+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}}, {\"match\": {\"isDoctorListed\": \"?2\"}},"
			+ "{\"match\": {\"isActive\": \"?3\"}}, {\"match\": {\"isActivate\": \"?3\"}}]}}")
	List<ESDoctorDocument> findByCityLocation(String city, String location, Boolean isDoctorListed, Boolean isActive,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
			+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}}, {\"match\": {\"isDoctorListed\": \"?2\"}}]}}")
	List<ESDoctorDocument> findByCityLocation(String city, String location, Boolean isDoctorListed,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match\": {\"isDoctorListed\": \"?1\"}}, {\"match\": {\"isActive\": \"?2\"}}, {\"match\": {\"isActivate\": \"?2\"}}]}}")
	List<ESDoctorDocument> findByCity(String city, Boolean isDoctorListed, Boolean isActive, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match\": {\"isDoctorListed\": \"?1\"}}]}}")
	List<ESDoctorDocument> findByCity(String city, Boolean isDoctorListed, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}}, {\"match\": {\"isDoctorListed\": \"?1\"}},"
			+ "{\"match\": {\"isActive\": \"?3\"}}, {\"match\": {\"isActivate\": \"?3\"}}]}")
	List<ESDoctorDocument> findByLocation(String location, Boolean isDoctorListed, Boolean isActive,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}}, {\"match\": {\"isDoctorListed\": \"?1\"}}]}")
	List<ESDoctorDocument> findByLocation(String location, Boolean isDoctorListed, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
			+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}}, {\"match\": {\"isLocationListed\": \"?2\"}}]}}")
	List<ESDoctorDocument> findLocationByCityLocation(String city, String location, Boolean isLocationListed,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match\": {\"isLocationListed\": \"?1\"}}]}}")
	List<ESDoctorDocument> findLocationByCity(String city, Boolean isLocationListed, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}}, {\"match\": {\"isLocationListed\": \"?1\"}}]}")
	List<ESDoctorDocument> findLocationByLocation(String location, Boolean isLocationListed, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"locationName\": \"?0*\"}}]}}")
	List<ESDoctorDocument> findByLocationName(String searchTerm);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
			+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}},"
			+ "{\"match_phrase_prefix\": {\"locationName\": \"?2*\"}}, {\"match\": {\"isLocationListed\": \"?3\"}}]}}")
	List<ESDoctorDocument> findByCityLocationName(String city, String location, String searchTerm,
			Boolean isLocationListed, Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locationName\": \"?1*\"}}, {\"match\": {\"isLocationListed\": \"?2\"}}]}}")
	List<ESDoctorDocument> findByCityLocationName(String city, String searchTerm, Boolean isLocationListed,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}},"
			+ "{\"match_phrase_prefix\": {\"locationName\": \"?1*\"}}, {\"match\": {\"isLocationListed\": \"?2\"}}]}}")
	List<ESDoctorDocument> findByLocationLocationName(String location, String searchTerm, Boolean isLocationListed,
			Pageable pageRequest);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"userId\": \"?0\"}},{\"match\": {\"isDoctorListed\": \"?1\"}}]}}")
	List<ESDoctorDocument> findbySlugUrl(String doctorSlugURL, boolean isDoctorListed);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"userUId\": \"?0\"}},{\"match\": {\"isDoctorListed\": \"?1\"}}]}}")
	List<ESDoctorDocument> findbyUserUId(String userUId, boolean isDoctorListed);

}
