package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiagnosesDocument;

public interface SolrDiagnosesRepository extends SolrCrudRepository<SolrDiagnosesDocument, String> {
    @Query("diagnosis:*?0*")
    public List<SolrDiagnosesDocument> find(String searchTerm);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(Date date, Boolean discarded, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(Date date, Boolean discarded, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND diagnosis:*?2*")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND diagnosis:*?2*")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR (doctorId:null AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, Date date, Boolean discarded, Pageable pageRequest);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR (doctorId:null AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:null AND locationId:null AND hospitalId:null AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:null AND locationId:null AND hospitalId:null AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND diagnosis:*?3*) OR (doctorId:null AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND diagnosis:*?3*)")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND diagnosis:*?3*) OR (doctorId:null AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND diagnosis:*?3*)")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND diagnosis:*?5*) OR (doctorId:null AND locationId:null AND hospitalId: null AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND diagnosis:*?5*)")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND diagnosis:*?5*) OR (doctorId:null AND locationId:null AND hospitalId: null AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND diagnosis:*?5*)")
    public List<SolrDiagnosesDocument> findCustomGlobalDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Sort sort);

    @Query("doctorId: null AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDiagnosesDocument> findGlobalDiagnosis(Date date, Boolean discarded, Pageable pageRequest);

    @Query("doctorId: null AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDiagnosesDocument> findGlobalDiagnosis(Date date, Boolean discarded, Sort sort);

    @Query("doctorId: null AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND diagnosis:*?2*")
    public List<SolrDiagnosesDocument> findGlobalDiagnosis(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId: null AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND diagnosis:*?2*")
    public List<SolrDiagnosesDocument> findGlobalDiagnosis(Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, Date date, Boolean discarded, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND diagnosis:*?3*")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND diagnosis:*?3*")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND diagnosis:*?5*")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND diagnosis:*?5*")
    public List<SolrDiagnosesDocument> findCustomDiagnosis(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Sort sort);
}
