package com.dpdocter.beans;

public class PainRatingScale {
private Integer NPRS;
private String onRest;
private String onActivity;
private String painAggrevatingFactor;
private String painReleavingFactor;
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
public String getPainAggrevatingFactor() {
	return painAggrevatingFactor;
}
public void setPainAggrevatingFactor(String painAggrevatingFactor) {
	this.painAggrevatingFactor = painAggrevatingFactor;
}
public String getPainReleavingFactor() {
	return painReleavingFactor;
}
public void setPainReleavingFactor(String painReleavingFactor) {
	this.painReleavingFactor = painReleavingFactor;
}



}
