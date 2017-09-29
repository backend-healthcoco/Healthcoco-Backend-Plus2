package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.UserAddress;

public class UserAddressResponse {

	List<UserAddress> userAddress;

	public List<UserAddress> getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(List<UserAddress> userAddress) {
		this.userAddress = userAddress;
	}

	@Override
	public String toString() {
		return "UserAddressResponse [userAddress=" + userAddress + "]";
	}
}
