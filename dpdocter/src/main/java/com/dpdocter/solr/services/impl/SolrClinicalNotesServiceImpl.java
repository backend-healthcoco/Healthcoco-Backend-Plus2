package com.dpdocter.solr.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;
import com.dpdocter.solr.repository.SolrComplaintsRepository;
import com.dpdocter.solr.repository.SolrDiagnosesRepository;
import com.dpdocter.solr.repository.SolrDiagramsRepository;
import com.dpdocter.solr.repository.SolrInvestigationsRepository;
import com.dpdocter.solr.repository.SolrNotesRepository;
import com.dpdocter.solr.repository.SolrObservationsRepository;
import com.dpdocter.solr.services.SolrClinicalNotesService;

import common.util.web.DPDoctorUtils;

@Service
public class SolrClinicalNotesServiceImpl implements SolrClinicalNotesService {

    private static Logger logger = Logger.getLogger(SolrClinicalNotesServiceImpl.class.getName());

    @Autowired
    private SolrComplaintsRepository solrComplaintsRepository;

    @Autowired
    private SolrDiagnosesRepository solrDiagnosesRepository;

    @Autowired
    private SolrNotesRepository solrNotesRepository;

    @Autowired
    private SolrDiagramsRepository solrDiagramsRepository;

    @Autowired
    private SolrInvestigationsRepository solrInvestigationsRepository;

