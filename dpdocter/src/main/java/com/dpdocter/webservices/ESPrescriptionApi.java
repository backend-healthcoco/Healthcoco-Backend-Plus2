package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
(PathProxy.SOLR_PRESCRIPTION_BASEURL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_PRESCRIPTION_BASEURL, description = "Endpoint for solr prescription")
public class ESPrescriptionApi {

	private static Logger logger = LogManager.getLogger(ESPrescriptionApi.class.getName());

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	
	@GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	public Response<Object> searchDrug(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm, @RequestParam(value = "category") String category,
			@DefaultValue("false")  @RequestParam(value = "searchByGenericName") Boolean searchByGenericName) {

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

	
	@GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
	public Response<LabTest> searchLabTest(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
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

	
	@GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
	public Response<Object> searchDiagnosticTest(@PathVariable("range") String range,
			@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
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

	
	@GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_ADVICE)
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_ADVICE, notes = PathProxy.SolrPrescriptionUrls.SEARCH_ADVICE)
	public Response<ESAdvicesDocument> searchAdvices(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "disease") String disease, @RequestParam(value = "searchTerm") String searchTerm) {
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
