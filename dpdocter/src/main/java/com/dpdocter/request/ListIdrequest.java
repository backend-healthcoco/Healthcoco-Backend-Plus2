package com.dpdocter.request;

import java.util.List;

public class ListIdrequest {

	private List<String> ids;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	@Override
	public String toString() {
		return "ListIdrequest [ids=" + ids + "]";
	}
}
