package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Speciality;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;
import com.dpdocter.elasticsearch.document.ESEducationQualificationDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.document.ESMedicalCouncilDocument;
import com.dpdocter.elasticsearch.document.ESProfessionDocument;
import com.dpdocter.elasticsearch.document.ESProfessionalMembershipDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESSymptomDiseaseConditionDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDiseaseRepository;
import com.dpdocter.elasticsearch.repository.ESDrugRepository;
import com.dpdocter.elasticsearch.repository.ESEducationInstituteRepository;
import com.dpdocter.elasticsearch.repository.ESEducationQualificationRepository;
import com.dpdocter.elasticsearch.repository.ESLandmarkLocalityRepository;
import com.dpdocter.elasticsearch.repository.ESMedicalCouncilRepository;
import com.dpdocter.elasticsearch.repository.ESProfessionRepository;
import com.dpdocter.elasticsearch.repository.ESProfessionalMembershipRepository;
import com.dpdocter.elasticsearch.repository.ESReferenceRepository;
import com.dpdocter.elasticsearch.repository.ESServicesRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.repository.ESSymptomDiseaseConditionRepository;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESMasterServiceImpl implements ESMasterService {

    private static Logger logger = Logger.getLogger(ESMasterServiceImpl.class.getName());

    @Autowired
    private DrugRepository drugRepository;
    
    @Autowired
    private ESDrugRepository esDrugRepository;
    
    @Autowired
    private LandmarkLocalityRepository landmarkLocalityRepository;
    
    @Autowired
    private TransactionalManagementService transactionalManagementService;
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private ESLandmarkLocalityRepository esLocalityLandmarkRepository;
    
    @Autowired
    private ESCityRepository esCityRepository;
    
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ESReferenceRepository esReferenceRepository;

    @Autowired
    ESDiseaseRepository esDiseaseRepository;

    @Autowired
    ESProfessionRepository esProfessionRepository;

    @Autowired
    ESProfessionalMembershipRepository esProfessionalMembershipRepository;

    @Autowired
    ESEducationInstituteRepository esEducationInstituteRepository;

    @Autowired
    ESEducationQualificationRepository esEducationQualificationRepository;

    @Autowired
    ESMedicalCouncilRepository esMedicalCouncilRepository;

    @Autowired
    ESSpecialityRepository esSpecialityRepository;

    @Autowired
    ESDiagnosticTestRepository esDiagnosticTestRepository;
    
    @Autowired
	ESServicesRepository esServicesRepository;
    
    @Autowired
	ESSymptomDiseaseConditionRepository esSymptomDiseaseConditionRepository;;
    
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<Reference> getGlobalReferences(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Reference> response = null;
	List<ESReferenceDocument> referenceDocuments = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.REFERENCE, page, size, updatedTime, discarded, "reference", searchTerm, null, null, null, "reference");
		referenceDocuments = elasticsearchTemplate.queryForList(searchQuery, ESReferenceDocument.class);
		if (referenceDocuments != null) {
			response = new ArrayList<Reference>();
			BeanUtil.map(referenceDocuments, response);
		}
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<Reference> getCustomReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<Reference> response = null;
	List<ESReferenceDocument> referenceDocuments = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "reference", searchTerm, null, null, "reference");
		referenceDocuments = elasticsearchTemplate.queryForList(searchQuery, ESReferenceDocument.class);
		if (referenceDocuments != null) {
			response = new ArrayList<Reference>();
			BeanUtil.map(referenceDocuments, response);
		}
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<Reference> getCustomGlobalReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<Reference> response = null;
	List<ESReferenceDocument> referenceDocuments = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.REFERENCE, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "reference", searchTerm, null, null, null, "reference");
		referenceDocuments = elasticsearchTemplate.queryForList(searchQuery, ESReferenceDocument.class);
		
	    if (referenceDocuments != null) {
		response = new ArrayList<Reference>();
		BeanUtil.map(referenceDocuments, response);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
	List<ESDiseasesDocument> diseasesDocuments = null;
	try {

		if (DPDoctorUtils.anyStringEmpty(doctorId))diseasesDocuments = new ArrayList<ESDiseasesDocument>();
	    else {
	    	SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "disease", searchTerm, null, null, "disease");
	        diseasesDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDiseasesDocument.class);
	    }
	    if (diseasesDocuments != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (ESDiseasesDocument diseasesCollection : diseasesDocuments) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getExplanation(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), null, diseasesCollection.getUpdatedTime(), null);
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getGlobalDiseases(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<ESDiseasesDocument> diseasesDocuments = null;
	try {	
		
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DISEASE, page, size, updatedTime, discarded, "disease", searchTerm, null, null, null, "disease");
        diseasesDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDiseasesDocument.class);

	    if (diseasesDocuments != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (ESDiseasesDocument diseasesCollection : diseasesDocuments) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getExplanation(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), null, diseasesCollection.getUpdatedTime(), null);
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getCustomGlobalDiseases(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<ESDiseasesDocument> diseasesDocuments = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DISEASE, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "disease", searchTerm, null, null, null, "disease");
		
        diseasesDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDiseasesDocument.class);

        if (diseasesDocuments != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (ESDiseasesDocument diseasesCollection : diseasesDocuments) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getExplanation(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), null, diseasesCollection.getUpdatedTime(), null);
		    diseaseListResponses.add(diseaseListResponse);
			}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diseaseListResponses;
    }

    @Override
    public List<BloodGroup> searchBloodGroup() {
	List<BloodGroup> response = new ArrayList<BloodGroup>();
	try {
	    for (com.dpdocter.enums.BloodGroup group : com.dpdocter.enums.BloodGroup.values()) {
	    	  BloodGroup bloodGroup = new BloodGroup();
	    	  bloodGroup.setBloodGroup(group.getGroup());
	    	  response.add(bloodGroup);
	    	}
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<Profession> searchProfession(String searchTerm, String updatedTime, int page, int size) {
	List<Profession> response = null;
	List<ESProfessionDocument> professionDocuments = null;
	try {
	   BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
	   if (!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("profession", searchTerm));
	   
       SearchQuery searchQuery = null;
       if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, "profession")).build();
       else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("profession").order(SortOrder.ASC)).build();
       
	   professionDocuments = elasticsearchTemplate.queryForList(searchQuery, ESProfessionDocument.class);

	    if (professionDocuments != null) {
	    	response = new ArrayList<Profession>();
	    	BeanUtil.map(professionDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

	@Override
	public Boolean add() {
		
		List<LandmarkLocalityCollection> landmarkLocalityCollections = landmarkLocalityRepository.findAll();
		
		for(LandmarkLocalityCollection landmarkLocalityCollection : landmarkLocalityCollections){
			ESLandmarkLocalityDocument landmarkLocalityDocument = new ESLandmarkLocalityDocument();
			BeanUtil.map(landmarkLocalityCollection, landmarkLocalityDocument);
			landmarkLocalityDocument.setGeoPoint(new GeoPoint(landmarkLocalityDocument.getLatitude(), landmarkLocalityDocument.getLongitude()));
			esLocalityLandmarkRepository.save(landmarkLocalityDocument);
		}
		
       List<CityCollection> cityCollections = cityRepository.findAll();
		
		for(CityCollection professionCollection : cityCollections){
			ESCityDocument professionDocument = new ESCityDocument();
			BeanUtil.map(professionCollection, professionDocument);
			professionDocument.setGeoPoint(new GeoPoint(professionDocument.getLatitude(), professionDocument.getLongitude()));
			esCityRepository.save(professionDocument);
		}
		
		List<DrugCollection> drugCollections = drugRepository.findAll();
		
		for(DrugCollection drugCollection : drugCollections){
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugCollection, esDrugDocument);
			if(drugCollection.getDrugType()!=null){
				esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
				esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
			}
			esDrugRepository.save(esDrugDocument);
		}
		return true;
	}

    @Override
    public List<ProfessionalMembership> searchProfessionalMembership(String searchTerm, String updatedTime, int page, int size) {
	List<ProfessionalMembership> response = null;
	List<ESProfessionalMembershipDocument> professionalMembershipDocuments = null;
	try {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("membership", searchTerm));
		   
	    SearchQuery searchQuery = null;
	    if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, "membership")).build();
	    else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("membership").order(SortOrder.ASC)).build();
	       
	    professionalMembershipDocuments = elasticsearchTemplate.queryForList(searchQuery, ESProfessionalMembershipDocument.class);

	    if (professionalMembershipDocuments != null) {
		response = new ArrayList<ProfessionalMembership>();
		BeanUtil.map(professionalMembershipDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<EducationInstitute> searchEducationInstitute(String searchTerm, String updatedTime, int page, int size) {
	List<EducationInstitute> response = null;
	List<ESEducationInstituteDocument> educationInstituteDocuments = null;
	try {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm));
		   
	    SearchQuery searchQuery = null;
	    if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, "name")).build();
	    else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("name").order(SortOrder.ASC)).build();
	       
	    educationInstituteDocuments = elasticsearchTemplate.queryForList(searchQuery, ESEducationInstituteDocument.class);

	    if (educationInstituteDocuments != null) {
		response = new ArrayList<EducationInstitute>();
		BeanUtil.map(educationInstituteDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<EducationQualification> searchEducationQualification(String searchTerm, String updatedTime, int page, int size) {
	List<EducationQualification> response = null;
	List<ESEducationQualificationDocument> educationQualificationDocuments = null;
	try {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm));
		   
	    SearchQuery searchQuery = null;
	    if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, "name")).build();
	    else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("name").order(SortOrder.ASC)).build();
	       
	    educationQualificationDocuments = elasticsearchTemplate.queryForList(searchQuery, ESEducationQualificationDocument.class);

	    if (educationQualificationDocuments != null) {
		response = new ArrayList<EducationQualification>();
		BeanUtil.map(educationQualificationDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<MedicalCouncil> searchMedicalCouncil(String searchTerm, String updatedTime, int page, int size) {
	List<MedicalCouncil> response = null;
	List<ESMedicalCouncilDocument> medicalCouncilDocuments = null;
	try {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("medicalCouncil", searchTerm));
		   
	    SearchQuery searchQuery = null;
	    if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, "medicalCouncil")).build();
	    else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("medicalCouncil").order(SortOrder.ASC)).build();
	       
	    medicalCouncilDocuments = elasticsearchTemplate.queryForList(searchQuery, ESMedicalCouncilDocument.class);

	    if (medicalCouncilDocuments != null) {
		response = new ArrayList<MedicalCouncil>();
		BeanUtil.map(medicalCouncilDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<Speciality> searchSpeciality(String searchTerm, String updatedTime, int page, int size) {
	List<Speciality> response = null;
	List<ESSpecialityDocument> specialityDocuments = null;
	try {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("superSpeciality", searchTerm));
		   
	    SearchQuery searchQuery = null;
	    if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, "superSpeciality")).build();
	    else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("superSpeciality").order(SortOrder.ASC)).build();
	       
	    specialityDocuments = elasticsearchTemplate.queryForList(searchQuery, ESSpecialityDocument.class);

	    if (specialityDocuments != null) {
		response = new ArrayList<Speciality>();
		BeanUtil.map(specialityDocuments, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

	@Override
	public void addEditDisease(ESDiseasesDocument esDiseasesDocument) {
		try {
		    esDiseaseRepository.save(esDiseasesDocument);
		    transactionalManagementService.addResource(new ObjectId(esDiseasesDocument.getId()), Resource.DISEASE, true);
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error("Error while adding disease " + e.getMessage());
		}
	}
	@Override
	public void addEditServices(ESServicesDocument esServicesDocument) {
		try {
			esServicesRepository.save(esServicesDocument);
			transactionalManagementService.addResource(new ObjectId(esServicesDocument.getId()), Resource.SERVICE,
					true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while adding  ES data service " + e.getMessage());
		}
	}

	@Override
	public void addEditSpecialities(ESSpecialityDocument esSpecialityDocument) {
		try {
			esSpecialityRepository.save(esSpecialityDocument);
			transactionalManagementService.addResource(new ObjectId(esSpecialityDocument.getId()), Resource.SPECIALITY,
					true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while adding  ES data speciality " + e.getMessage());
		}
	}

	@Override
	public void addEditSymptomDiseaseConditionDocument(ESSymptomDiseaseConditionDocument esSymptomDiseaseConditionDocument) {
		try {
			esSymptomDiseaseConditionRepository.save(esSymptomDiseaseConditionDocument);
			transactionalManagementService.addResource(new ObjectId(esSymptomDiseaseConditionDocument.getId()), Resource.SYMPTOM_DISEASE_CONDITION,
					true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while adding  ES data Symptom Disease Condition " + e.getMessage());
		}
	}

		
}
