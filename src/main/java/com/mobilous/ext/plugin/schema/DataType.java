package com.mobilous.ext.plugin.schema;

/**
 * This defines the data types that are recognised by the AppExe system.
 *
 * Please do not modify this file.
 *
 * @author yanto
 */
public enum DataType {

	BOOLEAN("boolean"),
	DATE("date"),
	INTEGER("integer"),
	REAL("real"),
	TEXT("text");

	private String value;

	DataType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
