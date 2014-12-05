package jp.co.jbat.qanat.rest.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Request implements Serializable{

	private static final long serialVersionUID = 2L;
	
	private List<RequestorMember> data = new ArrayList<>();
	
	public Request() {
		data.clear();
	}

	public final List<RequestorMember> getData() {
		return data;
	}

	public final void setData(List<RequestorMember> data) {
		this.data = data;
	}
	
}