package com.dpdocter.webservices.v2;

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

import com.dpdocter.beans.v2.Drug;
import com.dpdocter.beans.v2.Prescription;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.PrescriptionServices;
import com.dpdocter.webservices.v2.PathProxy;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "PrescriptionApiV2")
@Path(PathProxy.PRESCRIPTION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PRESCRIPTION_BASE_URL, description = "Endpoint for prescription")
public class PrescriptionApi {

	private static Logger logger = Logger.getLogger(PrescriptionApi.class.getName());
	
	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private OTPService otpService;

	@GET
	@ApiOperation(value = "GET_PRESCRIPTIONS", notes = "GET_PRESCRIPTIONS")
	public Response<Prescription> getPrescription(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		List<Prescription> prescriptions = null;

		prescriptions = prescriptionServices.getPrescriptions(page, size, doctorId, hospitalId, locationId, patientId,
				updatedTime, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), discarded,
				false);

		Response<Prescription> response = new Response<Prescription>();
		response.setDataList(prescriptions);
		return response;
	}
	
	@Path(value = PathProxy.PrescriptionUrls.SEARCH_DRUGS)
	@GET
	@ApiOperation(value = PathProxy.PrescriptionUrls.SEARCH_DRUGS, notes = PathProxy.PrescriptionUrls.SEARCH_DRUGS)
	public Response<Drug> searchDrug( @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {

		if (DPDoctorUtils.anyStringEmpty(doctorId,locationId,hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<Drug> drugDocuments = prescriptionServices.getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<Drug> response = new Response<Drug>();
		response.setDataList(drugDocuments);
		return response;
	}
}
