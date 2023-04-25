package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

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

@Component
@Path(PathProxy.SOLR_MASTER_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_MASTER_BASE_URL, description = "Endpoint for solr master")
public class ESMasterApi {

	private static Logger logger = Logger.getLogger(ESMasterApi.class.getName());

	@Autowired
	ESMasterService esMasterService;

	@Autowired
	AdminServices adminServices;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private ESCityService esCityService;

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_REFERENCE)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_REFERENCE, notes = PathProxy.SolrMasterUrls.SEARCH_REFERENCE)
	public Response<Reference> searchReference(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {

		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Reference> response = esMasterService.searchReference(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_DISEASE)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_DISEASE, notes = PathProxy.SolrMasterUrls.SEARCH_DISEASE)
	public Response<DiseaseListResponse> searchDisease(@PathParam("range") String range, @QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<DiseaseListResponse> searchResonse = esMasterService.searchDisease(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP, notes = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP)
	public Response<BloodGroup> searchBloodGroup() {

		List<BloodGroup> searchResonse = esMasterService.searchBloodGroup();
		Response<BloodGroup> response = new Response<BloodGroup>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = "add")
	@GET
	public Response<Boolean> add() {

		List<CityCollection> cityCollections = cityRepository.findAll();
		for (CityCollection cityCollection : cityCollections) {
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

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSION)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSION, notes = PathProxy.SolrMasterUrls.SEARCH_PROFESSION)
	public Response<Profession> searchProfession(@QueryParam(value = "searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") long page,
			@QueryParam("size") int size) {

		List<Profession> searchResonse = esMasterService.searchProfession(searchTerm, updatedTime, page, size);
		Response<Profession> response = new Response<Profession>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP, notes = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP)
	public Response<ProfessionalMembership> searchProfessionalMembership(
			@QueryParam(value = "searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") long page,
			@QueryParam("size") int size) {

		List<ProfessionalMembership> searchResonse = esMasterService.searchProfessionalMembership(searchTerm,
				updatedTime, page, size);
		Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE, notes = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE)
	public Response<EducationInstitute> searchEducationInstitute(@QueryParam(value = "searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") long page,
			@QueryParam("size") int size) {

		List<EducationInstitute> searchResonse = esMasterService.searchEducationInstitute(searchTerm, updatedTime, page,
				size);
		Response<EducationInstitute> response = new Response<EducationInstitute>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION, notes = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION)
	public Response<EducationQualification> searchEducationQualification(
			@QueryParam(value = "searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") long page,
			@QueryParam("size") int size) {

		List<EducationQualification> searchResonse = esMasterService.searchEducationQualification(searchTerm,
				updatedTime, page, size);
		Response<EducationQualification> response = new Response<EducationQualification>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL, notes = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL)
	public Response<MedicalCouncil> searchMedicalCouncil(@QueryParam(value = "searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") long page,
			@QueryParam("size") int size) {

		List<MedicalCouncil> searchResonse = esMasterService.searchMedicalCouncil(searchTerm, updatedTime, page, size);
		Response<MedicalCouncil> response = new Response<MedicalCouncil>();
		response.setDataList(searchResonse);
		return response;
	}

	@Path(value = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY)
	@GET
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY, notes = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY)
	public Response<Speciality> searchSpeciality(@QueryParam(value = "searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") long page,
			@QueryParam("size") int size) {

		List<Speciality> searchResonse = esMasterService.searchSpeciality(searchTerm, updatedTime, page, size);
		Response<Speciality> response = new Response<Speciality>();
		response.setDataList(searchResonse);
		return response;
	}
}
