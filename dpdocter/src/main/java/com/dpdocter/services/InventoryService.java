package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.InventorySettings;
import com.dpdocter.beans.InventoryStock;
import com.dpdocter.beans.Manufacturer;
import com.dpdocter.request.InventorySettingRequest;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.response.InventoryStockLookupResponse;

public interface InventoryService {

	InventoryItem addItem(InventoryItem inventoryItem);

	List<InventoryItemLookupResposne> getInventoryItemList(String locationId, String hospitalId, String type, String searchTerm,
			int page, int size);

	List<Manufacturer> getManufacturerList(String locationId, String hospitalId, String searchTerm, int page, int size);

	InventoryStock addInventoryStock(InventoryStock inventoryStock);

	InventoryItemLookupResposne getInventoryItem(String id);

	InventoryStock discardInventoryStock(String id, Boolean discarded);

	InventoryBatch discardInventoryBatch(String id, Boolean discarded);

	InventoryItem discardInventoryItem(String id, Boolean discarded);

	InventoryBatch addInventoryBatch(InventoryBatch inventoryBatch);

	Manufacturer addManufacturer(Manufacturer manufacturer);

	Integer getInventoryStockListCount(String locationId, String hospitalId, String itemId,
			String stockType, String searchTerm);

	Integer getInventoryItemListCount(String locationId, String hospitalId, String type, String searchTerm);

	List<InventoryBatch> getInventoryBatchList(String locationId, String hospitalId, String itemId, String searchTerm,
			int page, int size);

	List<InventoryStockLookupResponse> getInventoryStockList(String locationId, String hospitalId, String itemId,
			String stockType, String searchTerm, int page, int size);

	//InventorySettings getInventorySetting(String doctorId, String locationId, String hospitalId);

	InventorySettings addEditInventorySetting(InventorySettingRequest request);

	InventoryItem getInventoryItemByResourceId(String locationId, String hospitalId, String resourceId);

	InventorySettings getInventorySetting(String id, String doctorId, String locationId, String hospitalId);

	List<InventoryBatch> getInventoryBatchByResourceId(String locationId, String hospitalId, String resourceId);

	InventoryStock getInventoryStockByInvoiceIdResourceId(String locationId, String hospitalId, String resourceId,
			String invoiceId);

	InventoryBatch getInventoryBatchById(String id);

	Long getInventoryStockItemCount(String locationId, String hospitalId, String resourceId, String invoiceId);

}
