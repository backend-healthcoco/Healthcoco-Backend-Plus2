package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.Quantity;
import com.dpdocter.beans.Tax;
import com.dpdocter.enums.InvoiceItemType;
import com.dpdocter.enums.PatientTreatmentStatus;

public class InvoiceItemResponse {

	private String itemId;

	private String doctorId;

	private String doctorName;

	private Date itemDate;
	
	private String name;

	private InvoiceItemType type;

	private Quantity quantity;

	private PatientTreatmentStatus status;

	private Long inventoryQuantity;

	private boolean saveToInventory = false;

	private Double cost = 0.0;

	private Discount discount;

	private Tax tax;

	private Double finalCost = 0.0;

	private List<Fields> treatmentFields;

	private InventoryBatch inventoryBatch;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InvoiceItemType getType() {
		return type;
	}

	public void setType(InvoiceItemType type) {
		this.type = type;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	public PatientTreatmentStatus getStatus() {
		return status;
	}

	public void setStatus(PatientTreatmentStatus status) {
		this.status = status;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public Double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(Double finalCost) {
		this.finalCost = finalCost;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}

	public Long getInventoryQuantity() {
		return inventoryQuantity;
	}

	public void setInventoryQuantity(Long inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}

	public boolean isSaveToInventory() {
		return saveToInventory;
	}

	public void setSaveToInventory(boolean saveToInventory) {
		this.saveToInventory = saveToInventory;
	}

	public InventoryBatch getInventoryBatch() {
		return inventoryBatch;
	}

	public void setInventoryBatch(InventoryBatch inventoryBatch) {
		this.inventoryBatch = inventoryBatch;
	}
	
	public Date getItemDate() {
		return itemDate;
	}

	public void setItemDate(Date itemDate) {
		this.itemDate = itemDate;
	}

	@Override
	public String toString() {
		return "InvoiceItemResponse [itemId=" + itemId + ", doctorId=" + doctorId + ", doctorName=" + doctorName
				+ ", itemDate=" + itemDate + ", name=" + name + ", type=" + type + ", quantity=" + quantity
				+ ", status=" + status + ", inventoryQuantity=" + inventoryQuantity + ", saveToInventory="
				+ saveToInventory + ", cost=" + cost + ", discount=" + discount + ", tax=" + tax + ", finalCost="
				+ finalCost + ", treatmentFields=" + treatmentFields + ", inventoryBatch=" + inventoryBatch + "]";
	}

}
