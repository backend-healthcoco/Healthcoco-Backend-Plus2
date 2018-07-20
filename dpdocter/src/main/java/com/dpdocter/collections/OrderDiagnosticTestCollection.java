package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.LocaleWorkingHours;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.enums.OrderStatus;

@Document(collection = "order_diagnostic_test_cl")
public class OrderDiagnosticTestCollection extends GenericCollection {
	
	@Id
	private ObjectId id;
	
	@Field
	private ObjectId locationId;
	
	@Field
	private ObjectId userId;
	
	@Field
	private String uniqueOrderId;
	
	@Field
	private LocaleWorkingHours pickUpTime;
	
	@Field
	private Long pickUpDate;
	
	@Field
	private List<ObjectId> testsPackageIds;

	@Field
	private ObjectId testsPackageId;

	@Field
	private List<DiagnosticTest> diagnosticTests;
	
	@Field
	private UserAddress pickUpAddress;
		
	@Field
	private OrderStatus orderStatus = OrderStatus.PLACED;
	
	@Field
	private Double totalCost = 0.0;

	@Field
    private Double totalCostForPatient = 0.0;

	@Field
	private Double totalSavingInPercentage = 0.0;
	
	@Field
	private Boolean isCancelled = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getUniqueOrderId() {
		return uniqueOrderId;
	}

	public void setUniqueOrderId(String uniqueOrderId) {
		this.uniqueOrderId = uniqueOrderId;
	}

	public LocaleWorkingHours getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(LocaleWorkingHours pickUpTime) {
		this.pickUpTime = pickUpTime;
	}

	public Long getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Long pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public ObjectId getTestsPackageId() {
		return testsPackageId;
	}

	public void setTestsPackageId(ObjectId testsPackageId) {
		this.testsPackageId = testsPackageId;
	}

	public List<DiagnosticTest> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<DiagnosticTest> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public UserAddress getPickUpAddress() {
		return pickUpAddress;
	}

	public void setPickUpAddress(UserAddress pickUpAddress) {
		this.pickUpAddress = pickUpAddress;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Double getTotalCostForPatient() {
		return totalCostForPatient;
	}

	public void setTotalCostForPatient(Double totalCostForPatient) {
		this.totalCostForPatient = totalCostForPatient;
	}

	public Double getTotalSavingInPercentage() {
		return totalSavingInPercentage;
	}

	public void setTotalSavingInPercentage(Double totalSavingInPercentage) {
		this.totalSavingInPercentage = totalSavingInPercentage;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public List<ObjectId> getTestsPackageIds() {
		return testsPackageIds;
	}

	public void setTestsPackageIds(List<ObjectId> testsPackageIds) {
		this.testsPackageIds = testsPackageIds;
	}

	@Override
	public String toString() {
		return "OrderDiagnosticTestCollection [id=" + id + ", locationId=" + locationId + ", userId=" + userId
				+ ", uniqueOrderId=" + uniqueOrderId + ", pickUpTime=" + pickUpTime + ", pickUpDate=" + pickUpDate
				+ ", testsPackageIds=" + testsPackageIds + ", testsPackageId=" + testsPackageId + ", diagnosticTests="
				+ diagnosticTests + ", pickUpAddress=" + pickUpAddress + ", orderStatus=" + orderStatus + ", totalCost="
				+ totalCost + ", totalCostForPatient=" + totalCostForPatient + ", totalSavingInPercentage="
				+ totalSavingInPercentage + ", isCancelled=" + isCancelled + "]";
	}

}
