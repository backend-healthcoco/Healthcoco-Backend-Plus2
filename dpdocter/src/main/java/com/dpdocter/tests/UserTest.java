package com.dpdocter.tests;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dpdocter.beans.DOB;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.webservices.PathProxy;

public class UserTest {
	@Autowired
	private static UserRepository userRepository;

	public static void createUser(UserCollection user) {
		userRepository.save(user);
	}

	public static void createUsers(List<UserCollection> users) {
		userRepository.save(users);
	}

	public static void main(String[] args) {
		DOB dob = new DOB(30, 6, 1988, 26);

		UserCollection doctor = new UserCollection("1", "isank", "agarwal", "", "isankagarwal@gmail.com", "i123#", "isankagarwal@gmail.com", "9021703700",
				"male", "", dob, true);
		UserCollection patientOne = new UserCollection("2", "veeraj", "bhokre", "", "veeraj1", "v123#", "veeraj@gmail.com", "0123456789", "male", "", dob, true);
		patientOne.setTempPassword(true);
		UserCollection patientTwo = new UserCollection("3", "veeraj", "bhokre", "", "veeraj2", "v123#", "veeraj@gmail.com", "0123456789", "male", "", dob, true);
		UserCollection patientThree = new UserCollection("4", "veeraj", "bhokre", "", "veeraj3", "v123#", "veeraj@gmail.com", "0123456789", "male", "", dob,
				true);
		UserCollection patientFour = new UserCollection("5", "veeraj", "bhokre", "", "veeraj4", "v123#", "veeraj@gmail.com", "0123456789", "male", "", dob,
				true);
		UserCollection patientFive = new UserCollection("6", "veeraj", "bhokre", "", "veeraj5", "v123#", "veeraj@gmail.com", "0123456789", "male", "", dob,
				true);

		System.out.println(Converter.ObjectToJSON(patientOne));
		System.out.println(Converter.ObjectToJSON(patientTwo));
		System.out.println(Converter.ObjectToJSON(patientThree));
		System.out.println(Converter.ObjectToJSON(patientFour));
		System.out.println(Converter.ObjectToJSON(patientFive));

		System.out.println(PathProxy.FORGOT_PASSWORD_BASE_URL + PathProxy.ForgotPasswordUrls.FORGOT_USERNAME);

		userRepository.findByUserName("username");
	}
}
