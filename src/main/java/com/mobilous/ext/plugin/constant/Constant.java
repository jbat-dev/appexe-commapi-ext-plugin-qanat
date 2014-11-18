package com.mobilous.ext.plugin.constant;

/**
 * This defines the constants that are recognised by the AppExe system.
 *
 * Please do not modify this file.
 *
 * @author yanto
 */
public enum Constant {

	EMPTY_STRING(""),
	OAUTH1("OAuth1"),
	OAUTH2("OAuth2");

	private String value;

	Constant(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
