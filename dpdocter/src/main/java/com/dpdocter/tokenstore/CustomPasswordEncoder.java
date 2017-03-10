package com.dpdocter.tokenstore;

import java.io.UnsupportedEncodingException;

import org.springframework.security.authentication.encoding.BaseDigestPasswordEncoder;

import common.util.web.DPDoctorUtils;

public class CustomPasswordEncoder extends BaseDigestPasswordEncoder {
	/**
	 * @Harry
	 **/
	org.springframework.security.authentication.UsernamePasswordAuthenticationToken f;
	@Override
	public String encodePassword(String rawPass, Object salt) {
		String encrypt = rawPass;
		char[] password = null;
		char[] saltChar = null;
		char[] passwordWithSalt = null;
		try {
			if (salt != null) {
				saltChar = salt.toString().toCharArray();
				password = rawPass.toCharArray();
				passwordWithSalt = new char[password.length + saltChar.length];
				for (int i = 0; i < password.length; i++)
					passwordWithSalt[i] = password[i];
				for (int i = 0; i < saltChar.length; i++)
					passwordWithSalt[i + password.length] = saltChar[i];
				password = DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt);
			} else {
				password = DPDoctorUtils.getSHA3SecurePassword(encrypt.toCharArray());
			}
			String saltedPass = mergePasswordAndSalt(rawPass, salt, false);

			encrypt = String.valueOf(password);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encrypt;
	}

	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		// TODO Auto-generated method stub
		String pass1 = "" + encPass;
		String pass2 = encodePassword(rawPass, salt);
		return match(pass1, pass2);
	}

	boolean match(String pass1, String pass2)

	{
		if (pass1.equals(pass2)) {
			return true;
		}
		return false;

	}

}
