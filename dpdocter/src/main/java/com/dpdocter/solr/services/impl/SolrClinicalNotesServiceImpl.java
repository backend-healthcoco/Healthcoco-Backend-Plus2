package com.dpdocter.solr.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
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

@Service
public class SolrClinicalNotesServiceImpl implements SolrClinicalNotesService {
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

	@Override
	public boolean addComplaints(SolrComplaintsDocument request) {
		boolean response = false;
		try {
			solrComplaintsRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Complaints");
		}
		return response;
	}

	@Override
	public boolean editComplaints(SolrComplaintsDocument request) {
		boolean response = false;
		try {
			solrComplaintsRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Complaints");
		}
		return response;
	}

	@Override
	public boolean deleteComplaints(String id) {
		boolean response = false;
		try {
			solrComplaintsRepository.delete(id);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Complaints");
		}
		return response;
	}

	@Override
	public List<SolrComplaintsDocument> searchComplaints(String searchTerm) {
		List<SolrComplaintsDocument> response = null;
		try {
			response = solrComplaintsRepository.findByQueryAnnotation(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Complaints");
		}
		return response;
	}

	@Override
	public boolean addDiagnoses(SolrDiagnosesDocument request) {
		boolean response = false;
		try {
			solrDiagnosesRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Diagnoses");
		}
		return response;
	}

	@Override
	public boolean editDiagnoses(SolrDiagnosesDocument request) {
		boolean response = false;
		try {
			solrDiagnosesRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Diagnoses");
		}
		return response;
	}

	@Override
	public boolean deleteDiagnoses(String id) {
		boolean response = false;
		try {
			solrDiagnosesRepository.delete(id);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Diagnoses");
		}
		return response;
	}

	@Override
	public List<SolrDiagnosesDocument> searchDiagnoses(String searchTerm) {
		List<SolrDiagnosesDocument> response = null;
		try {
			response = solrDiagnosesRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Diagnoses");
		}
		return response;
	}

	@Override
	public boolean addNotes(SolrNotesDocument request) {
		boolean response = false;
		try {
			solrNotesRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Notes");
		}
		return response;
	}

	@Override
	public boolean editNotes(SolrNotesDocument request) {
		boolean response = false;
		try {
			solrNotesRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Notes");
		}
		return response;
	}

	@Override
	public boolean deleteNotes(String id) {
		boolean response = false;
		try {
			solrNotesRepository.delete(id);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Notes");
		}
		return response;
	}

	@Override
	public List<SolrNotesDocument> searchNotes(String searchTerm) {
		List<SolrNotesDocument> response = null;
		try {
			response = solrNotesRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Notes");
		}
		return response;
	}

	@Override
	public boolean addDiagrams(SolrDiagramsDocument request) {
		boolean response = false;
		try {
			solrDiagramsRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Diagrams");
		}
		return response;
	}

	@Override
	public boolean editDiagrams(SolrDiagramsDocument request) {
		boolean response = false;
		try {
//			solrDiagramsRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Diagrams");
		}
		return response;
	}

	@Override
	public boolean deleteDiagrams(String id) {
		boolean response = false;
		try {
			solrDiagramsRepository.delete(id);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Diagrams");
		}
		return response;
	}

	@Override
	public List<SolrDiagramsDocument> searchDiagrams(String searchTerm) {
		List<SolrDiagramsDocument> response = null;
		try {
			response = solrDiagramsRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Investigations");
		}
		return response;
	}

	@Override
	public boolean editInvestigations(SolrInvestigationsDocument request) {
		boolean response = false;
		try {
			solrInvestigationsRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Investigations");
		}
		return response;
	}

	@Override
	public boolean deleteInvestigations(String id) {
		boolean response = false;
		try {
			solrInvestigationsRepository.delete(id);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Investigations");
		}
		return response;
	}

	@Override
	public List<SolrInvestigationsDocument> searchInvestigations(String searchTerm) {
		List<SolrInvestigationsDocument> response = null;
		try {
			response = solrInvestigationsRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Investigations");
		}
		return response;
	}

	@Override
	public boolean addObservations(SolrObservationsDocument request) {
		boolean response = false;
		try {
			solrObservationsRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Observations");
		}
		return response;
	}

	@Override
	public boolean editObservations(SolrObservationsDocument request) {
		boolean response = false;
		try {
			solrObservationsRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Observations");
		}
		return response;
	}

	@Override
	public boolean deleteObservations(String id) {
		boolean response = false;
		try {
			solrObservationsRepository.delete(id);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Observations");
		}
		return response;
	}

	@Override
	public List<SolrObservationsDocument> searchObservations(String searchTerm) {
		List<SolrObservationsDocument> response = null;
		try {
			response = solrObservationsRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Observations");
		}
		return response;
	}

}
