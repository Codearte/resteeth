package eu.codearte.resteeth.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
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
	private final HashMap<Integer, String> queryParameters;
	private final Integer pojoQueryParameter;
	private final HttpHeaders httpHeaders;
	private final MethodAnnotationMetadata methodAnnotationMetadata;

	public MethodMetadata(String methodUrl, HttpMethod requestMethod, Class<?> returnType, Integer requestBody,
	                      Map<Integer, String> urlVariables, HashMap<Integer, String> queryParameters,
	                      Integer pojoQueryParameter, HttpHeaders httpHeaders,
	                      MethodAnnotationMetadata methodAnnotationMetadata) {
		this.methodUrl = methodUrl;
		this.requestMethod = requestMethod;
		this.returnType = returnType;
		this.requestBody = requestBody;
		this.urlVariables = urlVariables;
		this.queryParameters = queryParameters;
		this.pojoQueryParameter = pojoQueryParameter;
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

	public HashMap<Integer, String> getQueryParameters() {
		return queryParameters;
	}

	public Integer getPojoQueryParameter() {
		return pojoQueryParameter;
	}

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public MethodAnnotationMetadata getMethodAnnotationMetadata() {
		return methodAnnotationMetadata;
	}
}
