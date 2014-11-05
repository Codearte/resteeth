package eu.codearte.restofag.core;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;

/**
 * @author Jakub Kubrynski
 */
public class MethodMetadata {

	private final String methodUrl;
	private final RequestMethod requestMethod;
	private final Class<?> returnType;
	private final Integer requestBody;
	private final HashMap<Integer, String> urlVariables;

	public MethodMetadata(String methodUrl, RequestMethod requestMethod, Class<?> returnType, Integer requestBody, HashMap<Integer, String> urlVariables) {
		this.methodUrl = methodUrl;
		this.requestMethod = requestMethod;
		this.returnType = returnType;
		this.requestBody = requestBody;
		this.urlVariables = urlVariables;
	}

	public String getMethodUrl() {
		return methodUrl;
	}

	public RequestMethod getRequestMethod() {
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
}
