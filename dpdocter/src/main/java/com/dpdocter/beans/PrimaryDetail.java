package com.dpdocter.beans;

import java.util.Calendar;
import java.util.Date;

import com.dpdocter.enums.GenderType;

public class PrimaryDetail {
	
	private String name;
	private String mobilenumber;
	private Date DateOfBirth;
	private GenderType gender;
	private Integer age=0;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobilenumber() {
		return mobilenumber;
	}
	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	public Date getDateOfBirth() {
		return DateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		DateOfBirth = dateOfBirth;
	}
	public GenderType getGender() {
		return gender;
	}
	public void setGender(GenderType gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		
		Calendar today = Calendar.getInstance();
	    Calendar birthDate = Calendar.getInstance();

	     age = 0;

	    birthDate.setTime(this.getDateOfBirth());
	    if (birthDate.after(today)) {
	        throw new IllegalArgumentException("Can't be born in the future");
	    }

	    age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

	    // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year   
	    if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
	            (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
	        age--;

	     // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
	    }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
	              (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
	        age--;
	    }
	    
	    this.age = age;
	}
	
	
	@Override
	public String toString() {
		return "PrimaryDetail [name=" + name + ", mobilenumber=" + mobilenumber + ", DateOfBirth="
				+ DateOfBirth + ", gender=" + gender + ", age=" + age + "]";
	}
	

}
