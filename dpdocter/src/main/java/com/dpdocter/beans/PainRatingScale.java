package com.dpdocter.beans;

import java.util.List;

public class PainRatingScale {
private Integer NPRS;
private String onRest;
private String onActivity;
private List<String> painAggrevatingFactor;
private List<String> painReleavingFactor;
public Integer getNPRS() {
	return NPRS;
}
public void setNPRS(Integer nPRS) {
	NPRS = nPRS;
}
public String getOnRest() {
	return onRest;
}
public void setOnRest(String onRest) {
	this.onRest = onRest;
}
public String getOnActivity() {
	return onActivity;
}
public void setOnActivity(String onActivity) {
	this.onActivity = onActivity;
}
public List<String> getPainAggrevatingFactor() {
	return painAggrevatingFactor;
}
public void setPainAggrevatingFactor(List<String> painAggrevatingFactor) {
	this.painAggrevatingFactor = painAggrevatingFactor;
}
public List<String> getPainReleavingFactor() {
	return painReleavingFactor;
}
public void setPainReleavingFactor(List<String> painReleavingFactor) {
	this.painReleavingFactor = painReleavingFactor;
}



}
