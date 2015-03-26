package com.mobilous.ext.plugin.constant;

/**
 * This defines the dataset keys that are recognised by the AppExe system.
 *
 * Please do not modify this file.
 *
 * @author Mobilous
 */
public enum DatasetKey {

	ACCESS_TOKEN("access_token"),
	AUTH_TYPE("auth_type"),
	AUTHORIZE_URL("authorize_url"),
	AUTH_STATUS("auth_status"),
	DATA("data"),
	// error code for jbat request.
	ERROR_CODE("error_code"),
	// error message for jbat request.
	ERROR_MESSAGE("error_message"),
	NUMBEROFRECORD("numrec"),
	OAUTH_VERIFIER("oauth_verifier"),
	REC("rec"),
	REQUEST_TOKEN("request_token"),
	// return status for jbat return request.
	RETURN_STATUS("return_status"),
	SCHEMA("schema"),
	SERVICENAME("servicename"),
	TABLE("table"),
	USER_CONNECTION("user_connection"),
	WHERE("where");
	
	private String key;

	DatasetKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
}
