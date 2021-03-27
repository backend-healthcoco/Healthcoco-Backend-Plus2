//package com.dpdocter.tokenstore;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//public class ClientcustomPasswordEncoder implements PasswordEncoder {
//
//	public boolean matches(CharSequence rawPassword, String encodedPassword) {
//		if (rawPassword != null) {
//			String raw = rawPassword.toString();
//			if (raw.equalsIgnoreCase(encodedPassword)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	@Override
//	public String encode(CharSequence rawPassword) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
