package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DataDynamicUI;
import com.dpdocter.beans.DentalLabDynamicField;
import com.dpdocter.beans.DentalLabDynamicUi;
import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DynamicUIRequest;
import com.dpdocter.response.DynamicUIResponse;
import com.dpdocter.services.DynamicUIService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DYNAMIC_UI_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DYNAMIC_UI_BASE_URL, description = "Endpoint for Dynamic UI")
public class DynamicUIApi {

	@Autowired
	DynamicUIService dynamicUIService;

	@Path(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	public Response<UIPermissions> getAllPermissionForDoctor(@PathParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		UIPermissions uiPermissions = dynamicUIService.getAllPermissionForDoctor(doctorId);
		Response<UIPermissions> response = new Response<UIPermissions>();
		response.setData(uiPermissions);
		return response;
	}

	@Path(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	public Response<DynamicUI> getPermissionForDoctor(@PathParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		DynamicUI dynamicUI = dynamicUIService.getPermissionForDoctor(doctorId);
		Response<DynamicUI> response = new Response<DynamicUI>();
		response.setData(dynamicUI);
		return response;
	}

	@Path(value = PathProxy.DynamicUIUrls.POST_PERMISSIONS)
	@POST
	@ApiOperation(value = "SUBMIT_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DYNAMIC_UI_PERMISSION")
	public Response<DynamicUI> postPermissions(DynamicUIRequest dynamicUIRequest) {
		if (dynamicUIRequest == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request is null");
		} else if (dynamicUIRequest.getUiPermissions() == null) {
			throw new BusinessException(ServiceError.InvalidInput, "UI permissions are null");
		}
		if (dynamicUIRequest.getDoctorId() == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}

		DynamicUI dynamicUI = dynamicUIService.postPermissions(dynamicUIRequest);
		Response<DynamicUI> response = new Response<DynamicUI>();
		response.setData(dynamicUI);
		return response;

	}

	@Path(value = PathProxy.DynamicUIUrls.POST_DATA_PERMISSIONS)
	@POST
	@ApiOperation(value = "SUBMIT_DATA_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DATA_DYNAMIC_UI_PERMISSION")
	public Response<DataDynamicUI> postDataPermissions(DataDynamicUI dynamicUIRequest) {
		if (dynamicUIRequest == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request is null");
		} else if (dynamicUIRequest.getDataDynamicField() == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Data permissions are null");
		}
		if (dynamicUIRequest.getDoctorId() == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}

		DataDynamicUI dynamicUI = dynamicUIService.postDataPermissions(dynamicUIRequest);
		Response<DataDynamicUI> response = new Response<DataDynamicUI>();
		response.setData(dynamicUI);
		return response;

	}
	
	@Path(value = PathProxy.DynamicUIUrls.GET_BOTH_PERMISSION_FOR_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_BOTH_PERMISSION_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_BOTH_PERMISSION_FOR_DOCTOR)
	public Response<DynamicUIResponse> getBothPermissionForDoctor(@PathParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		DynamicUIResponse dynamicUIResponse = new DynamicUIResponse();
		dynamicUIResponse = dynamicUIService.getBothPermissions(doctorId);
		Response<DynamicUIResponse> response = new Response<DynamicUIResponse>();
		response.setData(dynamicUIResponse);
		return response;
	}
	
	@Path(value = PathProxy.DynamicUIUrls.GET_DATA_PERMISSION_FOR_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_DATA_PERMISSION_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_DATA_PERMISSION_FOR_DOCTOR)
	public Response<DataDynamicUI> getDataPermissionForDoctor(@PathParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		DataDynamicUI dynamicUIResponse = new DataDynamicUI();
		dynamicUIResponse = dynamicUIService.getDynamicDataPermissionForDoctor(doctorId);
		Response<DataDynamicUI> response = new Response<DataDynamicUI>();
		response.setData(dynamicUIResponse);
		return response;
	}
	
	@Path(value = PathProxy.DynamicUIUrls.GET_ALL_DENTAL_LAB_PERMISSION_FOR_LAB)
	@GET
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_ALL_DENTAL_LAB_PERMISSION_FOR_LAB, notes = PathProxy.DynamicUIUrls.GET_ALL_DENTAL_LAB_PERMISSION_FOR_LAB)
	public Response<DentalLabDynamicField> getAllPermissionForDentalLab() {
		
		DentalLabDynamicField dentalLabDynamicField = dynamicUIService.getAllDentalLabPermissions();
		Response<DentalLabDynamicField> response = new Response<DentalLabDynamicField>();
		response.setData(dentalLabDynamicField);
		return response;
	}

	@Path(value = PathProxy.DynamicUIUrls.GET_DENTAL_LAB_PERMISSION_FOR_LAB)
	@GET
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_DENTAL_LAB_PERMISSION_FOR_LAB, notes = PathProxy.DynamicUIUrls.GET_DENTAL_LAB_PERMISSION_FOR_LAB)
	public Response<DentalLabDynamicUi> getPermissionForDentalLab(@PathParam("dentalLabId") String dentalLabId) {
		if (DPDoctorUtils.anyStringEmpty(dentalLabId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Dental Lab Id is null");
		}
		DentalLabDynamicUi dentalLabDynamicUi = dynamicUIService.getPermissionForDentalLab(dentalLabId);
		Response<DentalLabDynamicUi> response = new Response<DentalLabDynamicUi>();
		response.setData(dentalLabDynamicUi);
		return response;
	}

	@Path(value = PathProxy.DynamicUIUrls.POST_DENTAL_LAB_PERMISSIONS)
	@POST
	@ApiOperation(value = "SUBMIT_DENTAL_LAB_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DENTAL_LAB_DYNAMIC_UI_PERMISSION")
	public Response<DentalLabDynamicUi> postDentalLabPermissions(DentalLabDynamicUi dynamicUIRequest) {
		if (dynamicUIRequest == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request is null");
		} else if (dynamicUIRequest.getDentalLabDynamicField() == null) {
			throw new BusinessException(ServiceError.InvalidInput, "UI permissions are null");
		}
		if (dynamicUIRequest.getDentalLabId() == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Dental Lab Id is null");
		}

		DentalLabDynamicUi dentalLabDynamicUi = dynamicUIService.postDentalLabPermissions(dynamicUIRequest);
		Response<DentalLabDynamicUi> response = new Response<DentalLabDynamicUi>();
		response.setData(dentalLabDynamicUi);
		return response;

	}


}
