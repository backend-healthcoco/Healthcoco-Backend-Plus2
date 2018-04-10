package com.dpdocter.beans;

public class OffDays {

	   private boolean sun = false;
       private boolean mon = false;
    	   private boolean tue = false;
    	   private boolean Wed = false;
       private boolean thu = false;
    	   private boolean fri = false;
    	   private boolean sat = false;
    	   
		public boolean isSun() {
			return sun;
		}
		public void setSun(boolean sun) {
			this.sun = sun;
		}
		public boolean isMon() {
			return mon;
		}
		public void setMon(boolean mon) {
			this.mon = mon;
		}
		public boolean isTue() {
			return tue;
		}
		public void setTue(boolean tue) {
			this.tue = tue;
		}
		public boolean isWed() {
			return Wed;
		}
		public void setWed(boolean wed) {
			Wed = wed;
		}
		public boolean isThu() {
			return thu;
		}
		public void setThu(boolean thu) {
			this.thu = thu;
		}
		public boolean isFri() {
			return fri;
		}
		public void setFri(boolean fri) {
			this.fri = fri;
		}
		public boolean isSat() {
			return sat;
		}
		public void setSat(boolean sat) {
			this.sat = sat;
		}
		
		
		@Override
		public String toString() {
			return "OffDays [sun=" + sun + ", mon=" + mon + ", tue=" + tue + ", Wed=" + Wed + ", thu=" + thu + ", fri="
					+ fri + ", sat=" + sat + "]";
		}
	
	
}
