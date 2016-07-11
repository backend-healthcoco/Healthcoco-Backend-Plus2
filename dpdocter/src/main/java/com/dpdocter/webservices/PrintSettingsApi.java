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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.PrintSettings;
import com.dpdocter.beans.PrintSettingsDefaultData;
import com.dpdocter.collections.PrintSettingsCollection;
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

    @Path(value = PathProxy.PrintSettingsUrls.SAVE_SETTINGS_DEFAULT_DATA)
    @POST
    @ApiOperation(value = "SAVE_SETTINGS_DEFAULT_DATA", notes = "SAVE_SETTINGS_DEFAULT_DATA")
    public Response<PrintSettingsDefaultData> saveDefaultSettings(PrintSettingsDefaultData request) {

	if (request == null) {
	    logger.warn("Request cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");
	}
	PrintSettingsDefaultData printSettingsData = printSettingsService.saveDefaultSettings(request);
	Response<PrintSettingsDefaultData> response = new Response<PrintSettingsDefaultData>();
	response.setData(printSettingsData);
	return response;
    }

    @GET
    @ApiOperation(value = "GET_SETTINGS_DEFAULT_DATA", notes = "GET_SETTINGS_DEFAULT_DATA")
    public Response<PrintSettingsDefaultData> getDefaultSettings() {

	List<PrintSettingsDefaultData> printSettingsData = printSettingsService.getDefaultSettings();
	Response<PrintSettingsDefaultData> response = new Response<PrintSettingsDefaultData>();
	response.setDataList(printSettingsData);
	return response;
    }

    @Path(value = PathProxy.PrintSettingsUrls.SAVE_PRINT_SETTINGS)
    @POST
    @ApiOperation(value = "SAVE_PRINT_SETTINGS", notes = "SAVE_PRINT_SETTINGS")
    public Response<PrintSettings> saveSettings(PrintSettings request) {

	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	PrintSettings printSettings = printSettingsService.saveSettings(request);
	if (printSettings != null)
	    printSettings.setClinicLogoUrl(getFinalImageURL(printSettings.getClinicLogoUrl()));
	Response<PrintSettings> response = new Response<PrintSettings>();
	response.setData(printSettings);
	return response;
    }

    @Path(value = PathProxy.PrintSettingsUrls.GET_PRINT_SETTINGS)
    @GET
    @ApiOperation(value = "GET_PRINT_SETTINGS", notes = "GET_PRINT_SETTINGS")
    public Response<PrintSettings> getSettings(@PathParam(value = "printFilter") String printFilter, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @QueryParam(value = "page") int page,
	    @QueryParam(value = "size") int size, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

	if (DPDoctorUtils.anyStringEmpty(printFilter, doctorId, locationId, hospitalId)) {
	    logger.warn("PrintFilter, DoctorId or locationId or hospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "PrintFilter, DoctorId or locationId or hospitalId cannot be null");
	}
	List<PrintSettings> printSettings = printSettingsService.getSettings(printFilter, doctorId, locationId, hospitalId, page, size, updatedTime, discarded);
	if (printSettings != null) {
	    for (Object pSettings : printSettings) {
		((PrintSettingsCollection) pSettings).setClinicLogoUrl(getFinalImageURL(((PrintSettingsCollection) pSettings).getClinicLogoUrl()));
	    }
	}
	Response<PrintSettings> response = new Response<PrintSettings>();
	response.setDataList(printSettings);
	return response;
    }

    @Path(value = PathProxy.PrintSettingsUrls.DELETE_PRINT_SETTINGS)
    @DELETE
    @ApiOperation(value = "DELETE_PRINT_SETTINGS", notes = "DELETE_PRINT_SETTINGS")
    public Response<PrintSettings> deletePrintSettings(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

	if (DPDoctorUtils.anyStringEmpty(id, doctorId, locationId, hospitalId)) {
	    logger.warn("Id, DoctorId or locationId or hospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "Id, DoctorId or locationId or hospitalId cannot be null");
	}
	PrintSettings printSettings = printSettingsService.deletePrintSettings(id, doctorId, locationId, hospitalId, discarded);
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

}
