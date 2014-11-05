package eu.codearte.restofag.core;

import eu.codearte.restofag.endpoint.EndpointProvider;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Kubrynski
 */
public class RestTemplateInvoker {

	private final RestTemplate restTemplate;
	private final EndpointProvider endpointProvider;
	private final Map<Method, MethodMetadata> methodMetadataMap;

	public RestTemplateInvoker(RestTemplate restTemplate, EndpointProvider endpointProvider, Map<Method, MethodMetadata> methodMetadataMap) {
		this.restTemplate = restTemplate;
		this.endpointProvider = endpointProvider;
		this.methodMetadataMap = methodMetadataMap;
	}

	Object invokeRest(MethodInvocation invocation) {
		MethodMetadata methodMetadata = methodMetadataMap.get(invocation.getMethod());
		Map<String, ?> urlVariablesValues = buildArgumentsMap(methodMetadata.getUrlVariables(), invocation.getArguments());

		String requestUrl = endpointProvider.getEndpoint() + methodMetadata.getMethodUrl();

		switch (methodMetadata.getRequestMethod()) {
			case GET:
				if (ResponseEntity.class.isAssignableFrom(methodMetadata.getReturnType())) {
					return restTemplate.getForEntity(requestUrl, methodMetadata.getReturnType(), urlVariablesValues);
				} else {
					return restTemplate.getForObject(requestUrl, methodMetadata.getReturnType(), urlVariablesValues);
				}
			case POST:
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				@SuppressWarnings("unchecked")
				HttpEntity entity = new HttpEntity(extractRequestBody(methodMetadata.getRequestBody(), invocation.getArguments()), headers);
				// if 201 then post for location
				return restTemplate.postForLocation(requestUrl, entity, urlVariablesValues);
			default:
				throw new IllegalStateException("Uuups");
		}
	}

	private Object extractRequestBody(Integer requestBody, Object[] arguments) {
		return arguments[requestBody];
	}

	private Map<String, ?> buildArgumentsMap(HashMap<Integer, String> urlVariables, Object[] arguments) {
		HashMap<String, Object> stringHashMap = new HashMap<>();

		for (int i = 0; i < arguments.length; i++) {
			stringHashMap.put(urlVariables.get(i), arguments[i]);
		}

		return stringHashMap;
	}
}
