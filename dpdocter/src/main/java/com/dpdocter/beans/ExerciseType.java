package com.dpdocter.beans;

public class ExerciseType {
	
	private Boolean gym=false;
	private Boolean yoga=false;
	private Boolean walking=false;
	private Boolean running=false;
	private Boolean briskWalk=false;
	private Boolean cycling=false;
	private Boolean weightLifting=false;
	private Boolean swimming=false;
	
	
	public Boolean getGym() {
		return gym;
	}
	public void setGym(Boolean gym) {
		this.gym = gym;
	}
	public Boolean getYoga() {
		return yoga;
	}
	public void setYoga(Boolean yoga) {
		this.yoga = yoga;
	}
	public Boolean getWalking() {
		return walking;
	}
	public void setWalking(Boolean walking) {
		this.walking = walking;
	}
	public Boolean getRunning() {
		return running;
	}
	public void setRunning(Boolean running) {
		this.running = running;
	}
	public Boolean getBriskWalk() {
		return briskWalk;
	}
	public void setBriskWalk(Boolean briskWalk) {
		this.briskWalk = briskWalk;
	}
	public Boolean getCycling() {
		return cycling;
	}
	public void setCycling(Boolean cycling) {
		this.cycling = cycling;
	}
	public Boolean getWeightLifting() {
		return weightLifting;
	}
	public void setWeightLifting(Boolean weightLifting) {
		this.weightLifting = weightLifting;
	}
	public Boolean getSwimming() {
		return swimming;
	}
	public void setSwimming(Boolean swimming) {
		this.swimming = swimming;
	}
	
	
	
	@Override
	public String toString() {
		return "ExerciseType [gym=" + gym + ", yoga=" + yoga + ", walking=" + walking + ", running=" + running
				+ ", briskWalk=" + briskWalk + ", cycling=" + cycling + ", weightLifting=" + weightLifting
				+ ", swimming=" + swimming + "]";
	}
	
	

}
