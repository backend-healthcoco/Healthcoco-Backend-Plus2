package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DataDynamicUI;
import com.dpdocter.beans.DentalLabDynamicField;
import com.dpdocter.beans.DentalLabDynamicUi;
import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.KioskDynamicUi;
import com.dpdocter.beans.NutritionUI;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DynamicUIRequest;
import com.dpdocter.request.KioskDynamicUiResquest;
import com.dpdocter.request.NutrirtionUIRequest;
import com.dpdocter.response.DynamicUIResponse;
import com.dpdocter.services.DynamicUIService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.DYNAMIC_UI_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DYNAMIC_UI_BASE_URL, description = "Endpoint for Dynamic UI")
public class DynamicUIApi {

	@Autowired
	DynamicUIService dynamicUIService;

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	public Response<UIPermissions> getAllPermissionForDoctor(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		UIPermissions uiPermissions = dynamicUIService.getAllPermissionForDoctor(doctorId);
		Response<UIPermissions> response = new Response<UIPermissions>();
		response.setData(uiPermissions);
		return response;
	}

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	public Response<DynamicUI> getPermissionForDoctor(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		DynamicUI dynamicUI = dynamicUIService.getPermissionForDoctor(doctorId);
		Response<DynamicUI> response = new Response<DynamicUI>();
		response.setData(dynamicUI);
		return response;
	}

	
	@PostMapping(value = PathProxy.DynamicUIUrls.POST_PERMISSIONS)
	@ApiOperation(value = "SUBMIT_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DYNAMIC_UI_PERMISSION")
	public Response<DynamicUI> postPermissions(@RequestBody DynamicUIRequest dynamicUIRequest) {
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

	
	@PostMapping(value = PathProxy.DynamicUIUrls.POST_DATA_PERMISSIONS)
	@ApiOperation(value = "SUBMIT_DATA_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DATA_DYNAMIC_UI_PERMISSION")
	public Response<DataDynamicUI> postDataPermissions(@RequestBody DataDynamicUI dynamicUIRequest) {
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

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_BOTH_PERMISSION_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_BOTH_PERMISSION_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_BOTH_PERMISSION_FOR_DOCTOR)
	public Response<DynamicUIResponse> getBothPermissionForDoctor(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		DynamicUIResponse dynamicUIResponse = new DynamicUIResponse();
		dynamicUIResponse = dynamicUIService.getBothPermissions(doctorId);
		Response<DynamicUIResponse> response = new Response<DynamicUIResponse>();
		response.setData(dynamicUIResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_DATA_PERMISSION_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_DATA_PERMISSION_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_DATA_PERMISSION_FOR_DOCTOR)
	public Response<DataDynamicUI> getDataPermissionForDoctor(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		DataDynamicUI dynamicUIResponse = new DataDynamicUI();
		dynamicUIResponse = dynamicUIService.getDynamicDataPermissionForDoctor(doctorId);
		Response<DataDynamicUI> response = new Response<DataDynamicUI>();
		response.setData(dynamicUIResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_ALL_DENTAL_LAB_PERMISSION_FOR_LAB)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_ALL_DENTAL_LAB_PERMISSION_FOR_LAB, notes = PathProxy.DynamicUIUrls.GET_ALL_DENTAL_LAB_PERMISSION_FOR_LAB)
	public Response<DentalLabDynamicField> getAllPermissionForDentalLab() {

		DentalLabDynamicField dentalLabDynamicField = dynamicUIService.getAllDentalLabPermissions();
		Response<DentalLabDynamicField> response = new Response<DentalLabDynamicField>();
		response.setData(dentalLabDynamicField);
		return response;
	}

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_DENTAL_LAB_PERMISSION_FOR_LAB)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_DENTAL_LAB_PERMISSION_FOR_LAB, notes = PathProxy.DynamicUIUrls.GET_DENTAL_LAB_PERMISSION_FOR_LAB)
	public Response<DentalLabDynamicUi> getPermissionForDentalLab(@PathVariable("dentalLabId") String dentalLabId) {
		if (DPDoctorUtils.anyStringEmpty(dentalLabId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Dental Lab Id is null");
		}
		DentalLabDynamicUi dentalLabDynamicUi = dynamicUIService.getPermissionForDentalLab(dentalLabId);
		Response<DentalLabDynamicUi> response = new Response<DentalLabDynamicUi>();
		response.setData(dentalLabDynamicUi);
		return response;
	}

	
	@PostMapping(value = PathProxy.DynamicUIUrls.POST_DENTAL_LAB_PERMISSIONS)
	@ApiOperation(value = "SUBMIT_DENTAL_LAB_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DENTAL_LAB_DYNAMIC_UI_PERMISSION")
	public Response<DentalLabDynamicUi> postDentalLabPermissions(@RequestBody DentalLabDynamicUi dynamicUIRequest) {
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

	
	@PostMapping(value = PathProxy.DynamicUIUrls.ADD_EDIT_KIOSK_PERMISSION)
	@ApiOperation(value = PathProxy.DynamicUIUrls.ADD_EDIT_KIOSK_PERMISSION, notes = "ADD_EDIT_KIOSK_PERMISSION")
	public Response<KioskDynamicUi> addEditKoiskPermissions(@RequestBody KioskDynamicUiResquest dynamicUIRequest) {
		if (dynamicUIRequest == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request is null");
		} else if (dynamicUIRequest.getKioskPermission() == null) {
			throw new BusinessException(ServiceError.InvalidInput, " KIOSK UI permissions are null");
		}
		if (DPDoctorUtils.anyStringEmpty(dynamicUIRequest.getDoctorId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null or Empty");
		}

		KioskDynamicUi koiskDynamicUi = dynamicUIService.addEditKioskUiPermission(dynamicUIRequest);
		Response<KioskDynamicUi> response = new Response<KioskDynamicUi>();
		response.setData(koiskDynamicUi);
		return response;

	}

	
	@PostMapping(value = PathProxy.DynamicUIUrls.GET_KIOSK_PERMISSION)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_KIOSK_PERMISSION, notes = "GET_KIOSK_PERMISSION")
	public Response<KioskDynamicUi> getKoiskPermissions(@PathVariable("doctorId") String doctorId) {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null or Empty");
		}

		KioskDynamicUi koiskDynamicUi = dynamicUIService.getKioskUiPermission(doctorId);
		Response<KioskDynamicUi> response = new Response<KioskDynamicUi>();
		response.setData(koiskDynamicUi);
		return response;

	}

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_ALL_NUTRITION_PERMISSION)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_ALL_NUTRITION_PERMISSION, notes = "GET_ALL_NUTRITION_PERMISSION")
	public Response<NutritionUI> getAllNutritionPermissions() {

		NutritionUI nutritionUI = dynamicUIService.getAllNutritionUIPermission();
		Response<NutritionUI> response = new Response<NutritionUI>();
		response.setData(nutritionUI);
		return response;

	}

	
	@GetMapping(value = PathProxy.DynamicUIUrls.GET_NUTRITION_PERMISSION)
	@ApiOperation(value = PathProxy.DynamicUIUrls.GET_NUTRITION_PERMISSION, notes = "GET_NUTRITION_PERMISSION")
	public Response<NutritionUI> GET_NUTRITION_PERMISSION(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null or Empty");
		}
		NutritionUI nutritionUI = dynamicUIService.getNutritionUIPermission(doctorId);
		Response<NutritionUI> response = new Response<NutritionUI>();
		response.setData(nutritionUI);
		return response;

	}

	
	@PostMapping(value = PathProxy.DynamicUIUrls.ADD_EDIT_NUTRITION_PERMISSION)
	@ApiOperation(value = PathProxy.DynamicUIUrls.ADD_EDIT_NUTRITION_PERMISSION, notes = "ADD_EDIT_NUTRITION_PERMISSION")
	public Response<NutritionUI> addEditNutritionPermissions(@RequestBody NutrirtionUIRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request is null");
		} else if (request.getUiPermission() == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Nutrition UI permissions are null");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId(), request.getAdminId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id  and Admin Id,is null or Empty");
		}

		NutritionUI nutritionUI = dynamicUIService.addEditNutritionUIPermission(request);
		Response<NutritionUI> response = new Response<NutritionUI>();
		response.setData(nutritionUI);
		return response;

	}

}
