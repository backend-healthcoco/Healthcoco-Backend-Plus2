package com.dpdocter.request;

public class KeyMaterialRequestDataFlow {

	private String cryptoAlg;
	private String curve;
	private DhPublicKeyDataFlowRequest dhPublicKey;
	private String nonce;
	public String getCryptoAlg() {
		return cryptoAlg;
	}
	public void setCryptoAlg(String cryptoAlg) {
		this.cryptoAlg = cryptoAlg;
	}
	public String getCurve() {
		return curve;
	}
	public void setCurve(String curve) {
		this.curve = curve;
	}
	public DhPublicKeyDataFlowRequest getDhPublicKey() {
		return dhPublicKey;
	}
	public void setDhPublicKey(DhPublicKeyDataFlowRequest dhPublicKey) {
		this.dhPublicKey = dhPublicKey;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	
	

}
