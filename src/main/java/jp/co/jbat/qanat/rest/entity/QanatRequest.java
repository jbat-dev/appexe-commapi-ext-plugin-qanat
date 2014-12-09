package jp.co.jbat.qanat.rest.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QanatRequest implements Serializable{

	private static final long serialVersionUID = 2L;
	
	private List<QanatRequestMember> data = new ArrayList<>();
	
	public QanatRequest() {
		data.clear();
	}

	public final List<QanatRequestMember> getData() {
		return data;
	}

	public final void setData(List<QanatRequestMember> data) {
		this.data = data;
	}
	
}