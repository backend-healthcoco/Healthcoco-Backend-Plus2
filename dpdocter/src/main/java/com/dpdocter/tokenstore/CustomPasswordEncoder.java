package com.dpdocter.tokenstore;

import java.io.UnsupportedEncodingException;

import org.springframework.security.authentication.encoding.BaseDigestPasswordEncoder;
import org.springframework.stereotype.Component;

import common.util.web.DPDoctorUtils;
@Component
public class CustomPasswordEncoder extends BaseDigestPasswordEncoder {

	@Override
	public String encodePassword(String rawPass, Object salt) {
		String saltedPass = mergePasswordAndSalt(rawPass, salt, false);
		char[] pass = saltedPass.toCharArray();
		try {
			pass = DPDoctorUtils.getSHA3SecurePassword(pass);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.valueOf(pass);
	}
	
	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		// TODO Auto-generated method stub
		String pass1 = "" + encPass;
		
		String pass2 = encodePassword(rawPass, salt);
		return match( pass1, pass2);
	}

	boolean match(String pass1, String pass2)

	{
		if (pass1.equals(pass2)) {
			return true;
		}
		return false;

	}

}
