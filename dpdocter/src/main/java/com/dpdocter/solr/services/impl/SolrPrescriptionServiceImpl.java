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
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.repository.SolrDrugRepository;
import com.dpdocter.solr.repository.SolrLabTestRepository;
import com.dpdocter.solr.services.SolrPrescriptionService;
import com.dpdocter.webservices.PrescriptionApi;

import common.util.web.DPDoctorUtils;

@Service
public class SolrPrescriptionServiceImpl implements SolrPrescriptionService {

    private static Logger logger = Logger.getLogger(SolrPrescriptionServiceImpl.class.getName());

    @Autowired
    private SolrDrugRepository solrDrugRepository;

    @Autowired
    private SolrLabTestRepository solrLabTestRepository;
    
    @Autowired
    private TransactionalManagementService transnationalService;

    @Override
    public boolean addDrug(SolrDrugDocument request) {
	boolean response = false;
	try {
	    solrDrugRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DRUG, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Drug in Solr");
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug in Solr");
	}
	return response;
    }

    @Override
    public boolean editDrug(SolrDrugDocument request) {
	boolean response = false;
	try {
	    solrDrugRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DRUG, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Drug in Solr");
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug");
	}
	return response;
    }

    @Override
    public boolean deleteDrug(String id, Boolean discarded) {
	boolean response = false;
	try {
	    SolrDrugDocument drugDocument = solrDrugRepository.findOne(id);
	    if(drugDocument != null){
	    	drugDocument.setDiscarded(discarded);
	    	drugDocument.setUpdatedTime(new Date());
	    	solrDrugRepository.save(drugDocument);
	    }
	    response = true;
	    transnationalService.addResource(id, Resource.DRUG, true);
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug in Solr");
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
	}
	return response;
    }

    @Override
    public boolean addLabTest(SolrLabTestDocument request) {
	boolean response = false;
	try {
	    solrLabTestRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.LABTEST, true);
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Lab Test in Solr");
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Lab Test in Solr");
	}
	return response;
    }

    @Override
    public boolean editLabTest(SolrLabTestDocument request) {
	boolean response = false;
	try {
	    solrLabTestRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.LABTEST, true);
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Lab Test in Solr");
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Lab Test");
	}
	return response;
    }

    @Override
    public boolean deleteLabTest(String labTestId, Boolean discarded) {
	boolean response = false;
	try {
		SolrLabTestDocument  labTestDocument = solrLabTestRepository.findOne(labTestId);
		if(labTestDocument != null){
			labTestDocument.setDiscarded(discarded);
			labTestDocument.setUpdatedTime(new Date());
		    solrLabTestRepository.save(labTestDocument);
		}
	    response = true;
	    transnationalService.addResource(labTestId, Resource.LABTEST, true);
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Lab Test in Solr");
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
	}
	return response;
    }

    @Override
    public List<SolrDrugDocument> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrDrugDocument> response = new ArrayList<SolrDrugDocument>();
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalDrugs(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;

    }

    private List<SolrDrugDocument> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<SolrDrugDocument> solrDrugDocument = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    solrDrugDocument = solrDrugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return solrDrugDocument;
    }

    private List<SolrDrugDocument> getGlobalDrugs(int page, int size, String updatedTime, boolean discarded, String searchTerm) {
	List<SolrDrugDocument> SolrDrugDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    SolrDrugDocuments = solrDrugRepository.getGlobalDrugs(new Date(createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));

		else
		    SolrDrugDocuments = solrDrugRepository.getGlobalDrugs(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    SolrDrugDocuments = solrDrugRepository.getGlobalDrugs(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));

		else
		    SolrDrugDocuments = solrDrugRepository.getGlobalDrugs(new Date(createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC,
			    "updatedTime"));

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return SolrDrugDocuments;
    }

    private List<SolrDrugDocument> getCustomDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<SolrDrugDocument> SolrDrugDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		SolrDrugDocuments = new ArrayList<SolrDrugDocument>();
	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    SolrDrugDocuments = solrDrugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return SolrDrugDocuments;
    }

    @Override
    public List<SolrLabTestDocument> searchLabTest(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<SolrLabTestDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalLabTests(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomLabTests(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalLabTests(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    private List<SolrLabTestDocument> getGlobalLabTests(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<SolrLabTestDocument> labTestCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    labTestCollections = solrLabTestRepository.getGlobalLabTests(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));

		else
		    labTestCollections = solrLabTestRepository.getGlobalLabTests(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    labTestCollections = solrLabTestRepository.getGlobalLabTests(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));

		else
		    labTestCollections = solrLabTestRepository.getGlobalLabTests(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return labTestCollections;
    }

    private List<SolrLabTestDocument> getCustomLabTests(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<SolrLabTestDocument> labTestCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		labTestCollections = new ArrayList<SolrLabTestDocument>();

	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }

		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return labTestCollections;
    }

    private List<SolrLabTestDocument> getCustomGlobalLabTests(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<SolrLabTestDocument> labTestCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (size > 0)
			labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(
				page, size, Direction.DESC, "updatedTime"));
		    else
			labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));

		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    labTestCollections = solrLabTestRepository.getCustomGlobalLabTests(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }

		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return labTestCollections;
    }

}
