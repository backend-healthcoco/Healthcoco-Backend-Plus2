package com.dpdocter.beans;

import java.util.List;

public class TeethExamination {

	private List<DMFTIndex> decayedDMFTIndex;
	private List<DMFTIndex> missingDMFTIndex;
	private List<DMFTIndex> filledDMFTIndex;
	private List<String> malocclusion;

	public List<DMFTIndex> getDecayedDMFTIndex() {
		return decayedDMFTIndex;
	}

	public void setDecayedDMFTIndex(List<DMFTIndex> decayedDMFTIndex) {
		this.decayedDMFTIndex = decayedDMFTIndex;
	}

	public List<DMFTIndex> getMissingDMFTIndex() {
		return missingDMFTIndex;
	}

	public void setMissingDMFTIndex(List<DMFTIndex> missingDMFTIndex) {
		this.missingDMFTIndex = missingDMFTIndex;
	}

	public List<DMFTIndex> getFilledDMFTIndex() {
		return filledDMFTIndex;
	}

	public void setFilledDMFTIndex(List<DMFTIndex> filledDMFTIndex) {
		this.filledDMFTIndex = filledDMFTIndex;
	}

	public List<String> getMalocclusion() {
		return malocclusion;
	}

	public void setMalocclusion(List<String> malocclusion) {
		this.malocclusion = malocclusion;
	}

	@Override
	public String toString() {
		return "TeethExamination [decayedDMFTIndex=" + decayedDMFTIndex + ", missingDMFTIndex=" + missingDMFTIndex
				+ ", filledDMFTIndex=" + filledDMFTIndex + ", malocclusion=" + malocclusion + ", getDecayedDMFTIndex()="
				+ getDecayedDMFTIndex() + ", getMissingDMFTIndex()=" + getMissingDMFTIndex() + ", getFilledDMFTIndex()="
				+ getFilledDMFTIndex() + ", getMalocclusion()=" + getMalocclusion() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
