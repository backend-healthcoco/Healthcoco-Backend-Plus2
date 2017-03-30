package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Records;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LabService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.LAB_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LAB_BASE_URL, description = "")
public class LabApi {

	private static Logger logger = Logger.getLogger(LabApi.class.getName());

	@Autowired
	private LabService labService;

	@Path(value = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT, notes = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT)
	public Response<List<Clinic>> getClinicWithReportCount(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId) || DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<List<Clinic>> response = new Response<List<Clinic>>();
		response.setDataList(labService.getClinicWithReportCount(doctorId, locationId, hospitalId));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR, notes = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	public Response<List<Records>> getReports(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "prescribedByDoctorId") String prescribedByDoctorId,
			@QueryParam(value = "prescribedByLocationId") String prescribedByLocationId,
			@QueryParam(value = "prescribedByHospitalId") String prescribedByHospitalId,
			@QueryParam(value = "size") int size, @QueryParam(value = "page") int page) {
		if (DPDoctorUtils.anyStringEmpty(doctorId) || DPDoctorUtils.anyStringEmpty(hospitalId)
				|| DPDoctorUtils.anyStringEmpty(hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<List<Records>> response = new Response<List<Records>>();
		response.setDataList(labService.getReports(doctorId, locationId, hospitalId, prescribedByDoctorId,
				prescribedByLocationId, prescribedByHospitalId, size, page));

		return response;
	}

}
