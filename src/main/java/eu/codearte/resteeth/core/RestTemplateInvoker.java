package eu.codearte.resteeth.core;

import eu.codearte.resteeth.endpoint.EndpointProvider;
import eu.codearte.resteeth.handlers.RestInvocationHandler;
import eu.codearte.resteeth.util.SpringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
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
		Map<String, ?> urlVariablesValues = buildArgumentsMap(methodMetadata.getParameterMetadata().getUrlVariables(), invocation.getArguments());

		String requestUrl = endpointProvider.getEndpoint() + methodMetadata.getMethodUrl();

		requestUrl = appendAnnotatedQueryParameters(requestUrl, methodMetadata.getParameterMetadata().getQueryParameters(), invocation.getArguments());
		requestUrl = appendPojoQueryParameters(requestUrl, methodMetadata.getParameterMetadata().getPojoQueryIndex(), invocation.getArguments());

		@SuppressWarnings("unchecked")
		HttpEntity entity = new HttpEntity(
				extractRequestBody(methodMetadata.getParameterMetadata().getRequestBodyIndex(), invocation.getArguments()), methodMetadata.getHttpHeaders());

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

		for (Integer paramIndex : urlVariables.keySet()) {
			stringHashMap.put(urlVariables.get(paramIndex), arguments[paramIndex]);
		}

		return stringHashMap;
	}

	private String appendPojoQueryParameters(String requestUrl, Integer pojoQueryParameter, Object[] arguments) {
		if (pojoQueryParameter == null) {
			return requestUrl;
		}
		Map<String, Object> queryParamsMap = new HashMap<>();
		Object pojoParameterValue = arguments[pojoQueryParameter];
		Field[] fields = pojoParameterValue.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (!field.isSynthetic()) {
				try {
					String property = BeanUtils.getProperty(pojoParameterValue, field.getName());
					if (property != null) {
						queryParamsMap.put(field.getName(), property);
					}
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException("Cannot do reflection magic on " + pojoParameterValue.getClass(), e);
				}
			}
		}
		return appendQueryParameters(requestUrl, queryParamsMap);
	}

	private String appendAnnotatedQueryParameters(String requestUrl, Map<Integer, String> queryParameters, Object[] arguments) {
		return appendQueryParameters(requestUrl, buildArgumentsMap(queryParameters, arguments));
	}

	private String appendQueryParameters(String requestUrl, Map<String, ?> queryParamsMap) {
		if (queryParamsMap.isEmpty()) {
			return requestUrl;
		}

		StringBuilder urlBuilder = new StringBuilder(requestUrl);

		if (urlBuilder.indexOf("?") == -1) {
			urlBuilder.append("?");
		}

		boolean isFirst = true;

		for (Map.Entry<String, ?> entry : queryParamsMap.entrySet()) {
			if (isFirst) {
				isFirst = false;
			} else {
				urlBuilder.append("&");
			}
			urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
		}

		return urlBuilder.toString();
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
