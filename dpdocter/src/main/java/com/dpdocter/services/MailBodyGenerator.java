package com.dpdocter.services;

public interface MailBodyGenerator {
	public String generateActivationEmailBody(String userName,String fName,String mName,String lName)throws Exception;
}
