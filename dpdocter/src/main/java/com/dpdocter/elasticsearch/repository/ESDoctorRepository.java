package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;

public interface ESDoctorRepository extends ElasticsearchRepository<ESDoctorDocument, String>, PagingAndSortingRepository<ESDoctorDocument, String> {
    
    @Query("{\"bool\": {\"must\": [{\"match\": {\"userId\": \"?0\"}},{\"match\": {\"locationId\": \"?1\"}}]}}")
    ESDoctorDocument findByUserIdAndLocationId(String userId, String locationId);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"userId\": \"?0\"}}]}}")
    List<ESDoctorDocument> findByUserId(String userId);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"userId\": \"?0\"}}]}}")
    List<ESDoctorDocument> findByUserId(String userId, Pageable pageable);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"locationId\": \"?0\"}}]}}")
    List<ESDoctorDocument> findByLocationId(String locationId);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"firstName\": \"?0*\"}}]}}")
    List<ESDoctorDocument> findByFirstName(String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
    		+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}},"
    		+ "{\"match_phrase_prefix\": {\"firstName\": \"?2*\"}}]}}")
    List<ESDoctorDocument> findByCityLocation(String city, String location, String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match_phrase_prefix\": {\"firstName\": \"?1*\"}}]}}")
    List<ESDoctorDocument> findByCity(String city, String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}},"
    		+ "{\"match_phrase_prefix\": {\"firstName\": \"?1*\"}}]}}")
    List<ESDoctorDocument> findByLocation(String location, String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
    		+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}}]}}")
    List<ESDoctorDocument> findByCityLocation(String city, String location);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}]}}")
    List<ESDoctorDocument> findByCity(String city);

    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}}]}")
    List<ESDoctorDocument> findByLocation(String location);

//    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*)")
//    List<SolrDoctorDocument> findByCityLocation(String city, String location);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"locationName\": \"?0*\"}}]}}")
    List<ESDoctorDocument> findByLocationName(String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
    		+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}},"
    		+ "{\"match_phrase_prefix\": {\"locationName\": \"?2*\"}}]}}")
    List<ESDoctorDocument> findByCityLocationName(String city, String location, String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locationName\": \"?1*\"}}]}}")
    List<ESDoctorDocument> findByCityLocationName(String city, String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}},"
    		+ "{\"match_phrase_prefix\": {\"locationName\": \"?1*\"}}]}}")
    List<ESDoctorDocument> findByLocationLocationName(String location, String searchTerm);

    
//    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND locationId : ?2 AND isLab : ?3")
//    List<SolrDoctorDocument> findLabByCityLocationName(String city, String location, String locationId, boolean isLab);
//
//    @Query("city : ?0* AND locationId : ?1 AND isLab : ?2")
//    List<SolrDoctorDocument> findLabByCity(String city, String locationId, boolean isLab);
//
//    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND isLab : ?2")
//    SolrDoctorDocument findLabByCityLocationName(String city, String location, boolean isLab);
//
//    @Query("city : ?0* AND isLab : ?1")
//    SolrDoctorDocument findLabByCity(String city, boolean isLab);
//


//    @Query("city : ?0*")
//    List<SolrDoctorDocument> findByCity(String city);
//
//    @Query("(landmarkDetails: ?0* OR streetAddress: ?0* OR locality: ?0*)")
//    List<SolrDoctorDocument> findByLocation(String location);
//
//
//    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10 AND locationId : ?2 AND isLab : ?3 ")
//    List<SolrDoctorDocument> findLabByLatLong(String latitude, String longitude, String locationId, boolean isLab);
}
