package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.InventoryStock;
import com.dpdocter.beans.Manufacturer;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.response.InventoryStockLookupResponse;

public interface InventoryService {

	InventoryItem addItem(InventoryItem inventoryItem);

	List<InventoryItemLookupResposne> getInventoryItemList(String locationId, String hospitalId, String type, String searchTerm,
			int page, int size);

	List<Manufacturer> getManufacturerList(String locationId, String hospitalId, String searchTerm, int page, int size);

	List<InventoryStockLookupResponse> getInventoryStockList(String locationId, String hospitalId, String searchTerm, int page,
			int size);

	InventoryStock addInventoryStock(InventoryStock inventoryStock);

	InventoryItemLookupResposne getInventoryItem(String id);

	InventoryStock discardInventoryStock(String id, Boolean discarded);

	InventoryBatch discardInventoryBatch(String id, Boolean discarded);

	InventoryItem discardInventoryItem(String id, Boolean discarded);

	List<InventoryBatch> getInventoryBatchList(String locationId, String hospitalId, String searchTerm, int page,
			int size);

	InventoryBatch addInventoryBatch(InventoryBatch inventoryBatch);

	Manufacturer addManufacturer(Manufacturer manufacturer);

	Integer getInventoryStockListCount(String locationId, String hospitalId, String searchTerm);

	Integer getInventoryItemListCount(String locationId, String hospitalId, String type, String searchTerm);

}
