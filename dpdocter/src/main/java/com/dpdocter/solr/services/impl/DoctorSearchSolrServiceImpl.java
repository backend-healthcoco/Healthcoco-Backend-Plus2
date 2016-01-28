package com.dpdocter.solr.services.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.DoctorCoreDemoDocument;
import com.dpdocter.solr.document.SearchDoctorSolrDocument;
import com.dpdocter.solr.repository.DoctorCoreDemoRepository;
import com.dpdocter.solr.repository.DoctorSearchSolrRepository;
import com.dpdocter.solr.services.DoctorSearchSolrService;

@Service
public class DoctorSearchSolrServiceImpl implements DoctorSearchSolrService {

    private static Logger logger = Logger.getLogger(DoctorSearchSolrServiceImpl.class.getName());

    @Resource
    private DoctorSearchSolrRepository doctorSearchSolrRepository;

    @Resource
    private DoctorCoreDemoRepository doctorCoreDemoRepository;

    @Override
    public void addToIndex(SearchDoctorSolrDocument searchDoctorSolrDocument) {
	try {
	    doctorSearchSolrRepository.save(searchDoctorSolrDocument);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while saving");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while saving");
	}

    }

    @Override
    public void deleteFromIndex(String id) {
	try {
	    doctorSearchSolrRepository.delete(id);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden);
	}

    }

    @Override
    public List<SearchDoctorSolrDocument> findByQueryName(String text) {
	List<SearchDoctorSolrDocument> searchDoctorSolrDocuments = null;
	try {
	    searchDoctorSolrDocuments = doctorSearchSolrRepository.findByQueryAnnotation(text);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while searching");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while searching");
	}
	return searchDoctorSolrDocuments;
    }

    @Override
    public void addToIndex(DoctorCoreDemoDocument doctorCoreDemoDocument) {
	try {
	    doctorCoreDemoRepository.save(doctorCoreDemoDocument);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while saving");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while saving");
	}

    }

    @Override
    public List<DoctorCoreDemoDocument> findByQueryName1(String text) {
	List<DoctorCoreDemoDocument> doctorCoreDemoDocuments = null;
	try {
	    doctorCoreDemoDocuments = doctorCoreDemoRepository.findByQueryAnnotation(text);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while searching");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while searching");
	}
	return doctorCoreDemoDocuments;
    }

}
