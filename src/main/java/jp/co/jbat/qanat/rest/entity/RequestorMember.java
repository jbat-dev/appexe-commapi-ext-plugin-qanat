package jp.co.jbat.qanat.rest.entity;

import java.io.Serializable;

public class RequestorMember implements Serializable {
	

	private static final long serialVersionUID = 3L;

	private String tablename;
	private String user;
	private String pass;
	private String[] variable;
	private String utype;

	public RequestorMember(){
		tablename = null;
		user = "";
		pass = "";
		utype = "310"; //default
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public final String getUser() {
		return user;
	}

	public final void setUser(String user) {
		this.user = user;
	}

	public final String getPass() {
		return pass;
	}

	public final void setPass(String pass) {
		this.pass = pass;
	}

	public String[] getVariable() {
		return variable;
	}

	public void setVariable(String[] variable) {
		this.variable = variable;
	}

	public final String getUtype() {
		return utype;
	}

	public final void setUtype(String utype) {
		this.utype = utype;
	}
	
	
}
