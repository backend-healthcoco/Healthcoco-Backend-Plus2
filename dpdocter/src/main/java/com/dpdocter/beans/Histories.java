package com.dpdocter.beans;

import com.dpdocter.response.HistoryDetailsResponse;

public class Histories {

	private HistoryDetailsResponse familyHistory;

	private BirthDetails birthDetails;

	private PersonalHistory personalHistory;

	private DrugsAndAllergies drugsAndAllergies;

	public HistoryDetailsResponse getFamilyHistory() {
		return familyHistory;
	}

	public void setFamilyHistory(HistoryDetailsResponse familyHistory) {
		this.familyHistory = familyHistory;
	}

	public BirthDetails getBirthDetails() {
		return birthDetails;
	}

	public void setBirthDetails(BirthDetails birthDetails) {
		this.birthDetails = birthDetails;
	}

	public PersonalHistory getPersonalHistory() {
		return personalHistory;
	}

	public void setPersonalHistory(PersonalHistory personalHistory) {
		this.personalHistory = personalHistory;
	}

	public DrugsAndAllergies getDrugsAndAllergies() {
		return drugsAndAllergies;
	}

	public void setDrugsAndAllergies(DrugsAndAllergies drugsAndAllergies) {
		this.drugsAndAllergies = drugsAndAllergies;
	}

	@Override
	public String toString() {
		return "Histories [familyHistory=" + familyHistory + ", birthDetails=" + birthDetails + ", personalHistory="
				+ personalHistory + ", drugsAndAllergies=" + drugsAndAllergies + "]";
	}

}
