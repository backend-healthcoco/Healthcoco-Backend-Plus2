package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.InventoryStock;
import com.dpdocter.beans.Manufacturer;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.response.InventoryStockLookupResponse;
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
	
	@POST
	@ApiOperation(value = PathProxy.InventoryUrls.ADD_INVENTORY_STOCK, notes = PathProxy.InventoryUrls.ADD_INVENTORY_STOCK)
	@Path(PathProxy.InventoryUrls.ADD_INVENTORY_STOCK)
	public Response<InventoryStock> addInventoryStock(InventoryStock request)
	{
		Response<InventoryStock> response = new Response<>();
		InventoryStock inventoryStock = null;
		try {
			if(request == null)
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			inventoryStock = inventoryService.addInventoryStock(request);
			if(inventoryStock != null)
			{
				response.setData(inventoryStock);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while adding inventory stock");
			e.printStackTrace();
		}
		return response;
	}
	
	@POST
	@ApiOperation(value = PathProxy.InventoryUrls.ADD_MANUFACTURER, notes = PathProxy.InventoryUrls.ADD_MANUFACTURER)
	@Path(PathProxy.InventoryUrls.ADD_MANUFACTURER)
	public Response<Manufacturer> addManufacturer(Manufacturer request)
	{
		Response<Manufacturer> response = new Response<>();
		Manufacturer manufacturer = null;
		try {
			if(request == null)
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			manufacturer = inventoryService.addManufacturer(request);
			if(manufacturer != null)
			{
				response.setData(manufacturer);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while adding manufacturer");
			e.printStackTrace();
		}
		return response;
	}
	
	
	@POST
	@ApiOperation(value = PathProxy.InventoryUrls.ADD_INVENTORY_BATCH, notes = PathProxy.InventoryUrls.ADD_INVENTORY_BATCH)
	@Path(PathProxy.InventoryUrls.ADD_INVENTORY_BATCH)
	public Response<InventoryBatch> addInventoryBatch(InventoryBatch request)
	{
		Response<InventoryBatch> response = new Response<>();
		InventoryBatch inventoryBatch = null;
		try {
			if(request == null)
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			inventoryBatch = inventoryService.addInventoryBatch(request);
			if(inventoryBatch != null)
			{
				response.setData(inventoryBatch);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while adding inventory batch");
			e.printStackTrace();
		}
		return response;
	}
	
	@GET
	@ApiOperation(value = PathProxy.InventoryUrls.GET_INVENTORY_ITEMS, notes = PathProxy.InventoryUrls.GET_INVENTORY_ITEMS)
	@Path(PathProxy.InventoryUrls.GET_INVENTORY_ITEMS)
	public Response<Object> getInventoryItem(@QueryParam("hospitalId") String hospitalId , @QueryParam("locationId") String locationId , @QueryParam("searchTerm") String searchTerm , @QueryParam("type") String type , @QueryParam("page") int page ,@QueryParam("size") int size  )
	{
		Response<Object> response = new Response<>();
		List<InventoryItemLookupResposne> inventoryItems = null;
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
			response.setData(inventoryService.getInventoryItemListCount(locationId, hospitalId, type, searchTerm));
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while getting inventory item");
			e.printStackTrace();
		}
		return response;
	}
	
	@GET
	@ApiOperation(value = PathProxy.InventoryUrls.GET_INVENTORY_ITEM_BY_ID, notes = PathProxy.InventoryUrls.GET_INVENTORY_ITEM_BY_ID)
	@Path(PathProxy.InventoryUrls.GET_INVENTORY_ITEM_BY_ID)
	public Response<InventoryItemLookupResposne> getInventoryItemById(@PathParam("id") String id)
	{
		Response<InventoryItemLookupResposne> response = new Response<>();
		InventoryItemLookupResposne inventoryItem = null;
		try {
			if(DPDoctorUtils.anyStringEmpty(id))
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			inventoryItem = inventoryService.getInventoryItem(id);
			if(inventoryItem != null)
			{
				response.setData(inventoryItem);
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
	
	
	@GET
	@ApiOperation(value = PathProxy.InventoryUrls.GET_INVENTORY_STOCKS, notes = PathProxy.InventoryUrls.GET_INVENTORY_STOCKS)
	@Path(PathProxy.InventoryUrls.GET_INVENTORY_STOCKS)
	public Response<Object> getInventoryStock(@QueryParam("hospitalId") String hospitalId , @QueryParam("locationId") String locationId , @QueryParam("searchTerm") String searchTerm , @QueryParam("page") int page ,@QueryParam("size") int size  )
	{
		Response<Object> response = new Response<>();
		List<InventoryStockLookupResponse> inventoryStocks = null;
		try {
			if(DPDoctorUtils.anyStringEmpty(hospitalId,locationId))
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			inventoryStocks = inventoryService.getInventoryStockList(locationId, hospitalId, searchTerm, page, size);
			if(inventoryStocks != null)
			{
				response.setDataList(inventoryStocks);
			}
			response.setData(inventoryService.getInventoryStockListCount(locationId, hospitalId, searchTerm));
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while getting inventory stocks");
			e.printStackTrace();
		}
		return response;
	}
	
	@GET
	@ApiOperation(value = PathProxy.InventoryUrls.GET_INVENTORY_BATCHES, notes = PathProxy.InventoryUrls.GET_INVENTORY_BATCHES)
	@Path(PathProxy.InventoryUrls.GET_INVENTORY_BATCHES)
	public Response<InventoryBatch> getInventoryBatches(@QueryParam("hospitalId") String hospitalId , @QueryParam("locationId") String locationId , @QueryParam("searchTerm") String searchTerm, @QueryParam("page") int page ,@QueryParam("size") int size  )
	{
		Response<InventoryBatch> response = new Response<>();
		List<InventoryBatch> inventoryBatches = null;
		try {
			if(DPDoctorUtils.anyStringEmpty(hospitalId,locationId))
			{
				throw new BusinessException(ServiceError.InvalidInput , "Invalid Input");
			}
			inventoryBatches = inventoryService.getInventoryBatchList(locationId, hospitalId, searchTerm, page, size);
			if(inventoryBatches != null)
			{
				response.setDataList(inventoryBatches);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Error while getting inventory item");
			e.printStackTrace();
		}
		return response;
	}
}
