package com.dpdocter.request;

public class LoginRequest {

    private String username;

    private char[] password;

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public char[] getPassword() {
	return password;
    }

    public void setPassword(char[] password) {
	this.password = password;
    }

    @Override
    public String toString() {
	return "LoginRequest [username=" + username + ", password=" + password + "]";
    }
}
