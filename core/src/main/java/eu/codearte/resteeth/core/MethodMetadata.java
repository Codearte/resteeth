package eu.codearte.resteeth.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.HashMap;

/**
 * @author Jakub Kubrynski
 */
class MethodMetadata {

	private final String methodUrl;
	private final HttpMethod requestMethod;
	private final Class<?> returnType;
	private final Integer requestBody;
	private final HashMap<Integer, String> urlVariables;
	private final HttpHeaders httpHeaders;

	MethodMetadata(String methodUrl, HttpMethod requestMethod, Class<?> returnType, Integer requestBody,
								 HashMap<Integer, String> urlVariables, HttpHeaders httpHeaders) {
		this.methodUrl = methodUrl;
		this.requestMethod = requestMethod;
		this.returnType = returnType;
		this.requestBody = requestBody;
		this.urlVariables = urlVariables;
		this.httpHeaders = httpHeaders;
	}

	String getMethodUrl() {
		return methodUrl;
	}

	HttpMethod getRequestMethod() {
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

	HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}
}
