package eu.codearte.resteeth.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @author Jakub Kubrynski
 */
public class MethodMetadata {

	private final String methodUrl;
	private final HttpMethod requestMethod;
	private final Class<?> returnType;
	private final Integer requestBody;
	private final Map<Integer, String> urlVariables;
	private final HttpHeaders httpHeaders;
	private final MethodAnnotationMetadata methodAnnotationMetadata;

	public MethodMetadata(String methodUrl, HttpMethod requestMethod, Class<?> returnType, Integer requestBody,
	                      Map<Integer, String> urlVariables, HttpHeaders httpHeaders,
	                      MethodAnnotationMetadata methodAnnotationMetadata) {
		this.methodUrl = methodUrl;
		this.requestMethod = requestMethod;
		this.returnType = returnType;
		this.requestBody = requestBody;
		this.urlVariables = urlVariables;
		this.httpHeaders = httpHeaders;
		this.methodAnnotationMetadata = methodAnnotationMetadata;
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

	public Map<Integer, String> getUrlVariables() {
		return urlVariables;
	}

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public MethodAnnotationMetadata getMethodAnnotationMetadata() {
		return methodAnnotationMetadata;
	}
}
