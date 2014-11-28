package eu.codearte.resteeth.core;

import java.util.Map;

public class ParameterMetadata {

	private final Integer requestBodyIndex;
	private final Map<Integer, String> urlVariables;
	private final Map<Integer, String> queryParameters;
	private final Integer pojoQueryIndex;

	public ParameterMetadata(Integer requestBody, Map<Integer, String> urlVariables,
	                         Map<Integer, String> queryParameters, Integer pojoQueryParameter) {
		this.requestBodyIndex = requestBody;
		this.urlVariables = urlVariables;
		this.queryParameters = queryParameters;
		this.pojoQueryIndex = pojoQueryParameter;
	}

	public Integer getRequestBodyIndex() {
		return requestBodyIndex;
	}

	public Map<Integer, String> getUrlVariables() {
		return urlVariables;
	}

	public Map<Integer, String> getQueryParameters() {
		return queryParameters;
	}

	public Integer getPojoQueryIndex() {
		return pojoQueryIndex;
	}
}