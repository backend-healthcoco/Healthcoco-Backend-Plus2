package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.OrderStatus;

public class OrderDiagnosticTest extends GenericCollection{

	private String id;
	
	private String locationId;
	
	private String userId;
	
	private String uniqueOrderId;
	
	private LocaleWorkingHours pickUpTime;
	
	private Long pickUpDate;
	
	private List<String> testsPackageIds;

	private String testsPackageId;

	private List<DiagnosticTest> diagnosticTests;
	
	private UserAddress pickUpAddress;
		
	private OrderStatus orderStatus = OrderStatus.PLACED;
	
	private Double totalCost = 0.0;

	private Double totalCostForPatient = 0.0;

	private Double totalSavingInPercentage = 0.0;
	
	private Boolean isCancelled = false;

	private String locationName;
	
	private String patientName;
	
	private Boolean isNABLAccredited = false;
	
	List<DiagnosticTestPackage> testsPackages;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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

	public String getTestsPackageId() {
		return testsPackageId;
	}

	public void setTestsPackageId(String testsPackageId) {
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

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Boolean getIsNABLAccredited() {
		return isNABLAccredited;
	}

	public void setIsNABLAccredited(Boolean isNABLAccredited) {
		this.isNABLAccredited = isNABLAccredited;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public List<String> getTestsPackageIds() {
		return testsPackageIds;
	}

	public void setTestsPackageIds(List<String> testsPackageIds) {
		this.testsPackageIds = testsPackageIds;
	}

	public List<DiagnosticTestPackage> getTestsPackages() {
		return testsPackages;
	}

	public void setTestsPackages(List<DiagnosticTestPackage> testsPackages) {
		this.testsPackages = testsPackages;
	}

	@Override
	public String toString() {
		return "OrderDiagnosticTest [id=" + id + ", locationId=" + locationId + ", userId=" + userId
				+ ", uniqueOrderId=" + uniqueOrderId + ", pickUpTime=" + pickUpTime + ", pickUpDate=" + pickUpDate
				+ ", testsPackageIds=" + testsPackageIds + ", testsPackageId=" + testsPackageId + ", diagnosticTests="
				+ diagnosticTests + ", pickUpAddress=" + pickUpAddress + ", orderStatus=" + orderStatus + ", totalCost="
				+ totalCost + ", totalCostForPatient=" + totalCostForPatient + ", totalSavingInPercentage="
				+ totalSavingInPercentage + ", isCancelled=" + isCancelled + ", locationName=" + locationName
				+ ", patientName=" + patientName + ", isNABLAccredited=" + isNABLAccredited + ", testsPackages="
				+ testsPackages + "]";
	}

}
