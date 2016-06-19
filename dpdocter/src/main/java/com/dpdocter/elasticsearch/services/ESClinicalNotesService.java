package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;

public interface ESClinicalNotesService {

    boolean addComplaints(ESComplaintsDocument request);

    boolean addDiagnoses(ESDiagnosesDocument request);

    boolean addNotes(ESNotesDocument request);

    boolean addDiagrams(ESDiagramsDocument request);

//    List<ESDiagramsDocument> searchDiagramsBySpeciality(String searchTerm);

    boolean addInvestigations(ESInvestigationsDocument request);

    boolean addObservations(ESObservationsDocument request);

    List<ESObservationsDocument> searchObservations(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm);

    List<ESInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm);

    List<ESDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

    List<ESNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

    List<ESDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

    List<ESComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

}
