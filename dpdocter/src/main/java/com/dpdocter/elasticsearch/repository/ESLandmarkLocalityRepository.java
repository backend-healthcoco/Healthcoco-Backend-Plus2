package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;

public interface ESLandmarkLocalityRepository extends ElasticsearchRepository<ESLandmarkLocalityDocument, String> {

//    @Query("cityId:*?0* OR landmark:?1* OR locality:?1*")
//    List<ESLandmarkLocalityDocument> findByCityId(String cityId, String searchTerm);
//
//    @Query("cityId:*?0* AND landmark:?1*")
//    List<ESLandmarkLocalityDocument> findByCityIdAndLandmark(String cityId, String searchTerm);
//
//    @Query("cityId:*?0* AND locality:?1*")
//    List<ESLandmarkLocalityDocument> findByCityIdAndLocality(String cityId, String searchTerm);

//    @Query("{\"bool\": {\"must\": [{\"prefix\": {\"landmark\": \"?0\"}}]}}")
//    List<ESLandmarkLocalityDocument> findByLandmark(String searchTerm, Pageable pageable);
//
//    @Query("{\"bool\": {\"must\": [{\"prefix\": {\"locality\": \"?0\"}}]}}")
//    List<ESLandmarkLocalityDocument> findByLocality(String searchTerm, Pageable pageable);

//    @Query("landmark:?0* AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
//    List<ESLandmarkLocalityDocument> findByLandmark(String searchTerm, double latitude, double longitude);
//
//    @Query("locality:?0* AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
//    List<ESLandmarkLocalityDocument> findByLocality(String searchTerm, double latitude, double longitude);
//
//    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10")
//    List<ESLandmarkLocalityDocument> findByLandmarkANDLocality(double latitude, double longitude);

}
