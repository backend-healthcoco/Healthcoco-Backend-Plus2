package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESNotesDocument;

public interface ESNotesRepository extends ElasticsearchRepository<ESNotesDocument, String> {
//    @Query("note:?0*")
//    public List<SolrNotesDocument> find(String searchTerm);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrNotesDocument> findCustomGlobalNotes(Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrNotesDocument> findCustomGlobalNotes(Date date, Boolean discarded, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND note:?2*")
//    public List<SolrNotesDocument> findCustomGlobalNotes(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND note:?2*")
//    public List<SolrNotesDocument> findCustomGlobalNotes(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, Date date, Boolean discarded, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND note:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND note:?3*)")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND note:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND note:?3*)")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND note:?5*) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND note:?5*)")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
//	    Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND note:?5*) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND note:?5*)")
//    public List<SolrNotesDocument> findCustomGlobalNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
//	    Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrNotesDocument> findGlobalNotes(Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrNotesDocument> findGlobalNotes(Date date, Boolean discarded, Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND note:?2*")
//    public List<SolrNotesDocument> findGlobalNotes(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND note:?2*")
//    public List<SolrNotesDocument> findGlobalNotes(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, Date date, Boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND note:?3*")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND note:?3*")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND note:?5*")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
//	    Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND note:?5*")
//    public List<SolrNotesDocument> findCustomNotes(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
//	    Sort sort);
}
