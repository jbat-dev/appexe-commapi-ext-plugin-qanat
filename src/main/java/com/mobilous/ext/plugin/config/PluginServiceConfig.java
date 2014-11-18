package com.mobilous.ext.plugin.config;

import java.text.MessageFormat;

/**
 * This is where you define the constants.
 *
 * @author yanto
 */
public enum PluginServiceConfig {

	/*
	 * NOTE: DO NOT MODIFY the callback URL!
	 */
	APPEXE_CALLBACK_URL("https://{0}:8181/commapi/extsvc/authenticate?servicename={1}"),

	// TODO Please set the correct values accordingly.
	MY_DOMAIN_NAME("www.jbsw.co.jp"),
	MY_SERVICE_NAME("qanat"),
	MY_API_KEY("api_key"),
	MY_SECRET_KEY("secret_key"),
	MY_API_USERNAME("api_username"),
	MY_API_PASSWORD("api_upassword");

	public static final String MY_CALLBACK_URL = MessageFormat.format(PluginServiceConfig.APPEXE_CALLBACK_URL.getValue(), PluginServiceConfig.MY_DOMAIN_NAME.getValue(), PluginServiceConfig.MY_SERVICE_NAME.getValue());

	private String value;

	PluginServiceConfig(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