    @Autowired
    private SolrObservationsRepository solrObservationsRepository;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Override
    public boolean addComplaints(SolrComplaintsDocument request) {
	boolean response = false;
	try {
	    solrComplaintsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.COMPLAINT, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Complaints");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Complaints");
	}
	return response;
    }

    @Override
    public boolean editComplaints(SolrComplaintsDocument request) {
	boolean response = false;
	try {
	    solrComplaintsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.COMPLAINT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Complaints");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Editing Complaints");
	}
	return response;
    }

    @Override
    public boolean deleteComplaints(String id, Boolean discarded) {
	boolean response = false;
	try {
	    SolrComplaintsDocument complaintsDocument = solrComplaintsRepository.findOne(id);
	    if (complaintsDocument != null) {
		complaintsDocument.setDiscarded(discarded);
		complaintsDocument.setUpdatedTime(new Date());
		solrComplaintsRepository.save(complaintsDocument);
	    }
	    response = true;
	    transnationalService.addResource(id, Resource.COMPLAINT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Complaints");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Deleting Complaints");
	}
	return response;
    }

    @Override
    public boolean addDiagnoses(SolrDiagnosesDocument request) {
	boolean response = false;
	try {
	    solrDiagnosesRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DIAGNOSIS, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Diagnosis");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Diagnoses");
	}
	return response;
    }

    @Override
    public boolean editDiagnoses(SolrDiagnosesDocument request) {
	boolean response = false;
	try {
	    solrDiagnosesRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DIAGNOSIS, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Diagnosis");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Editing Diagnoses");
	}
	return response;
    }

    @Override
    public boolean deleteDiagnoses(String id, Boolean discarded) {
	boolean response = false;
	try {
	    SolrDiagnosesDocument diagnosesDocument = solrDiagnosesRepository.findOne(id);
	    if (diagnosesDocument != null) {
		diagnosesDocument.setDiscarded(discarded);
		diagnosesDocument.setUpdatedTime(new Date());
		solrDiagnosesRepository.save(diagnosesDocument);
	    }
	    response = true;
	    transnationalService.addResource(id, Resource.DIAGNOSIS, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Diagnosis");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Deleting Diagnoses");
	}
	return response;
    }

    @Override
    public boolean addNotes(SolrNotesDocument request) {
	boolean response = false;
	try {
	    solrNotesRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.NOTES, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Notes");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Notes");
	}
	return response;
    }

    @Override
    public boolean editNotes(SolrNotesDocument request) {
	boolean response = false;
	try {
	    solrNotesRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.NOTES, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Notes");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Editing Notes");
	}
	return response;
    }

    @Override
    public boolean deleteNotes(String id, Boolean discarded) {
	boolean response = false;
	try {
	    SolrNotesDocument notesDocument = solrNotesRepository.findOne(id);
	    if (notesDocument != null) {
		notesDocument.setDiscarded(discarded);
		notesDocument.setUpdatedTime(new Date());
		solrNotesRepository.save(notesDocument);
	    }
	    response = true;
	    transnationalService.addResource(id, Resource.NOTES, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Notes");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Deleting Notes");
	}
	return response;
    }

    @Override
    public boolean addDiagrams(SolrDiagramsDocument request) {
	boolean response = false;
	try {
	    solrDiagramsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DIAGRAM, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Diagrams");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Diagrams");
	}
	return response;
    }

    @Override
    public boolean editDiagrams(SolrDiagramsDocument request) {
	boolean response = false;
	try {
	    solrDiagramsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DIAGRAM, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Diagrams");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Editing Diagrams");
	}
	return response;
    }

    @Override
    public boolean deleteDiagrams(String id, Boolean discarded) {
	boolean response = false;
	try {
	    SolrDiagramsDocument diagramsDocument = solrDiagramsRepository.findOne(id);
	    if (diagramsDocument != null) {
		diagramsDocument.setDiscarded(discarded);
		diagramsDocument.setUpdatedTime(new Date());
		solrDiagramsRepository.save(diagramsDocument);
	    }
	    response = true;
	    transnationalService.addResource(id, Resource.DIAGRAM, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Diagrams");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Deleting Diagrams");
	}
	return response;
    }

    @Override
    public List<SolrDiagramsDocument> searchDiagramsBySpeciality(String searchTerm) {
	List<SolrDiagramsDocument> response = null;
	try {
	    response = solrDiagramsRepository.findBySpeciality(searchTerm);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Diagrams");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Diagrams");
	}
	return response;
    }

    @Override
    public boolean addInvestigations(SolrInvestigationsDocument request) {
	boolean response = false;
	try {
	    solrInvestigationsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.INVESTIGATION, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Investigations");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Investigations");
	}
	return response;
    }

    @Override
    public boolean editInvestigations(SolrInvestigationsDocument request) {
	boolean response = false;
	try {
	    solrInvestigationsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.INVESTIGATION, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Investigations");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Editing Investigations");
	}
	return response;
    }

    @Override
    public boolean deleteInvestigations(String id, Boolean discarded) {
	boolean response = false;
	try {
	    SolrInvestigationsDocument investigationsDocument = solrInvestigationsRepository.findOne(id);
	    if (investigationsDocument != null) {
		investigationsDocument.setDiscarded(discarded);
		investigationsDocument.setUpdatedTime(new Date());
		solrInvestigationsRepository.save(investigationsDocument);
	    }
	    response = true;
	    transnationalService.addResource(id, Resource.INVESTIGATION, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Investigations");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Deleting Investigations");
	}
	return response;
    }

    @Override
    public boolean addObservations(SolrObservationsDocument request) {
	boolean response = false;
	try {
	    solrObservationsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.OBSERVATION, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Observations");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Observations");
	}
	return response;
    }

    @Override
    public boolean editObservations(SolrObservationsDocument request) {
	boolean response = false;
	try {
	    solrObservationsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.OBSERVATION, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Observations");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Editing Observations");
	}
	return response;
    }

    @Override
    public boolean deleteObservations(String id, Boolean discarded) {
	boolean response = false;
	try {
	    SolrObservationsDocument observationsDocument = solrObservationsRepository.findOne(id);
	    if (observationsDocument != null) {
		observationsDocument.setDiscarded(discarded);
		observationsDocument.setUpdatedTime(new Date());
		solrObservationsRepository.save(observationsDocument);
	    }
	    response = true;
	    transnationalService.addResource(id, Resource.OBSERVATION, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Observations");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Deleting Observations");
	}
	return response;
    }

    @Override
    public List<SolrObservationsDocument> searchObservations(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrObservationsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalObservations(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}

	return response;
    }

    @Override
    public List<SolrInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrInvestigationsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalInvestigations(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}

	return response;
    }

    @Override
    public List<SolrDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrDiagramsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {
	case GLOBAL:
	    response = getGlobalDiagrams(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    @Override
    public List<SolrNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrNotesDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {
	case GLOBAL:
	    response = getGlobalNotes(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    @Override
    public List<SolrDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrDiagnosesDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {
	case GLOBAL:
	    response = getGlobalDiagnosis(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    @Override
    public List<SolrComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrComplaintsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalComplaints(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    private List<SolrComplaintsDocument> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrComplaintsDocument> complaintCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discarded, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discarded, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discarded, searchTerm,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return complaintCollections;

    }

    private List<SolrComplaintsDocument> getGlobalComplaints(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrComplaintsDocument> complaintCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    complaintCollections = solrComplaintsRepository.findGlobalComplaints(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    complaintCollections = solrComplaintsRepository.findGlobalComplaints(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    complaintCollections = solrComplaintsRepository.findGlobalComplaints(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(
			    page, size, Direction.DESC, "updatedTime"));
		else
		    complaintCollections = solrComplaintsRepository.findGlobalComplaints(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return complaintCollections;
    }

    private List<SolrComplaintsDocument> getCustomComplaints(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrComplaintsDocument> complaintCollections = null;

	try {

	    if (doctorId == null)
		complaintCollections = new ArrayList<SolrComplaintsDocument>();

	    else {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    complaintCollections = solrComplaintsRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return complaintCollections;
    }

    private List<SolrDiagramsDocument> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrDiagramsDocument> diagramCollections = null;

	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discarded, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discarded, searchTerm,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return diagramCollections;
    }

    private List<SolrDiagramsDocument> getGlobalDiagrams(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrDiagramsDocument> diagramCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    diagramCollections = solrDiagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    diagramCollections = solrDiagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    diagramCollections = solrDiagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    diagramCollections = solrDiagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return diagramCollections;
    }

    private List<SolrDiagramsDocument> getCustomDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrDiagramsDocument> diagramCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null)
		diagramCollections = new ArrayList<SolrDiagramsDocument>();
	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagramCollections = solrDiagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return diagramCollections;
    }

    private List<SolrInvestigationsDocument> getCustomGlobalInvestigations(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrInvestigationsDocument> investigationsCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discarded,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discarded,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discarded,
				searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discarded,
				searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		}

	    } else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return investigationsCollections;
    }

    private List<SolrInvestigationsDocument> getGlobalInvestigations(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrInvestigationsDocument> investigationsCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    investigationsCollections = solrInvestigationsRepository.findGlobalInvestigations(new Date(createdTimeStamp), discarded, new PageRequest(
			    page, size, Direction.DESC, "updatedTime"));
		else
		    investigationsCollections = solrInvestigationsRepository.findGlobalInvestigations(new Date(createdTimeStamp), discarded, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    investigationsCollections = solrInvestigationsRepository.findGlobalInvestigations(new Date(createdTimeStamp), discarded, searchTerm,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    investigationsCollections = solrInvestigationsRepository.findGlobalInvestigations(new Date(createdTimeStamp), discarded, searchTerm,
			    new Sort(Sort.Direction.DESC, "updatedTime"));

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return investigationsCollections;
    }

    private List<SolrInvestigationsDocument> getCustomInvestigations(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrInvestigationsDocument> investigationsCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		investigationsCollections = new ArrayList<SolrInvestigationsDocument>();
	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    investigationsCollections = solrInvestigationsRepository.findCustomInvestigations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return investigationsCollections;
    }

    private List<SolrObservationsDocument> getCustomGlobalObservations(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrObservationsDocument> observationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			observationCollections = solrObservationsRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discarded,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			observationCollections = solrObservationsRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discarded, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			observationCollections = solrObservationsRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discarded, searchTerm,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			observationCollections = solrObservationsRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discarded, searchTerm,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "createdTime"));
		    } else {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "createdTime"));

		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));
		    } else {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));

		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return observationCollections;

    }

    private List<SolrObservationsDocument> getGlobalObservations(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrObservationsDocument> observationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    observationCollections = solrObservationsRepository.findGlobalObservations(new Date(createdTimeStamp), discarded, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    observationCollections = solrObservationsRepository.findGlobalObservations(new Date(createdTimeStamp), discarded, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    observationCollections = solrObservationsRepository.findGlobalObservations(new Date(createdTimeStamp), discarded, searchTerm,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    observationCollections = solrObservationsRepository.findGlobalObservations(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return observationCollections;
    }

    private List<SolrObservationsDocument> getCustomObservations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrObservationsDocument> observationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		observationCollections = new ArrayList<SolrObservationsDocument>();
	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), searchTerm, discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    observationCollections = solrObservationsRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), searchTerm, discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return observationCollections;
    }

    private List<SolrDiagnosesDocument> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrDiagnosesDocument> diagnosisCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discarded, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discarded, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discarded, searchTerm,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));

		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "createdTime"));

		    } else {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));

		    } else {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(
				    createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return diagnosisCollections;
    }

    private List<SolrDiagnosesDocument> getGlobalDiagnosis(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrDiagnosesDocument> diagnosisCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    diagnosisCollections = solrDiagnosesRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    diagnosisCollections = solrDiagnosesRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    diagnosisCollections = solrDiagnosesRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    diagnosisCollections = solrDiagnosesRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return diagnosisCollections;
    }

    private List<SolrDiagnosesDocument> getCustomDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrDiagnosesDocument> diagnosisCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		diagnosisCollections = new ArrayList<SolrDiagnosesDocument>();
	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diagnosisCollections = solrDiagnosesRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return diagnosisCollections;
    }

    private List<SolrNotesDocument> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {

	List<SolrNotesDocument> notesCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			notesCollections = solrNotesRepository.findCustomGlobalNotes(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			notesCollections = solrNotesRepository.findCustomGlobalNotes(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			notesCollections = solrNotesRepository.findCustomGlobalNotes(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			notesCollections = solrNotesRepository.findCustomGlobalNotes(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "createdTime"));
		    }
		} else {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return notesCollections;

    }

    private List<SolrNotesDocument> getGlobalNotes(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrNotesDocument> notesCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    notesCollections = solrNotesRepository.findGlobalNotes(new Date(createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    notesCollections = solrNotesRepository.findGlobalNotes(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    notesCollections = solrNotesRepository.findGlobalNotes(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    notesCollections = solrNotesRepository.findGlobalNotes(new Date(createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC,
			    "updatedTime"));

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return notesCollections;
    }

    private List<SolrNotesDocument> getCustomNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrNotesDocument> notesCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null)
		notesCollections = new ArrayList<SolrNotesDocument>();

	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }

		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    notesCollections = solrNotesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }

		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return notesCollections;
    }

}
