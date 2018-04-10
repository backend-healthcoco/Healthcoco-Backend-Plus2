package com.dpdocter.beans;

public class LaptopUsage {
	
	private Integer hoursperday=0;
	private Boolean LaptopInBedroom;
	
	public int getHoursperday() {
		return hoursperday;
	}
	public void setHoursperday(int hoursperday) {
		this.hoursperday = hoursperday;
	}
	public Boolean getLaptopInBedroom() {
		return LaptopInBedroom;
	}
	public void setLaptopInBedroom(Boolean laptopInBedroom) {
		LaptopInBedroom = laptopInBedroom;
	}
	
	
	@Override
	public String toString() {
		return "LaptopUsage [hoursperday=" + hoursperday + ", LaptopInBedroom=" + LaptopInBedroom + "]";
	}
	
	

}
