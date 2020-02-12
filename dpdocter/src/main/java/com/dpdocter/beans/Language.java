package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Language extends GenericCollection {
 
 private String id;
 private String name;
 private Boolean discarded=Boolean.FALSE;

 public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public Boolean getDiscarded() {
	return discarded;
}
public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
}
@Override
public String toString() {
	return "Language [id=" + id + ", name=" + name + ", discarded=" + discarded + "]";
}

}
