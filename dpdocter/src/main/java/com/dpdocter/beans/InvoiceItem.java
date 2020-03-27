package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.enums.InvoiceItemType;
import com.dpdocter.enums.PatientTreatmentStatus;

public class InvoiceItem {

	private ObjectId itemId;

	private ObjectId doctorId;

	private String doctorName;

	private String name;

	private InvoiceItemType type;// = [SERVICE || PRODUCT]

	private Quantity quantity;
	
	private Quantity day;

	private Long inventoryQuantity;
	
	private boolean saveToInventory = false;

	private PatientTreatmentStatus status;

	private Double cost = 0.0;

	private Discount discount;

	private Tax tax;

	private Double finalCost = 0.0;

	private InventoryBatch inventoryBatch;

	private String note;
	
	private List<Fields> treatmentFields;
	
	public ObjectId getItemId() {
		return itemId;
	}

	public void setItemId(ObjectId itemId) {
		this.itemId = itemId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
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

	public InventoryBatch getInventoryBatch() {
		return inventoryBatch;
	}

	public void setInventoryBatch(InventoryBatch inventoryBatch) {
		this.inventoryBatch = inventoryBatch;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}
	
	

	public Quantity getDay() {
		return day;
	}

	public void setDay(Quantity day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "InvoiceItem [itemId=" + itemId + ", doctorId=" + doctorId + ", doctorName=" + doctorName + ", name="
				+ name + ", type=" + type + ", quantity=" + quantity + ", inventoryQuantity=" + inventoryQuantity
				+ ", saveToInventory=" + saveToInventory + ", status=" + status + ", cost=" + cost + ", discount="
				+ discount + ", tax=" + tax + ", finalCost=" + finalCost + ", inventoryBatch=" + inventoryBatch
				+ ", note=" + note + ", treatmentFields=" + treatmentFields + "]";
	}

}
