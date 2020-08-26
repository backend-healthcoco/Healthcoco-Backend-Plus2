package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.PrintSettings;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.PrintSettingsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.PRINT_SETTINGS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PRINT_SETTINGS_BASE_URL, description = "Endpoint for print settings")
public class PrintSettingsApi {

	private static Logger logger = Logger.getLogger(PrintSettingsApi.class.getName());

	@Autowired
	private PrintSettingsService printSettingsService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.PrintSettingsUrls.SAVE_PRINT_SETTINGS)
	@POST
	@ApiOperation(value = "SAVE_PRINT_SETTINGS", notes = "SAVE_PRINT_SETTINGS")
	public Response<PrintSettings> saveSettings(PrintSettings request,
			@QueryParam("printSettingType") String printSettingType) {

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

	@Path(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTINGS)
	@GET
	@ApiOperation(value = "GET_PRINT_SETTINGS", notes = "GET_PRINT_SETTINGS")
	public Response<PrintSettings> getSettings(@PathParam(value = "printFilter") String printFilter,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@DefaultValue("true") @QueryParam(value = "isWeb") Boolean isWeb) {

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

	@Path(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTING_BY_TYPE)
	@GET
	@ApiOperation(value = "GET_PRINT_SETTING_BY_TYPE", notes = "GET_PRINT_SETTING_BY_TYPE")
	public Response<PrintSettings> getSettingByType(@PathParam(value = "printFilter") String printFilter,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@PathParam("printSettingType") String printSettingType,
			@DefaultValue("false") @QueryParam(value = "discarded") Boolean discarded) {

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

	@Path(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTING_TYPE)
	@GET
	@ApiOperation(value = "GET_PRINT_SETTING_TYPE", notes = "GET_PRINT_SETTING_TYPE")
	public Response<Boolean> putSettingByType() {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(printSettingsService.putSettingByType());
		return response;
	}
	
	@Path(value = PathProxy.PrintSettingsUrls.GET_LAB_PRINT_SETTING)
	@GET
	@ApiOperation(value = "GET_LAB_PRINT_SETTING", notes = "GET_LAB_PRINT_SETTING")
	public Response<PrintSettings> getSettings(@PathParam(value = "printFilter") String printFilter,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "page") int page, @QueryParam(value = "size") int size,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

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

	@Path(value = PathProxy.PrintSettingsUrls.DELETE_PRINT_SETTINGS)
	@DELETE
	@ApiOperation(value = "DELETE_PRINT_SETTINGS", notes = "DELETE_PRINT_SETTINGS")
	public Response<PrintSettings> deletePrintSettings(@PathParam(value = "id") String id,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

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

	@Path(value = PathProxy.PrintSettingsUrls.GET_GENERAL_NOTES)
	@GET
	@ApiOperation(value = "GET_GENERAL_NOTES", notes = "GET_GENERAL_NOTES")
	public Response<String> getSettingsGeneralNote(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {

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

	@Path(value = PathProxy.PrintSettingsUrls.UPLOAD_FILE)
	@POST
	@ApiOperation(value = PathProxy.PrintSettingsUrls.UPLOAD_FILE, notes = PathProxy.PrintSettingsUrls.UPLOAD_FILE)
	public Response<String> upladFile(FileDetails fileDetails, @QueryParam("type") String type) {
		if (DPDoctorUtils.anyStringEmpty(type)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(fileDetails.getFileEncoded())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		String file = printSettingsService.uploadFile(fileDetails, type);
		response.setData(file);
		return response;
	}

	@Path(value = PathProxy.PrintSettingsUrls.UPLOAD_SIGNATURE)
	@POST
	@ApiOperation(value = PathProxy.PrintSettingsUrls.UPLOAD_SIGNATURE, notes = PathProxy.PrintSettingsUrls.UPLOAD_SIGNATURE)
	public Response<String> upladSignature(FileDetails fileDetails) {
		if (DPDoctorUtils.anyStringEmpty(fileDetails.getFileEncoded())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		String file = printSettingsService.uploadSignature(fileDetails);
		response.setData(file);
		return response;
	}
}
