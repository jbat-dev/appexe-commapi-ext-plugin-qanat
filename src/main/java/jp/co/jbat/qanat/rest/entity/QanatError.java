package jp.co.jbat.qanat.rest.entity;

import java.util.ArrayList;
import java.util.List;

public class QanatError {
	
	private String code;
	private List<String> codes = new ArrayList<String>();
	private String message;
	private List<String> messages = new ArrayList<String>();
	private String trace;
	
	public QanatError() {
		code = "";
		codes.clear();
		message = "";
		messages.clear();
		trace = "";
	}

	public final String getCode() {
		return code;
	}

	public final void setCode(String code) {
		this.code = code;
	}

	public final List<String> getCodes() {
		return codes;
	}

	public final void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public final List<String> getMessages() {
		return messages;
	}

	public final void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public final String getTrace() {
		return trace;
	}

	public final void setTrace(String trace) {
		this.trace = trace;
	}
	
}
