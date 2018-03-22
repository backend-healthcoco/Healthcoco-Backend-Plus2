package com.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class TvUsage {
	
	private Integer hoursperday=0;	
	private boolean tv_in_bedroom;
	private ArrayList<Date> WatchTo=new ArrayList<Date>();
	private ArrayList<Date> WatchFrom=new ArrayList<Date>();
	
	public boolean isTv_in_bedroom() {
		return tv_in_bedroom;
	}
	
	
	public void setTv_in_bedroom(boolean tv_in_bedroom) {
		this.tv_in_bedroom = tv_in_bedroom;
	}
	
	
	public int getHoursperday() {
		return hoursperday;
	}
	
	
	public void setHoursperday(int hoursperday) {
		
		hoursperday = 0;
		
		Iterator<Date> itrTo=WatchTo.iterator();  
		Iterator<Date> itrFrom=WatchFrom.iterator();  
		
		  while(itrTo.hasNext() && itrFrom.hasNext()){  
			  
		         long diff = ((Date) itrTo.next()).getTime() - ((Date) itrFrom.next()).getTime();
			     long diffMinutes = diff / (60 * 1000) % 60;
			     long diffHours = diff / (60 * 60 * 1000);
			     hoursperday = (int) (hoursperday + diffHours + diffMinutes/60);
			  
		  }  
		this.hoursperday = hoursperday;
	}
	
	
	public ArrayList<Date> getWatchTo() {
		return WatchTo;
	}
	
	
	public void setWatchTo(ArrayList<Date> watchTo) {
		this.WatchTo = watchTo; 
	}
	
	
	public ArrayList<Date> getWatchFrom() {
		return WatchFrom;
	}
	
	
	public void setWatchFrom(ArrayList<Date> watchFrom) {
		this.WatchFrom = watchFrom;
	}


	@Override
	public String toString() {
		return "TvUsage [hoursperday=" + hoursperday + ", tv_in_bedroom=" + tv_in_bedroom + ", WatchTo=" + WatchTo
				+ ", WatchFrom=" + WatchFrom + "]";
	}
		

}
