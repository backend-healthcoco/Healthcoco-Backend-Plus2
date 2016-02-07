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

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Speciality;
import com.dpdocter.enums.Range;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.solr.document.SolrBloodGroupDocument;
import com.dpdocter.solr.document.SolrDiseasesDocument;
import com.dpdocter.solr.document.SolrEducationInstituteDocument;
import com.dpdocter.solr.document.SolrEducationQualificationDocument;
import com.dpdocter.solr.document.SolrMedicalCouncilDocument;
import com.dpdocter.solr.document.SolrProfessionDocument;
import com.dpdocter.solr.document.SolrProfessionalMembershipDocument;
import com.dpdocter.solr.document.SolrReferenceDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.repository.SolrBloodGroupRepository;
import com.dpdocter.solr.repository.SolrDiseaseRepository;
import com.dpdocter.solr.repository.SolrEducationInstituteRepository;
import com.dpdocter.solr.repository.SolrEducationQualificationRepository;
import com.dpdocter.solr.repository.SolrMedicalCouncilRepository;
import com.dpdocter.solr.repository.SolrProfessionRepository;
import com.dpdocter.solr.repository.SolrProfessionalMembershipRepository;
import com.dpdocter.solr.repository.SolrReferenceRepository;
import com.dpdocter.solr.repository.SolrSpecialityRepository;
import com.dpdocter.solr.services.SolrMasterService;
import common.util.web.DPDoctorUtils;

@Service
public class SolrMasterServiceImpl implements SolrMasterService {

    private static Logger logger = Logger.getLogger(SolrMasterServiceImpl.class.getName());

    @Autowired
    SolrReferenceRepository solrReferenceRepository;

    @Autowired
    SolrDiseaseRepository solrDiseaseRepository;

    @Autowired
    SolrBloodGroupRepository solrBloodGroupRepository;

    @Autowired
    SolrProfessionRepository solrProfessionRepository;

    @Autowired
    SolrProfessionalMembershipRepository solrProfessionalMembershipRepository;

    @Autowired
    SolrEducationInstituteRepository solrEducationInstituteRepository;

    @Autowired
    SolrEducationQualificationRepository solrEducationQualificationRepository;

    @Autowired
    SolrMedicalCouncilRepository solrMedicalCouncilRepository;

    @Autowired
    SolrSpecialityRepository solrSpecialityRepository;

