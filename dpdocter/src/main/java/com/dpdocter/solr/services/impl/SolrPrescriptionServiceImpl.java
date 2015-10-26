package com.dpdocter.solr.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.repository.SolrDrugRepository;
import com.dpdocter.solr.repository.SolrLabTestRepository;
import com.dpdocter.solr.services.SolrPrescriptionService;

@Service
public class SolrPrescriptionServiceImpl implements SolrPrescriptionService {

    private static Logger logger = Logger.getLogger(SolrPrescriptionServiceImpl.class.getName());

    @Autowired
    private SolrDrugRepository solrDrugRepository;

    @Autowired
    private SolrLabTestRepository solrLabTestRepository;

    @Override
    public boolean addDrug(SolrDrugDocument request) {
	boolean response = false;
	try {
	    solrDrugRepository.save(request);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Drug in Solr");
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
	    logger.error(e + " Error Occurred While Editing Drug in Solr");
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
	    logger.error(e + " Error Occurred While Deleting Drug in Solr");
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
	    logger.error(e + " Error Occurred While Searching Drug in Solr");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Drug");
	}
	return response;
    }

	@Override
	public boolean addLabTest(SolrLabTestDocument request) {
		boolean response = false;
		try {
		    solrLabTestRepository.save(request);
		    response = true;
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error Occurred While Saving Lab Test in Solr");
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Lab Test in Solr");
		}
		return response;		
	}

	@Override
	public boolean editLabTest(SolrLabTestDocument request) {
		boolean response = false;
		try {
			solrLabTestRepository.save(request);
		    response = true;
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error Occurred While Editing Lab Test in Solr");
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Lab Test");
		}
		return response;
	}

	@Override
	public boolean deleteLabTest(String labTestId) {
		boolean response = false;
		try {
			solrLabTestRepository.delete(labTestId);
		    response = true;
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error Occurred While Deleting Lab Test in Solr");
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
		}
		return response;
	}

	@Override
	public List<SolrLabTestDocument> searchLabTest(String searchTerm) {
		List<SolrLabTestDocument> response = null;
		try {
		    response = solrLabTestRepository.find(searchTerm);
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error Occurred While Searching Lab Test in Solr");
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Lab Test");
		}
		return response;
	}

}
