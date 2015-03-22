package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

public class LoginResponse {
	
	private User user;
	private List<Hospital> hospitals = null;
	private String role;
	
	
	

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Hospital> getHospitals() {
		if(hospitals == null){
			hospitals = new ArrayList<Hospital>();
		}
		return hospitals;
	}
	public void setHospitals(List<Hospital> hospitals) {
		this.hospitals = hospitals;
	}
	@Override
	public String toString() {
		return "LoginResponse [user=" + user + ", hospitals=" + hospitals
				+ ", role=" + role + "]";
	}
	

}
