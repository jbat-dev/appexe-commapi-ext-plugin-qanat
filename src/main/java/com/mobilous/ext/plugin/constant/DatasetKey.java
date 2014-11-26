package com.mobilous.ext.plugin.constant;

/**
 * This defines the dataset keys that are recognised by the AppExe system.
 *
 * Please do not modify this file.
 *
 * @author yanto
 */
public enum DatasetKey {

	ACCESS_TOKEN("access_token"),
	AUTH_TYPE("auth_type"),
	AUTHORIZE_URL("authorize_url"),
	DATA("data"),
	OAUTH_VERIFIER("oauth_verifier"),
	REC("rec"),
	REQUEST_TOKEN("request_token"),
	SCHEMA("schema"),
	SERVICENAME("servicename"),
	TABLE("table"),
	USER_CONNECTION("user_connection"),
	WHERE("where"),
    FIELDS("fields"),   // add JBAT
    ORDER("order"),   // add JBAT
    LIMIT("limit"),   // add JBAT
    OFFSET("offset"),   // add JBAT
	NUMBEROFRECORD("numrec");
	
	private String key;

	DatasetKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
}
