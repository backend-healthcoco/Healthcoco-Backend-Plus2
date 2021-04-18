package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.PrintSettings;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.PrintSettingsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.PRINT_SETTINGS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.PRINT_SETTINGS_BASE_URL, description = "Endpoint for print settings")
public class PrintSettingsApi {

	private static Logger logger = LogManager.getLogger(PrintSettingsApi.class.getName());

	@Autowired
	private PrintSettingsService printSettingsService;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@PostMapping(value = PathProxy.PrintSettingsUrls.SAVE_PRINT_SETTINGS)
	@ApiOperation(value = "SAVE_PRINT_SETTINGS", notes = "SAVE_PRINT_SETTINGS")
	public Response<PrintSettings> saveSettings(PrintSettings request,
			@RequestParam("printSettingType") String printSettingType) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (printSettingType == null)
			printSettingType = PrintSettingType.DEFAULT.getType();
		PrintSettings printSettings = printSettingsService.saveSettings(request, printSettingType);
		if (printSettings != null)
			printSettings.setClinicLogoUrl(getFinalImageURL(printSettings.getClinicLogoUrl()));
		Response<PrintSettings> response = new Response<PrintSettings>();
		response.setData(printSettings);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTINGS)
	@ApiOperation(value = "GET_PRINT_SETTINGS", notes = "GET_PRINT_SETTINGS")
	public Response<PrintSettings> getSettings(@PathVariable(value = "printFilter") String printFilter,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			  @RequestParam(value = "isWeb") Boolean isWeb) {

		if (DPDoctorUtils.anyStringEmpty(printFilter, locationId, hospitalId)) {
			logger.warn("PrintFilter, DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"PrintFilter, DoctorId or locationId or hospitalId cannot be null");
		}

		List<PrintSettings> printSettings = printSettingsService.getSettings(printFilter, doctorId, locationId,
				hospitalId, page, size, updatedTime, discarded);
		Response<PrintSettings> response = new Response<PrintSettings>();
		if (printSettings != null) {
			for (Object pSettings : printSettings) {
				((PrintSettings) pSettings)
						.setClinicLogoUrl(getFinalImageURL(((PrintSettings) pSettings).getClinicLogoUrl()));
			}
			response.setDataList(printSettings);
		}
		if (!isWeb && (printSettings == null || printSettings.isEmpty())) {
			printSettings = new ArrayList<PrintSettings>();
			printSettings.add(new PrintSettings());
			response.setDataList(printSettings);
		}

		return response;
	}

	
	@GetMapping(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTING_BY_TYPE)
	@ApiOperation(value = "GET_PRINT_SETTING_BY_TYPE", notes = "GET_PRINT_SETTING_BY_TYPE")
	public Response<PrintSettings> getSettingByType(@PathVariable(value = "printFilter") String printFilter,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable("printSettingType") String printSettingType,
			@DefaultValue("false") @RequestParam(value = "discarded") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(printFilter, locationId, hospitalId)) {
			logger.warn("PrintFilter, DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"PrintFilter, DoctorId or locationId or hospitalId cannot be null");
		}

		PrintSettings printSetting = printSettingsService.getSettingByType(printFilter, doctorId, locationId, hospitalId, discarded, printSettingType);
		if (printSetting != null) {
			printSetting.setClinicLogoUrl(getFinalImageURL(printSetting.getClinicLogoUrl()));
		}
		Response<PrintSettings> response = new Response<PrintSettings>();
		response.setData(printSetting);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTING_TYPE)
	@ApiOperation(value = "GET_PRINT_SETTING_TYPE", notes = "GET_PRINT_SETTING_TYPE")
	public Response<Boolean> putSettingByType() {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(printSettingsService.putSettingByType());
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrintSettingsUrls.GET_LAB_PRINT_SETTING)
	@ApiOperation(value = "GET_LAB_PRINT_SETTING", notes = "GET_LAB_PRINT_SETTING")
	public Response<PrintSettings> getSettings(@PathVariable(value = "printFilter") String printFilter,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(printFilter, locationId, hospitalId)) {
			logger.warn("PrintFilter, DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"PrintFilter, DoctorId or locationId or hospitalId cannot be null");
		}

		List<PrintSettings> printSettings = printSettingsService.getSettings(printFilter, null, locationId, hospitalId,
				page, size, updatedTime, discarded);
		if (printSettings != null) {
			for (Object pSettings : printSettings) {
				((PrintSettings) pSettings)
						.setClinicLogoUrl(getFinalImageURL(((PrintSettings) pSettings).getClinicLogoUrl()));
			}
		}
		Response<PrintSettings> response = new Response<PrintSettings>();
		response.setDataList(printSettings);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrintSettingsUrls.DELETE_PRINT_SETTINGS)
	@ApiOperation(value = "DELETE_PRINT_SETTINGS", notes = "DELETE_PRINT_SETTINGS")
	public Response<PrintSettings> deletePrintSettings(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(id, doctorId, locationId, hospitalId)) {
			logger.warn("Id, DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					"Id, DoctorId or locationId or hospitalId cannot be null");
		}
		PrintSettings printSettings = printSettingsService.deletePrintSettings(id, doctorId, locationId, hospitalId,
				discarded);
		Response<PrintSettings> response = new Response<PrintSettings>();
		response.setData(printSettings);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	
	@GetMapping(value = PathProxy.PrintSettingsUrls.GET_GENERAL_NOTES)
	@ApiOperation(value = "GET_GENERAL_NOTES", notes = "GET_GENERAL_NOTES")
	public Response<String> getSettingsGeneralNote(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId) {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn(" DoctorId or locationId or hospitalId cannot be null");
			throw new BusinessException(ServiceError.InvalidInput,
					" DoctorId or locationId or hospitalId cannot be null");
		}
		String generalNote = printSettingsService.getPrintSettingsGeneralNote(doctorId, locationId, hospitalId);

		Response<String> response = new Response<String>();
		response.setData(generalNote);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrintSettingsUrls.UPLOAD_FILE)
	@ApiOperation(value = PathProxy.PrintSettingsUrls.UPLOAD_FILE, notes = PathProxy.PrintSettingsUrls.UPLOAD_FILE)
	public Response<String> upladFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
		if (DPDoctorUtils.anyStringEmpty(type)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (file == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		String fileResponse = printSettingsService.uploadFile(file, type);
		response.setData(fileResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrintSettingsUrls.UPLOAD_SIGNATURE)
	@ApiOperation(value = PathProxy.PrintSettingsUrls.UPLOAD_SIGNATURE, notes = PathProxy.PrintSettingsUrls.UPLOAD_SIGNATURE)
	public Response<String> upladSignature(@RequestParam("file") MultipartFile file) {
		if (file == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		String fileResponse = printSettingsService.uploadSignature(file);
		response.setData(fileResponse);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrintSettingsUrls.BLANK_PRINT)
	@ApiOperation(value = PathProxy.PrintSettingsUrls.BLANK_PRINT, notes = PathProxy.PrintSettingsUrls.BLANK_PRINT)
	public Response<String> createBlankPrint(@PathVariable("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(printSettingsService.createBlankPrint(patientId, locationId, hospitalId, doctorId));
		return response;
	}
}
