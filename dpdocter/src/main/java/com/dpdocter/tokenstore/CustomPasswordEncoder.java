package com.dpdocter.tokenstore;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

	//String salt = "$2a$12$HHBRV5pOMt9wQ9Ve.2mnhu";
	
	@Override
    public String encode(CharSequence rawPassword) {
        String hashed = BCrypt.hashpw(String.valueOf(rawPassword), BCrypt.gensalt(12));
        return hashed;
    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(String.valueOf(rawPassword), encodedPassword);
    }
}
