package eu.codearte.restofag.core;

import eu.codearte.restofag.endpoint.EndpointProvider;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * @author Jakub Kubrynski
 */
public class RestTemplateInvoker {

	private final RestTemplate restTemplate;

	public RestTemplateInvoker(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	Object invokeRest(MethodInvocation invocation, EndpointProvider endpointProvider) {
		RequestMapping requestMapping = invocation.getMethod().getAnnotation(RequestMapping.class);
		String[] urls = requestMapping.value();

		if (urls == null) {
			// read mapping from type annotation
			throw new IllegalArgumentException("No mapping specified");
		}

		RequestMethod[] methods = requestMapping.method();
		if (methods == null || methods.length == 0) {
			throw new IllegalArgumentException("No request method specified");
		}

		RequestMethod method = methods[0];
		Class<?> returnType = invocation.getMethod().getReturnType();

		Parameter[] parameters = invocation.getMethod().getParameters();
		Object[] arguments = invocation.getArguments();
		Object requestBody = null;
		HashMap<String, Object> urlVariables = new HashMap<>();
		if (parameters != null && parameters.length > 0) {
			for (int i = 0; i < parameters.length; i++) {
				PathVariable pathVariable = parameters[i].getDeclaredAnnotation(PathVariable.class);
				if (pathVariable != null) {
					urlVariables.put(pathVariable.value(), arguments[i]);
				}
				if (parameters[i].getDeclaredAnnotation(RequestBody.class) != null) {
					requestBody = arguments[i];
				}
			}
		}

		String url = endpointProvider.getEndpoint() + urls[0];
		switch (method) {
			case GET:
				if (ResponseEntity.class.isAssignableFrom(returnType)) {
					return restTemplate.getForEntity(url, returnType, urlVariables);
				} else {
					return restTemplate.getForObject(url, returnType, urlVariables);
				}
			case POST:
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				HttpEntity entity = new HttpEntity(requestBody, headers);
				// if 201 then post for location
				return restTemplate.postForLocation(url, entity, urlVariables);
			default:
				throw new IllegalStateException("Uuups");
		}
	}
}
