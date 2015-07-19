package com.dpdocter.solr.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrComplaints;
import com.dpdocter.solr.document.SolrDiagnoses;
import com.dpdocter.solr.document.SolrDiagrams;
import com.dpdocter.solr.document.SolrInvestigations;
import com.dpdocter.solr.document.SolrNotes;
import com.dpdocter.solr.document.SolrObservations;
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
	public boolean addComplaints(SolrComplaints request) {
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
	public boolean editComplaints(SolrComplaints request) {
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
	public List<SolrComplaints> searchComplaints(String searchTerm) {
		List<SolrComplaints> response = null;
		try {
			response = solrComplaintsRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Complaints");
		}
		return response;
	}

	@Override
	public boolean addDiagnoses(SolrDiagnoses request) {
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
	public boolean editDiagnoses(SolrDiagnoses request) {
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
	public List<SolrDiagnoses> searchDiagnoses(String searchTerm) {
		List<SolrDiagnoses> response = null;
		try {
			response = solrDiagnosesRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Diagnoses");
		}
		return response;
	}

	@Override
	public boolean addNotes(SolrNotes request) {
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
	public boolean editNotes(SolrNotes request) {
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
	public List<SolrNotes> searchNotes(String searchTerm) {
		List<SolrNotes> response = null;
		try {
			response = solrNotesRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Notes");
		}
		return response;
	}

	@Override
	public boolean addDiagrams(SolrDiagrams request) {
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
	public boolean editDiagrams(SolrDiagrams request) {
		boolean response = false;
		try {
			solrDiagramsRepository.save(request);
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
	public List<SolrDiagrams> searchDiagrams(String searchTerm) {
		List<SolrDiagrams> response = null;
		try {
			response = solrDiagramsRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Diagrams");
		}
		return response;
	}

	@Override
	public boolean addInvestigations(SolrInvestigations request) {
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
	public boolean editInvestigations(SolrInvestigations request) {
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
	public List<SolrInvestigations> searchInvestigations(String searchTerm) {
		List<SolrInvestigations> response = null;
		try {
			response = solrInvestigationsRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Investigations");
		}
		return response;
	}

	@Override
	public boolean addObservations(SolrObservations request) {
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
	public boolean editObservations(SolrObservations request) {
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
	public List<SolrObservations> searchObservations(String searchTerm) {
		List<SolrObservations> response = null;
		try {
			response = solrObservationsRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Observations");
		}
		return response;
	}

}
