package com.dpdocter.tokenstore;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RestController
public class CustomPasswordEncoder implements PasswordEncoder {
	@Override
	public String encode(CharSequence rawPassword) {
		String hashed = BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(12));
		return hashed;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		System.out.println(encode(rawPassword));
		System.out.println(encodedPassword);
		return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
	}
}
