package com.dpdocter.services.impl;

import org.springframework.stereotype.Service;

import com.dpdocter.services.MailBodyGenerator;
@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

	public String generateActivationEmailBody(String userName, String fName,
			String mName, String lName) throws Exception{
		StringBuffer body = new StringBuffer();
		body.append("Dear "+fName+ " "+lName+", \n");
		body.append("Please click on below link to activate the account.");
		return body.toString();
	}

}
