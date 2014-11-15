package eu.codearte.resteeth.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.HashMap;

/**
 * @author Jakub Kubrynski
 */
public class MethodMetadata {

	private final String methodUrl;
	private final HttpMethod requestMethod;
	private final Class<?> returnType;
	private final Integer requestBody;
	private final HashMap<Integer, String> urlVariables;
	private final HttpHeaders httpHeaders;

	public MethodMetadata(String methodUrl, HttpMethod requestMethod, Class<?> returnType, Integer requestBody,
								 HashMap<Integer, String> urlVariables, HttpHeaders httpHeaders) {
		this.methodUrl = methodUrl;
		this.requestMethod = requestMethod;
		this.returnType = returnType;
		this.requestBody = requestBody;
		this.urlVariables = urlVariables;
		this.httpHeaders = httpHeaders;
	}

	public String getMethodUrl() {
		return methodUrl;
	}

	public HttpMethod getRequestMethod() {
		return requestMethod;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public Integer getRequestBody() {
		return requestBody;
	}

	public HashMap<Integer, String> getUrlVariables() {
		return urlVariables;
	}

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}
}
