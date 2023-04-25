package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UserMobileNumbers implements java.io.Serializable {

	public List<String> mobileNumber = new ArrayList<String>();
}
