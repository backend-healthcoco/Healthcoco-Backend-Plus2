package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.Manufacturer;

public interface InventoryService {

	InventoryItem addItem(InventoryItem inventoryItem);

	List<InventoryItem> getInventoryItemList(String locationId, String hospitalId, String type, String searchTerm,
			int page, int size);

	List<Manufacturer> getManufacturerList(String locationId, String hospitalId, String searchTerm, int page, int size);

}
