package com.dpdocter.tokenstore;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		String hashed = BCrypt.hashpw((String) rawPassword, BCrypt.gensalt(12));
		return hashed;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return BCrypt.checkpw((String) rawPassword, encodedPassword);
	}
}
