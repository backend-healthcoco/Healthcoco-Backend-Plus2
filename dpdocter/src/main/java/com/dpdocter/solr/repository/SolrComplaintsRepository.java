package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrComplaintsDocument;

public interface SolrComplaintsRepository extends SolrCrudRepository<SolrComplaintsDocument, String> {
    @Query("complaint:*?0*")
    public List<SolrComplaintsDocument> findByQueryAnnotation(String searchTerm);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND complaint:*?2*")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(Date date, boolean discards, String searchTerm, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND complaint:*?2*")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(Date date, boolean discards, String searchTerm, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(Date date, Boolean discarded, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(Date date, Boolean discarded, Sort sort);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded, Pageable pageRequest);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND complaint:*?3*) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND complaint:*?3*)")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND complaint:*?3*) OR (doctorId: \"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND complaint:*?3*)")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId:\"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId:\"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND complaint:*?5*) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId: \"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND complaint:*?5*)")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND complaint:*?5*) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId: \"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND complaint:*?5*)")
    public List<SolrComplaintsDocument> findCustomGlobalComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Sort sort);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrComplaintsDocument> findGlobalComplaints(Date date, Boolean discarded, Pageable pageRequest);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrComplaintsDocument> findGlobalComplaints(Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, Date date, Boolean discarded, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND complaint:*?3*")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND complaint:*?3*")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND complaint:*?5*")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND complaint:*?5*")
    public List<SolrComplaintsDocument> findCustomComplaints(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
	    String searchTerm, Sort sort);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND complaint:*?2*")
    public List<SolrComplaintsDocument> findGlobalComplaints(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND complaint:*?2*")
    public List<SolrComplaintsDocument> findGlobalComplaints(Date date, Boolean discarded, String searchTerm, Sort sort);

}
