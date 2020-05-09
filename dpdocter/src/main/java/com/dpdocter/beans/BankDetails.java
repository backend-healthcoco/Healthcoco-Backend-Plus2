package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AccountType;

public class BankDetails extends GenericCollection{
	
	private String id;
	
	private String doctorId;
	
	private String accountholderName;
	
	private String accountNumber;
	
	private String ifscNumber;
	
	private String panCardNumber;
	
	private AccountType accountType;
	
	private String bankName;
	
	private String branchCity;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountholderName() {
		return accountholderName;
	}

	public void setAccountholderName(String accountholderName) {
		this.accountholderName = accountholderName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIfscNumber() {
		return ifscNumber;
	}

	public void setIfscNumber(String ifscNumber) {
		this.ifscNumber = ifscNumber;
	}

	public String getPanCardNumber() {
		return panCardNumber;
	}

	public void setPanCardNumber(String panCardNumber) {
		this.panCardNumber = panCardNumber;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}
	
	

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "BankDetails [id=" + id + ", accountholderName=" + accountholderName + ", accountNumber=" + accountNumber
				+ ", ifscNumber=" + ifscNumber + ", panCardNumber=" + panCardNumber + ", accountType=" + accountType
				+ ", bankName=" + bankName + ", branchCity=" + branchCity + "]";
	}
	
	 
}
