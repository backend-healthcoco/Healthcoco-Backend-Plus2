package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MobilePhoneUsage {
	
	private Integer hoursperday=0;
	
	
	private ArrayList<Date> TalkTo=new ArrayList<Date>();
	private ArrayList<Date> TalkFrom=new ArrayList<Date>();

	
	public int getHoursperday() {
		return hoursperday;
	}
	
	
	public void setHoursperday(int hoursperday) 
	{
		
		hoursperday = 0;
		
		Iterator<Date> itrTo=TalkTo.iterator();  
		Iterator<Date> itrFrom=TalkFrom.iterator();  
		
		  while(itrTo.hasNext() && itrFrom.hasNext()){  
			  
		         long diff = ((Date) itrTo.next()).getTime() - ((Date) itrFrom.next()).getTime();
			     long diffMinutes = diff / (60 * 1000) % 60;
			     long diffHours = diff / (60 * 60 * 1000);
			     hoursperday = (int) (hoursperday + diffHours + diffMinutes/60);
			  
		  }  
		this.hoursperday = hoursperday;
	}
	
	public ArrayList<Date> getTalkTo() {
		return TalkTo;
	}
	
	
	public void setTalkTo(ArrayList<Date> talkTo) {
		this.TalkTo = talkTo;
	}
	
	
	public ArrayList<Date> getTalkFrom() {
		return TalkFrom;
	}
	
	
	public void setTalkFrom(ArrayList<Date> talkFrom) {
		this.TalkFrom = talkFrom;
	}


	@Override
	public String toString() {
		return "MobilePhoneUsage [hoursperday=" + hoursperday + ", TalkTo=" + TalkTo + ", TalkFrom=" + TalkFrom + "]";
	}
	
		

}
