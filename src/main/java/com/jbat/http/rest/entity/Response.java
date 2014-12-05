package com.jbat.http.rest.entity;

import java.util.ArrayList;
import java.util.List;

public class Response {
	private List<Object> data = new ArrayList<>();
	
	public Response() {
		data.clear();
	}

	public final List<Object> getData() {
		return data;
	}

	public final void setData(List<Object> data) {
		this.data = data;
	}
}
