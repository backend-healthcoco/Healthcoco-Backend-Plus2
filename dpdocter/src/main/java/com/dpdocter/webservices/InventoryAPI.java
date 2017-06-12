package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.Manufacturer;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.InventoryService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.INVENTORY_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.INVENTORY_BASE_URL, description = "Endpoint for Inventory API's")
public class InventoryAPI {

	private static final Logger LOGGER = Logger.getLogger(InventoryAPI.class.getName());

	@Autowired
	InventoryService inventoryService;
	
	@POST
	@ApiOperation(value = PathProxy.InventoryUrls.ADD_INVENTORY_ITEM, notes = PathProxy.InventoryUrls.ADD_INVENTORY_ITEM)
	@Path(PathProxy.InventoryUrls.ADD_INVENTORY_ITEM)
	public Response<InventoryItem> addInventoryItem(InventoryItem request)
	{
		Response<InventoryItem> response = new Response<>();
		InventoryItem inventoryItem = null;
		try {
			if(request == null)
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			inventoryItem = inventoryService.addItem(request);
			if(inventoryItem != null)
			{
				response.setData(inventoryItem);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while adding inventory item");
			e.printStackTrace();
		}
		return response;
	}
	
	@GET
	@ApiOperation(value = PathProxy.InventoryUrls.GET_INVENTORY_ITEMS, notes = PathProxy.InventoryUrls.GET_INVENTORY_ITEMS)
	@Path(PathProxy.InventoryUrls.GET_INVENTORY_ITEMS)
	public Response<InventoryItem> getInventoryItem(@QueryParam("hospitalId") String hospitalId , @QueryParam("locationId") String locationId , @QueryParam("searchTerm") String searchTerm , @QueryParam("type") String type , @QueryParam("page") int page ,@QueryParam("size") int size  )
	{
		Response<InventoryItem> response = new Response<>();
		List<InventoryItem> inventoryItems = null;
		try {
			if(DPDoctorUtils.anyStringEmpty(hospitalId,locationId))
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			inventoryItems = inventoryService.getInventoryItemList(locationId, hospitalId, type, searchTerm, page, size);
			if(inventoryItems != null)
			{
				response.setDataList(inventoryItems);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while getting inventory item");
			e.printStackTrace();
		}
		return response;
	}
	
	@GET
	@ApiOperation(value = PathProxy.InventoryUrls.GET_MANUFACTURERS, notes = PathProxy.InventoryUrls.GET_MANUFACTURERS)
	@Path(PathProxy.InventoryUrls.GET_MANUFACTURERS)
	public Response<Manufacturer> getManufacturers(@QueryParam("hospitalId") String hospitalId , @QueryParam("locationId") String locationId , @QueryParam("searchTerm") String searchTerm, @QueryParam("page") int page ,@QueryParam("size") int size  )
	{
		Response<Manufacturer> response = new Response<>();
		List<Manufacturer> manufacturers = null;
		try {
			if(DPDoctorUtils.anyStringEmpty(hospitalId,locationId))
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			manufacturers = inventoryService.getManufacturerList(locationId, hospitalId, searchTerm, page, size);
			if(manufacturers != null)
			{
				response.setDataList(manufacturers);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while getting inventory item");
			e.printStackTrace();
		}
		return response;
	}
	
}
