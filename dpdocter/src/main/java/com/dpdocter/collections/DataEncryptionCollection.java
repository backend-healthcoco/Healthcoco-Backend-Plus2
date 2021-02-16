package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "encryption_key_cl")
public class DataEncryptionCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String senderPublicKey;
	@Field
	private String senderPrivateKey;
	@Field
	private String receiverPublicKey;
	@Field
	private String receiverPrivateKey;
	@Field
	private String randomSender;
	@Field
	private String randomReceiver;
	@Field
	private String sharedSenderNonce;
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getSenderPublicKey() {
		return senderPublicKey;
	}
	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
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
	public String getRandomSender() {
		return randomSender;
	}
	public void setRandomSender(String randomSender) {
		this.randomSender = randomSender;
	}
	public String getRandomReceiver() {
		return randomReceiver;
	}
	public void setRandomReceiver(String randomReceiver) {
		this.randomReceiver = randomReceiver;
	}
	public String getSharedSenderNonce() {
		return sharedSenderNonce;
	}
	public void setSharedSenderNonce(String sharedSenderNonce) {
		this.sharedSenderNonce = sharedSenderNonce;
	}
	
	
	
}
