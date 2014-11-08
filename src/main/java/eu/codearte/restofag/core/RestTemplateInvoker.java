package eu.codearte.restofag.core;

import eu.codearte.restofag.endpoint.EndpointProvider;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Kubrynski
 */
class RestTemplateInvoker {

	private final RestTemplate restTemplate;
	private final EndpointProvider endpointProvider;
	private final Map<Method, MethodMetadata> methodMetadataMap;

	RestTemplateInvoker(RestTemplate restTemplate, EndpointProvider endpointProvider, Map<Method, MethodMetadata> methodMetadataMap) {
		this.restTemplate = restTemplate;
		this.endpointProvider = endpointProvider;
		this.methodMetadataMap = methodMetadataMap;
	}

	Object invokeRest(MethodInvocation invocation) {
		MethodMetadata methodMetadata = methodMetadataMap.get(invocation.getMethod());
		Map<String, ?> urlVariablesValues = buildArgumentsMap(methodMetadata.getUrlVariables(), invocation.getArguments());

		String requestUrl = endpointProvider.getEndpoint() + methodMetadata.getMethodUrl();

		@SuppressWarnings("unchecked")
		HttpEntity entity = new HttpEntity(
				extractRequestBody(methodMetadata.getRequestBody(), invocation.getArguments()), methodMetadata.getHttpHeaders());

		ResponseEntity<?> exchange = restTemplate.exchange(requestUrl, methodMetadata.getRequestMethod(), entity,
				methodMetadata.getReturnType(), urlVariablesValues);

		return exchange.getBody();
	}

	private Object extractRequestBody(Integer requestBody, Object[] arguments) {
		if (requestBody != null && arguments != null && arguments.length >= requestBody) {
			return arguments[requestBody];
		}
		return null;
	}

	private Map<String, ?> buildArgumentsMap(HashMap<Integer, String> urlVariables, Object[] arguments) {
		Map<String, Object> stringHashMap = new HashMap<>();

		for (int i = 0; i < arguments.length; i++) {
			stringHashMap.put(urlVariables.get(i), arguments[i]);
		}

		return stringHashMap;
	}
}
