package eu.codearte.restofag.core;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;

/**
 * @author Jakub Kubrynski
 */
class MethodMetadata {

	private final String methodUrl;
	private final RequestMethod requestMethod;
	private final Class<?> returnType;
	private final Integer requestBody;
	private final HashMap<Integer, String> urlVariables;

	MethodMetadata(String methodUrl, RequestMethod requestMethod, Class<?> returnType, Integer requestBody, HashMap<Integer, String> urlVariables) {
		this.methodUrl = methodUrl;
		this.requestMethod = requestMethod;
		this.returnType = returnType;
		this.requestBody = requestBody;
		this.urlVariables = urlVariables;
	}

	String getMethodUrl() {
		return methodUrl;
	}

	RequestMethod getRequestMethod() {
		return requestMethod;
	}

	Class<?> getReturnType() {
		return returnType;
	}

	Integer getRequestBody() {
		return requestBody;
	}

	HashMap<Integer, String> getUrlVariables() {
		return urlVariables;
	}
}
