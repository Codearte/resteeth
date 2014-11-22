package eu.codearte.resteeth.core;

import eu.codearte.resteeth.endpoint.EndpointProvider;
import eu.codearte.resteeth.handlers.RestInvocationHandler;
import eu.codearte.resteeth.util.SpringUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Kubrynski
 */
class RestTemplateInvoker implements RestInvocationHandler {

	private final RestTemplate restTemplate;
	private final EndpointProvider endpointProvider;

	RestTemplateInvoker(RestTemplate restTemplate, EndpointProvider endpointProvider) {
		this.restTemplate = restTemplate;
		this.endpointProvider = endpointProvider;
	}

	@Override
	public Object proceed(RestInvocation invocation) {
		MethodMetadata methodMetadata = invocation.getMetadata();
		Map<String, ?> urlVariablesValues = buildArgumentsMap(methodMetadata.getUrlVariables(), invocation.getArguments());

		String requestUrl = endpointProvider.getEndpoint() + methodMetadata.getMethodUrl();

		@SuppressWarnings("unchecked")
		HttpEntity entity = new HttpEntity(
				extractRequestBody(methodMetadata.getRequestBody(), invocation.getArguments()), methodMetadata.getHttpHeaders());

		Class responseType;
		boolean returnsResponseEntity = ResponseEntity.class.isAssignableFrom(methodMetadata.getReturnType());
		if (returnsResponseEntity) {
			responseType = SpringUtils.getGenericType(invocation.getMethod().getGenericReturnType());
		} else {
			responseType = methodMetadata.getReturnType();
		}

		@SuppressWarnings("unchecked")
		ResponseEntity<?> exchange = restTemplate.exchange(requestUrl, methodMetadata.getRequestMethod(), entity,
				responseType, urlVariablesValues);

		return returnsResponseEntity ? exchange : exchange.getBody();
	}

	private Object extractRequestBody(Integer requestBody, Object[] arguments) {
		if (requestBody != null && arguments != null && arguments.length >= requestBody) {
			return arguments[requestBody];
		}
		return null;
	}

	private Map<String, ?> buildArgumentsMap(Map<Integer, String> urlVariables, Object[] arguments) {
		Map<String, Object> stringHashMap = new HashMap<>();

		for (int i = 0; i < arguments.length; i++) {
			stringHashMap.put(urlVariables.get(i), arguments[i]);
		}

		return stringHashMap;
	}

	@Override
	public String toString() {
		return "RestTemplateInvoker(" + "restTemplate=" + restTemplate +
				", endpointProvider=" + endpointProvider + ')';
	}

	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
