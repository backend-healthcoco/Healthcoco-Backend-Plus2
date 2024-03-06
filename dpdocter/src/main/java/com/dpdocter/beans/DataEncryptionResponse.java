package com.dpdocter.beans;

public class DataEncryptionResponse {

	private String encryptedData;
	
	private String decryptedData;
	
	private String senderPublicKey;
	
	private String senderPrivateKey;
	
	private String receiverPublicKey;
	
	private String receiverPrivateKey;
	
	private String randomSender;
	
	private String randomReceiver;
	
	

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

	public String getSenderPrivateKey() {
		return senderPrivateKey;
	}

	public void setSenderPrivateKey(String senderPrivateKey) {
		this.senderPrivateKey = senderPrivateKey;
	}

	public String getReceiverPublicKey() {
		return receiverPublicKey;
	}

	public void setReceiverPublicKey(String receiverPublicKey) {
		this.receiverPublicKey = receiverPublicKey;
	}

	public String getReceiverPrivateKey() {
		return receiverPrivateKey;
	}

	public void setReceiverPrivateKey(String receiverPrivateKey) {
		this.receiverPrivateKey = receiverPrivateKey;
	}

	public String getRandomReceiver() {
		return randomReceiver;
	}

	public void setRandomReceiver(String randomReceiver) {
		this.randomReceiver = randomReceiver;
	}

	
	
	
	
}
