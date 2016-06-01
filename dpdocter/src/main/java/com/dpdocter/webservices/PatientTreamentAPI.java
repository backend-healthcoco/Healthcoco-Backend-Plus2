package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.ProductAndService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.services.PatientTreatmentServices;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.PATIENT_TREATMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_TREATMENT_BASE_URL, description = "Endpoint for patient treatment")
public class PatientTreamentAPI {
    @Autowired
    private PatientTreatmentServices patientTreatmentServices;

    @Path(PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE)
    @POST
    @ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE)
    public Response<Boolean> addEditProductService(ProductAndService productAndService) {
	if (productAndService == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be Empty");
	}
	boolean addEditProductServiceResponse = patientTreatmentServices.addEditProductService(productAndService);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProductServiceResponse);
	return response;
    }

    @Path(PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE_COST)
    @POST
    @ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE_COST, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE_COST)
    public Response<Boolean> addEditProductServiceCost(ProductAndService productAndService) {
	if (productAndService == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be Empty");
	} else if (DPDoctorUtils.anyStringEmpty(productAndService.getLocationId(), productAndService.getHospitalId(), productAndService.getDoctorId())) {
	    throw new BusinessException(ServiceError.InvalidInput, "LocationId, HospitalId and DoctorId cannot be empty");
	}
	boolean addEditProductServiceResponse = patientTreatmentServices.addEditProductServiceCost(productAndService);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProductServiceResponse);
	return response;
    }

    @Path(PathProxy.PatientTreatmentURLs.GET_PRODUCTS_AND_SERVICES)
    @GET
    @ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_PRODUCTS_AND_SERVICES, notes = PathProxy.PatientTreatmentURLs.GET_PRODUCTS_AND_SERVICES)
    public Response<ProductAndService> getProductsAndServices(@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
	    @QueryParam("doctorId") String doctorId) {
	if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "LocationId, HospitalId and DoctorId cannot be empty");
	}
	List<ProductAndService> getProductsAndServicesResponse = patientTreatmentServices.getProductsAndServices(locationId, hospitalId, doctorId);

	Response<ProductAndService> response = new Response<ProductAndService>();
	response.setDataList(getProductsAndServicesResponse);
	return response;
    }

    @Path(PathProxy.PatientTreatmentURLs.ADD_EDIT_PATIENT_TREATMENT)
    @POST
    @ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_PATIENT_TREATMENT, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_PATIENT_TREATMENT)
    public Response<PatientTreatmentResponse> addEditPatientTreatment(@QueryParam("treatmentId") String treatmentId,
	    @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
	    List<PatientTreatment> patientTreatments) {
	if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "LocationId, HospitalId and DoctorId cannot be empty");
	}
	if (patientTreatments == null || patientTreatments.isEmpty()) {
	    throw new BusinessException(ServiceError.InvalidInput, "Patient Treament request cannot be empty");
	}
	PatientTreatmentResponse addEditPatientTreatmentResponse = patientTreatmentServices.addEditPatientTreatment(treatmentId, locationId, hospitalId,
		doctorId, patientTreatments);

	Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
	response.setData(addEditPatientTreatmentResponse);
	return response;
    }

    @Path(PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT)
    @DELETE
    @ApiOperation(value = PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT, notes = PathProxy.PatientTreatmentURLs.DELETE_PATIENT_TREATMENT)
    public Response<Boolean> deletePatientTreatment(@QueryParam("treatmentId") String treatmentId, @QueryParam("locationId") String locationId,
	    @QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId) {
	if (DPDoctorUtils.anyStringEmpty(treatmentId, locationId, hospitalId, doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "TreatmentId, LocationId, HospitalId and DoctorId cannot be empty");
	}

	boolean deletePatientTreatmentResponse = patientTreatmentServices.deletePatientTreatment(treatmentId, locationId, hospitalId, doctorId);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(deletePatientTreatmentResponse);
	return response;
    }

    @Path(PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_BY_ID)
    @GET
    @ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_BY_ID, notes = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENT_BY_ID)
    public Response<PatientTreatmentResponse> getPatientTreatmentById(@PathParam("treatmentId") String treatmentId) {
	if (DPDoctorUtils.anyStringEmpty(treatmentId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "TreatmentId cannot be empty");
	}

	PatientTreatmentResponse getPatientTreatmentByIdResponse = patientTreatmentServices.getPatientTreatmentById(treatmentId);

	Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
	response.setData(getPatientTreatmentByIdResponse);
	return response;
    }

    @Path(PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENTS)
    @GET
    @ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENTS, notes = PathProxy.PatientTreatmentURLs.GET_PATIENT_TREATMENTS)
    public Response<PatientTreatmentResponse> getPatientTreatments(@QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
	    @QueryParam("patientId") String patientId, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {
	List<PatientTreatmentResponse> getPatientTreatmentsResponse;

	getPatientTreatmentsResponse = patientTreatmentServices.getPatientTreatments(locationId, hospitalId, doctorId, patientId, page, size, updatedTime,
		discarded);

	Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
	response.setDataList(getPatientTreatmentsResponse);
	return response;
    }
}
