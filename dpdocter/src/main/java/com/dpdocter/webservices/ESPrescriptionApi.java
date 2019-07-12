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
import org.springframework.stereotype.Component;

import com.dpdocter.beans.LabTest;
import com.dpdocter.elasticsearch.document.ESAdvicesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_PRESCRIPTION_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_PRESCRIPTION_BASEURL, description = "Endpoint for solr prescription")
public class ESPrescriptionApi {

	private static Logger logger = Logger.getLogger(ESPrescriptionApi.class.getName());

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	@GET
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	public Response<Object> searchDrug(@PathParam("range") String range, @QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm, @QueryParam(value = "category") String category,
			@DefaultValue("false")  @QueryParam(value = "searchByGenericName") Boolean searchByGenericName) {

		if (DPDoctorUtils.anyStringEmpty(range)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<?> drugDocuments = esPrescriptionService.searchDrug(range, page, size, doctorId, locationId, hospitalId,
				updatedTime, discarded, searchTerm, category, searchByGenericName);
		Response<Object> response = new Response<Object>();
		response.setDataList(drugDocuments);
		return response;
	}

	@Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
	@GET
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
	public Response<LabTest> searchLabTest(@PathParam("range") String range, @QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<LabTest> labTests = esPrescriptionService.searchLabTest(range, page, size, locationId, hospitalId,
				updatedTime, discarded, searchTerm);
		Response<LabTest> response = new Response<LabTest>();
		response.setDataList(labTests);
		return response;
	}

	@Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
	@GET
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
	public Response<Object> searchDiagnosticTest(@PathParam("range") String range,
			@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESDiagnosticTestDocument> diagnosticTests = esPrescriptionService.searchDiagnosticTest(range, page, size,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<Object> response = new Response<Object>();
		response.setDataList(diagnosticTests);
		response.setData(esPrescriptionService.getDiagnosticTestCount(range, page, size, locationId, hospitalId, updatedTime, discarded, searchTerm));
		return response;
	}

	@Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_ADVICE)
	@GET
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_ADVICE, notes = PathProxy.SolrPrescriptionUrls.SEARCH_ADVICE)
	public Response<ESAdvicesDocument> searchAdvices(@PathParam("range") String range, @QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "disease") String disease, @QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESAdvicesDocument> advices = esPrescriptionService.searchAdvices(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, disease, searchTerm);
		Response<ESAdvicesDocument> response = new Response<ESAdvicesDocument>();
		response.setDataList(advices);
		return response;
	}

}
