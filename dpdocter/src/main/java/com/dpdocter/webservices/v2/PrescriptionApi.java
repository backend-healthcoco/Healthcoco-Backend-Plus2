package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;

@Component(value = "PrescriptionApiV2")
@Path(PathProxy.PRESCRIPTION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PRESCRIPTION_BASE_URL, description = "Endpoint for prescription")
public class PrescriptionApi {

	/*private static Logger logger = Logger.getLogger(PrescriptionApi.class.getName());

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
	
	@GET
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTIONS_FOR_EMR, notes = PathProxy.PrescriptionUrls.GET_PRESCRIPTIONS_FOR_EMR)
	@Path(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTIONS_FOR_EMR)
	public Response<Prescription> getPrescriptionForEMR(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		List<Prescription> prescriptions = null;

		prescriptions = prescriptionServices.getPrescriptionsForEMR(page, size, doctorId, hospitalId, locationId, patientId, updatedTime, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), discarded, false);

		Response<Prescription> response = new Response<Prescription>();
		response.setDataList(prescriptions);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.SEARCH_DRUGS)
	@GET
	@ApiOperation(value = PathProxy.PrescriptionUrls.SEARCH_DRUGS, notes = PathProxy.PrescriptionUrls.SEARCH_DRUGS)
	public Response<Object> searchDrug(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<Drug> drugDocuments = prescriptionServices.getCustomGlobalDrugs(page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<Object> response = new Response<Object>();
		response.setData(prescriptionServices.countCustomGlobalDrugs(doctorId, locationId, hospitalId, updatedTime,
				discarded, searchTerm));
		response.setDataList(drugDocuments);
		return response;
	}
	
	@Path(value = PathProxy.PrescriptionUrls.GET_DRUGS_BY_CODE)
	@GET
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_DRUGS_BY_CODE, notes = PathProxy.PrescriptionUrls.GET_DRUGS_BY_CODE)
	public Response<Drug> getDrugDetails(@PathParam("drugCode") String drugCode) {
		if (drugCode == null) {
			logger.error("DrugId Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "DrugId Is NULL");
		}
		Drug drugAddEditResponse = prescriptionServices.getDrugByDrugCode(drugCode);
		Response<Drug> response = new Response<Drug>();
		response.setData(drugAddEditResponse);
		return response;
	}
	*/
}
