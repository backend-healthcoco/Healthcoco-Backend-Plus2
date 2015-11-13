package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;

public interface SolrClinicalNotesService {

    boolean addComplaints(SolrComplaintsDocument request);

    boolean editComplaints(SolrComplaintsDocument request);

    boolean deleteComplaints(String id);

    boolean addDiagnoses(SolrDiagnosesDocument request);

    boolean editDiagnoses(SolrDiagnosesDocument request);

    boolean deleteDiagnoses(String id);

    boolean addNotes(SolrNotesDocument request);

    boolean editNotes(SolrNotesDocument request);

    boolean deleteNotes(String id);

    boolean addDiagrams(SolrDiagramsDocument request);

    boolean editDiagrams(SolrDiagramsDocument request);

    boolean deleteDiagrams(String id);

    List<SolrDiagramsDocument> searchDiagramsBySpeciality(String searchTerm);

    boolean addInvestigations(SolrInvestigationsDocument request);

    boolean editInvestigations(SolrInvestigationsDocument request);

    boolean deleteInvestigations(String id);

    boolean addObservations(SolrObservationsDocument request);

    boolean editObservations(SolrObservationsDocument request);

    boolean deleteObservations(String id);


	List<SolrObservationsDocument> searchObservations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<SolrInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<SolrDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<SolrNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<SolrDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<SolrComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

}
