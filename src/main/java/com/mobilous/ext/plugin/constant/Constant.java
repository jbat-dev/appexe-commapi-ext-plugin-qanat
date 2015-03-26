package com.mobilous.ext.plugin.constant;

/**
 * This defines the constants that are recognised by the AppExe system.
 *
 * Please do not modify this file.
 *
 * @author Mobilous
 */
public enum Constant {

	EMPTY_STRING(""),
	OAUTH1("OAuth1"),
	OAUTH2("OAuth2"),
	CUSTOM("custom"),
	AUTH_STATUS_VALID("valid"),
	AUTH_STATUS_INVALID("invalid"),
	AUTH_STATUS_REJECTED("rejected"),
	RET_STATUS_VALID("valid"),
	RET_STATUS_INVALID("invalid"),
	RET_STATUS_REJECTED("rejected");
	
	private String value;

	Constant(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
