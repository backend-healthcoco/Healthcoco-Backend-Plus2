package com.dpdocter.solr.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.repository.SolrDrugRepository;
import com.dpdocter.solr.services.SolrPrescriptionService;

@Service
public class SolrPrescriptionServiceImpl implements SolrPrescriptionService {
	@Autowired
	private SolrDrugRepository solrDrugRepository;

	@Override
	public boolean addDrug(SolrDrugDocument request) {
		boolean response = false;
		try {
			solrDrugRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug in Solr");
		}
		return response;
	}

	@Override
	public boolean editDrug(SolrDrugDocument request) {
		boolean response = false;
		try {
			solrDrugRepository.save(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug");
		}
		return response;
	}

	@Override
	public boolean deleteDrug(String id) {
		boolean response = false;
		try {
			solrDrugRepository.delete(id);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
		}
		return response;
	}

	@Override
	public List<SolrDrugDocument> searchDrug(String searchTerm) {
		List<SolrDrugDocument> response = null;
		try {
			response = solrDrugRepository.find(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Drug");
		}
		return response;
	}

}
