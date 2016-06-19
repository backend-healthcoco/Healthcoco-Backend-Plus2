package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;

public interface ESDiagnosticTestRepository extends ElasticsearchRepository<ESDiagnosticTestDocument, String> {

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"testName\": \"?0*\"}}]}}")
    List<ESDiagnosticTestDocument> findByTestName(String searchTerm);

//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, Pageable pageable);
//
//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, Sort sort);
//
//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
//    List<ESDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, String searchTerm, Pageable pageable);
//
//    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
//    List<ESDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
//    List<ESDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageable);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
//    List<ESDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*")
//    List<ESDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm,
//	    Pageable pageable);
//
//    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*")
//    List<ESDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, Sort sort);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageable);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, String searchTerm, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, String searchTerm, Sort sort);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*)")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm,
//	    Pageable pageable);
//
//    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName: ?4*)")
//    List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm,
//	    Sort sort);
}
