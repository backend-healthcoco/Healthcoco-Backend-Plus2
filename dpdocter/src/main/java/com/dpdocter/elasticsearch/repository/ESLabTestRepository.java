package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESLabTestDocument;

public interface ESLabTestRepository extends ElasticsearchRepository<ESLabTestDocument, String> {

//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
//    List<ESLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Collection<String> testIds, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
//    List<ESLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Collection<String> testIds, Sort sort);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
//    List<ESLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
//    List<ESLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4}) OR (((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4})")
//    List<ESLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds,
//	    Pageable pageRequest);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4}) OR (((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4})")
//    List<ESLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds,
//	    Sort sort);
//
//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Sort sort);
//
//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
//    List<ESLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Collection<String> testIds, Pageable pageRequest);
//
//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
//    List<ESLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Collection<String> testIds, Sort sort);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
//    List<ESLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageRequest);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
//    List<ESLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:{$in: ?4}")
//    List<ESLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds,
//	    Pageable pageRequest);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4}")
//    List<ESLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds, Sort sort);
//
//    @Query("testId: ?0")
//    List<ESLabTestDocument> findByTestId(String testId);
//
//    @Query("testId:  ?0")
//    List<ESLabTestDocument> findByTestIds(String string);

}
