package com.dpdocter.beans;

public class DataEncryptionResponse {

	private String encryptedData;
	
	private String decryptedData;
	
	private String senderPublicKey;
	
	private String randomSender;

	public String getEncryptedData() {
		return encryptedData;
	}

	public void setEncryptedData(String encryptedData) {
		this.encryptedData = encryptedData;
	}

	public String getDecryptedData() {
		return decryptedData;
	}

	public void setDecryptedData(String decryptedData) {
		this.decryptedData = decryptedData;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getRandomSender() {
		return randomSender;
	}

	public void setRandomSender(String randomSender) {
		this.randomSender = randomSender;
	}
	
	
}
