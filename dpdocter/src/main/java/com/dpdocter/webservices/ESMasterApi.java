package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Speciality;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.services.AdminServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_MASTER_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_MASTER_BASE_URL, description = "Endpoint for solr master")
public class ESMasterApi {

	private static Logger logger = LogManager.getLogger(ESMasterApi.class.getName());
	
    @Autowired
    ESMasterService esMasterService;

    @Autowired
    AdminServices adminServices;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private ESCityService esCityService;
 
    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_REFERENCE)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_REFERENCE, notes = PathProxy.SolrMasterUrls.SEARCH_REFERENCE)
    public Response<Reference> searchReference(@PathVariable("range") String range, @RequestParam("page") int page, @RequestParam("size") int size,
	    @RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
	    @RequestParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
	      @RequestParam(value = "discarded") Boolean discarded, @RequestParam(value = "searchTerm") String searchTerm) {

    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	
    Response<Reference> response = esMasterService.searchReference(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
    		searchTerm);
	return response;
    }

    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_DISEASE)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_DISEASE, notes = PathProxy.SolrMasterUrls.SEARCH_DISEASE)
    public Response<DiseaseListResponse> searchDisease(@PathVariable("range") String range, @RequestParam("page") long page, @RequestParam("size") int size,
	    @RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
	    @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
	    @RequestParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<DiseaseListResponse> searchResonse = esMasterService.searchDisease(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(searchResonse);
	return response;
    }

    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP, notes = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP)
    public Response<BloodGroup> searchBloodGroup() {

	List<BloodGroup> searchResonse = esMasterService.searchBloodGroup();
	Response<BloodGroup> response = new Response<BloodGroup>();
	response.setDataList(searchResonse);
	return response;
    }

    
    @GetMapping(value = "add")
    public Response<Boolean> add() {

    	List<CityCollection> cityCollections = cityRepository.findAll();
    	for(CityCollection cityCollection : cityCollections){
    		ESCityDocument esCityDocument = new ESCityDocument();
    		BeanUtil.map(cityCollection, esCityDocument);
    		esCityDocument.setGeoPoint(new GeoPoint(cityCollection.getLatitude(), cityCollection.getLongitude()));
    		esCityService.addCities(esCityDocument);
    	}
    	
		
    	adminServices.importDrug();
    Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }
    
    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSION)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSION, notes = PathProxy.SolrMasterUrls.SEARCH_PROFESSION)
    public Response<Profession> searchProfession(@RequestParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam("page") long page, @RequestParam("size") int size) {

	List<Profession> searchResonse = esMasterService.searchProfession(searchTerm, updatedTime, page, size);
	Response<Profession> response = new Response<Profession>();
	response.setDataList(searchResonse);
	return response;
    }

    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP, notes = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP)
    public Response<ProfessionalMembership> searchProfessionalMembership(@RequestParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam("page") long page, @RequestParam("size") int size) {

	List<ProfessionalMembership> searchResonse = esMasterService.searchProfessionalMembership(searchTerm, updatedTime, page, size);
	Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
	response.setDataList(searchResonse);
	return response;
    }

   
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE, notes = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE)
    public Response<EducationInstitute> searchEducationInstitute(@RequestParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam("page") long page, @RequestParam("size") int size) {

	List<EducationInstitute> searchResonse = esMasterService.searchEducationInstitute(searchTerm, updatedTime, page, size);
	Response<EducationInstitute> response = new Response<EducationInstitute>();
	response.setDataList(searchResonse);
	return response;
    }

    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION, notes = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION)
    public Response<EducationQualification> searchEducationQualification(@RequestParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam("page") long page, @RequestParam("size") int size) {

	List<EducationQualification> searchResonse = esMasterService.searchEducationQualification(searchTerm, updatedTime, page, size);
	Response<EducationQualification> response = new Response<EducationQualification>();
	response.setDataList(searchResonse);
	return response;
    }

    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL, notes = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL)
    public Response<MedicalCouncil> searchMedicalCouncil(@RequestParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam("page") long page, @RequestParam("size") int size) {

	List<MedicalCouncil> searchResonse = esMasterService.searchMedicalCouncil(searchTerm, updatedTime, page, size);
	Response<MedicalCouncil> response = new Response<MedicalCouncil>();
	response.setDataList(searchResonse);
	return response;
    }

    
    @GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY)
    @ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY, notes = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY)
    public Response<Speciality> searchSpeciality(@RequestParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime, @RequestParam("page") long page, @RequestParam("size") int size) {

	List<Speciality> searchResonse = esMasterService.searchSpeciality(searchTerm, updatedTime, page, size);
	Response<Speciality> response = new Response<Speciality>();
	response.setDataList(searchResonse);
	return response;
    }
}