    @Override
    public List<Reference> searchReference(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<Reference> response = null;

	try {

	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalReferences(page, size, updatedTime, discarded, searchTerm);
		break;
	    case CUSTOM:
		response = getCustomReferences(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		break;
	    case BOTH:
		response = getCustomGlobalReferences(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		break;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    private List<Reference> getGlobalReferences(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Reference> response = null;
	List<SolrReferenceDocument> referenceDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    referenceDocuments = solrReferenceRepository.findGlobal(new Date(createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    referenceDocuments = solrReferenceRepository
			    .findGlobal(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    referenceDocuments = solrReferenceRepository.findGlobal(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    referenceDocuments = solrReferenceRepository.findGlobal(new Date(createdTimeStamp), discarded, searchTerm, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    }
	    if (referenceDocuments != null) {
		response = new ArrayList<Reference>();
		BeanUtil.map(referenceDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    private List<Reference> getCustomReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<Reference> response = null;
	List<SolrReferenceDocument> referenceDocuments = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(doctorId))
		;
	    else {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	    if (referenceDocuments != null) {
		response = new ArrayList<Reference>();
		BeanUtil.map(referenceDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    private List<Reference> getCustomGlobalReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<Reference> response = null;
	List<SolrReferenceDocument> referenceDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
		    if (size > 0)
			referenceDocuments = solrReferenceRepository.findCustomGlobal(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			referenceDocuments = solrReferenceRepository.findCustomGlobal(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
		    if (size > 0)
			referenceDocuments = solrReferenceRepository.findCustomGlobal(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			referenceDocuments = solrReferenceRepository.findCustomGlobal(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    referenceDocuments = solrReferenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	    if (referenceDocuments != null) {
		response = new ArrayList<Reference>();
		BeanUtil.map(referenceDocuments, response);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public List<DiseaseListResponse> searchDisease(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<DiseaseListResponse> diseaseListResponses = null;

	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    diseaseListResponses = getGlobalDiseases(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    diseaseListResponses = getCustomDiseases(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    diseaseListResponses = getCustomGlobalDiseases(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getCustomDiseases(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<SolrDiseasesDocument> diseasesDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		diseasesDocuments = new ArrayList<SolrDiseasesDocument>();
	    else {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	    if (diseasesDocuments != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (SolrDiseasesDocument diseasesCollection : diseasesDocuments) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getDescription(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), null, diseasesCollection.getUpdatedTime(), null);
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getGlobalDiseases(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<SolrDiseasesDocument> diseasesDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    diseasesDocuments = solrDiseaseRepository.findGlobalDiseases(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    diseasesDocuments = solrDiseaseRepository.findGlobalDiseases(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    diseasesDocuments = solrDiseaseRepository.findGlobalDiseases(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    diseasesDocuments = solrDiseaseRepository.findGlobalDiseases(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }

	    if (diseasesDocuments != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (SolrDiseasesDocument diseasesCollection : diseasesDocuments) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getDescription(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), null, diseasesCollection.getUpdatedTime(), null);
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getCustomGlobalDiseases(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<SolrDiseasesDocument> diseasesDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (doctorId == null) {
		    if (size > 0)
			diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		if (doctorId == null) {
		    if (size > 0)
			diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), discarded, searchTerm, new PageRequest(
				page, size, Direction.DESC, "updatedTime"));
		    else
			diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), discarded, searchTerm, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discarded, searchTerm,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesDocuments = solrDiseaseRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	    if (diseasesDocuments != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (SolrDiseasesDocument diseasesCollection : diseasesDocuments) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getDescription(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), null, diseasesCollection.getUpdatedTime(), null);
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return diseaseListResponses;
    }

    @Override
    public List<BloodGroup> searchBloodGroup(String searchTerm, String updatedTime, int page, int size) {
	List<BloodGroup> response = null;
	List<SolrBloodGroupDocument> bloodGroupDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    bloodGroupDocuments = solrBloodGroupRepository.find(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    bloodGroupDocuments = solrBloodGroupRepository.find(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    bloodGroupDocuments = solrBloodGroupRepository.find(new Date(createdTimeStamp), searchTerm, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    bloodGroupDocuments = solrBloodGroupRepository.find(new Date(createdTimeStamp), searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
	    }
	    if (bloodGroupDocuments != null) {
		response = new ArrayList<BloodGroup>();
		BeanUtil.map(bloodGroupDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public List<Profession> searchProfession(String searchTerm, String updatedTime, int page, int size) {
	List<Profession> response = null;
	List<SolrProfessionDocument> professionDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    professionDocuments = solrProfessionRepository.find(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    professionDocuments = solrProfessionRepository.find(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    professionDocuments = solrProfessionRepository.find(new Date(createdTimeStamp), searchTerm, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    professionDocuments = solrProfessionRepository.find(new Date(createdTimeStamp), searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
	    }
	    if (professionDocuments != null) {
		response = new ArrayList<Profession>();
		BeanUtil.map(professionDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public List<ProfessionalMembership> searchProfessionalMembership(String searchTerm, String updatedTime, int page, int size) {
	List<ProfessionalMembership> response = null;
	List<SolrProfessionalMembershipDocument> professionalMembershipDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    professionalMembershipDocuments = solrProfessionalMembershipRepository.find(new Date(createdTimeStamp), new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    professionalMembershipDocuments = solrProfessionalMembershipRepository.find(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    professionalMembershipDocuments = solrProfessionalMembershipRepository.find(new Date(createdTimeStamp), searchTerm, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    professionalMembershipDocuments = solrProfessionalMembershipRepository.find(new Date(createdTimeStamp), searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }
	    if (professionalMembershipDocuments != null) {
		response = new ArrayList<ProfessionalMembership>();
		BeanUtil.map(professionalMembershipDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public List<EducationInstitute> searchEducationInstitute(String searchTerm, String updatedTime, int page, int size) {
	List<EducationInstitute> response = null;
	List<SolrEducationInstituteDocument> educationInstituteDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    educationInstituteDocuments = solrEducationInstituteRepository.find(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    educationInstituteDocuments = solrEducationInstituteRepository.find(new Date(createdTimeStamp),
			    new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    educationInstituteDocuments = solrEducationInstituteRepository.find(new Date(createdTimeStamp), searchTerm, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    educationInstituteDocuments = solrEducationInstituteRepository.find(new Date(createdTimeStamp), searchTerm, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    }
	    if (educationInstituteDocuments != null) {
		response = new ArrayList<EducationInstitute>();
		BeanUtil.map(educationInstituteDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public List<EducationQualification> searchEducationQualification(String searchTerm, String updatedTime, int page, int size) {
	List<EducationQualification> response = null;
	List<SolrEducationQualificationDocument> educationQualificationDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    educationQualificationDocuments = solrEducationQualificationRepository.find(new Date(createdTimeStamp), new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    educationQualificationDocuments = solrEducationQualificationRepository.find(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    educationQualificationDocuments = solrEducationQualificationRepository.find(new Date(createdTimeStamp), searchTerm, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    educationQualificationDocuments = solrEducationQualificationRepository.find(new Date(createdTimeStamp), searchTerm, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }
	    if (educationQualificationDocuments != null) {
		response = new ArrayList<EducationQualification>();
		BeanUtil.map(educationQualificationDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public List<MedicalCouncil> searchMedicalCouncil(String searchTerm, String updatedTime, int page, int size) {
	List<MedicalCouncil> response = null;
	List<SolrMedicalCouncilDocument> medicalCouncilDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    medicalCouncilDocuments = solrMedicalCouncilRepository.find(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    medicalCouncilDocuments = solrMedicalCouncilRepository.find(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    medicalCouncilDocuments = solrMedicalCouncilRepository.find(new Date(createdTimeStamp), searchTerm, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    medicalCouncilDocuments = solrMedicalCouncilRepository.find(new Date(createdTimeStamp), searchTerm, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    }
	    if (medicalCouncilDocuments != null) {
		response = new ArrayList<MedicalCouncil>();
		BeanUtil.map(medicalCouncilDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public List<Speciality> searchSpeciality(String searchTerm, String updatedTime, int page, int size) {
	List<Speciality> response = null;
	List<SolrSpecialityDocument> specialityDocuments = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (size > 0)
		    specialityDocuments = solrSpecialityRepository.find(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    specialityDocuments = solrSpecialityRepository.find(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    specialityDocuments = solrSpecialityRepository.find(new Date(createdTimeStamp), searchTerm, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    specialityDocuments = solrSpecialityRepository.find(new Date(createdTimeStamp), searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
	    }
	    if (specialityDocuments != null) {
		response = new ArrayList<Speciality>();
		BeanUtil.map(specialityDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

}
