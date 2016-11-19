package com.dpdocter.beans;

public class PersonalHistory {

	private String diet;
	private String addictions;
	private String bowelHabit;
	private String bladderHabit;

	public String getDiet() {
		return diet;
	}

	public void setDiet(String diet) {
		this.diet = diet;
	}

	public String getAddictions() {
		return addictions;
	}

	public void setAddictions(String addictions) {
		this.addictions = addictions;
	}

	public String getBowelHabit() {
		return bowelHabit;
	}

	public void setBowelHabit(String bowelHabit) {
		this.bowelHabit = bowelHabit;
	}

	public String getBladderHabit() {
		return bladderHabit;
	}

	public void setBladderHabit(String bladderHabit) {
		this.bladderHabit = bladderHabit;
	}

	@Override
	public String toString() {
		return "PersonalHistory [diet=" + diet + ", addictions=" + addictions + ", bowelHabit=" + bowelHabit
				+ ", bladderHabit=" + bladderHabit + "]";
	}

}
