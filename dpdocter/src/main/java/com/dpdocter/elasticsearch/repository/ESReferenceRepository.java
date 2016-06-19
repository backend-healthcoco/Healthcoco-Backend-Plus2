package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESReferenceDocument;

public interface ESReferenceRepository extends ElasticsearchRepository<ESReferenceDocument, String> {

//    @Query(" (doctorId: \"\" OR doctorId:null) AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESReferenceDocument> findGlobal(Date date, boolean discarded, Pageable pageable);
//
//    @Query(" (doctorId: \"\" OR doctorId:null) AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESReferenceDocument> findGlobal(Date date, boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    List<ESReferenceDocument> findCustom(String doctorId, Date date, boolean discarded, Pageable pageable);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    List<ESReferenceDocument> findCustom(String doctorId, Date date, boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    List<ESReferenceDocument> findCustom(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Pageable pageable);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    List<ESReferenceDocument> findCustom(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESReferenceDocument> findCustomGlobal(Date date, boolean discarded, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESReferenceDocument> findCustomGlobal(Date date, boolean discarded, Sort sort);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, Date date, boolean discarded, Pageable pageable);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, Date date, boolean discarded, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Pageable pageable);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, boolean discarded, Sort sort);
//
//    @Query(" (doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND reference:?2*")
//    List<ESReferenceDocument> findGlobal(Date date, Boolean discarded, String searchTerm, Pageable pageable);
//
//    @Query(" (doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND reference:?2*")
//    List<ESReferenceDocument> findGlobal(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND reference:?3*")
//    List<ESReferenceDocument> findCustom(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageable);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND reference:?3*")
//    List<ESReferenceDocument> findCustom(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND reference:?5*")
//    List<ESReferenceDocument> findCustom(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
//	    Pageable pageable);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND reference:?5*")
//    List<ESReferenceDocument> findCustom(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND reference:?2*")
//    List<ESReferenceDocument> findCustomGlobal(Date date, Boolean discarded, String searchTerm, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND reference:?2*")
//    List<ESReferenceDocument> findCustomGlobal(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND reference:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND reference:?3*)")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageable);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND reference:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND reference:?3*)")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND reference:?5*) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND reference:?5*)")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
//	    Pageable pageable);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND reference:?5*) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND reference:?5*)")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
//	    Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND reference:?3) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND reference:?3)")
//    List<ESReferenceDocument> findCustomGlobal(String doctorId, String locationId, String hospitalId, String searchTerm);
}
