package eu.codearte.resteeth.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * @author Jakub Kubrynski
 */
public class MethodMetadata {

	private final String methodUrl;
	private final HttpMethod requestMethod;
	private final Class<?> returnType;
	private final HttpHeaders httpHeaders;
	private final MethodAnnotationMetadata methodAnnotationMetadata;
	private final ParameterMetadata parameterMetadata;

	public MethodMetadata(String methodUrl, HttpMethod requestMethod, Class<?> returnType, HttpHeaders httpHeaders,
	                      MethodAnnotationMetadata methodAnnotationMetadata, ParameterMetadata parameterMetadata) {
		this.methodUrl = methodUrl;
		this.requestMethod = requestMethod;
		this.returnType = returnType;
		this.parameterMetadata = parameterMetadata;
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

	public ParameterMetadata getParameterMetadata() {
		return parameterMetadata;
	}

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public MethodAnnotationMetadata getMethodAnnotationMetadata() {
		return methodAnnotationMetadata;
	}
}
