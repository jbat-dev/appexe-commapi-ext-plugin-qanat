package com.jbat.http.rest.entity;

import java.io.Serializable;

import org.json.simple.JSONArray;

public class TableData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String table = "";
	private String column = "";
	private JSONArray j_array = null;
	private int numrecode = 0;
	
	private String user = "cvadmin";
	private String pass = "cvadmin";
	
	public TableData() {
	}
	
	public final String getTable() {
		return table;
	}
	public final void setTable(final String table) {
		this.table = table;
	}
	public final String getColumn() {
		return column;
	}
	public final void setColumn(final String column) {
		this.column = column;
	}
	public final JSONArray getJ_array() {
		return j_array;
	}
	public final void setJ_array(final JSONArray j_array) {
		this.j_array = j_array;
	}
	public final int getNumrecode() {
		return numrecode;
	}
	public final void setNumrecode(final int numrecode) {
		this.numrecode = numrecode;
	}
}