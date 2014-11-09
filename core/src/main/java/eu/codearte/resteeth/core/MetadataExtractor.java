package eu.codearte.resteeth.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Jakub Kubrynski
 */
class MetadataExtractor {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	MethodMetadata extractMethodMetadata(Method method, RequestMapping controllerRequestMapping) {
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

		String methodUrl = extractUrl(requestMapping, controllerRequestMapping);

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Integer requestBody = null;
		HashMap<Integer, String> urlVariables = new HashMap<>();
		if (parameterAnnotations != null && parameterAnnotations.length > 0) {
			for (int i = 0; i < parameterAnnotations.length; i++) {
				for (Annotation parameterAnnotation : parameterAnnotations[i]) {
					if (PathVariable.class.isAssignableFrom(parameterAnnotation.getClass())) {
						urlVariables.put(i, ((PathVariable) parameterAnnotation).value());
					}
					if (RequestBody.class.isAssignableFrom(parameterAnnotation.getClass())) {
						requestBody = i;
					}
				}
			}
		}

		return new MethodMetadata(methodUrl,
				extractRequestMethod(requestMapping, controllerRequestMapping),
				extractReturnType(method),
				requestBody,
				urlVariables,
				extractHeaders(requestMapping, controllerRequestMapping));
	}

	private Class<?> extractReturnType(Method method) {
		return method.getReturnType() == void.class ? Void.class : method.getReturnType();
	}

	private HttpHeaders extractHeaders(RequestMapping requestMapping, RequestMapping controllerRequestMapping) {
		HttpHeaders headers = new HttpHeaders();

		String[] consumes = requestMapping.consumes();
		if (consumes.length == 0 && controllerRequestMapping != null) {
			consumes = controllerRequestMapping.consumes();
		}

		if (consumes.length > 0) {
			headers.setContentType(MediaType.valueOf(consumes[0]));
		}

		String[] produces = requestMapping.produces();
		if (produces.length == 0 && controllerRequestMapping != null) {
			produces = controllerRequestMapping.produces();
		}

		if (produces.length > 0) {
			ArrayList<MediaType> acceptableMediaTypes = new ArrayList<>();
			for (String acceptType : produces) {
				acceptableMediaTypes.add(MediaType.valueOf(acceptType));
			}
			headers.setAccept(acceptableMediaTypes);
		}

		return headers;
	}

	private String extractUrl(RequestMapping methodMapping, RequestMapping controllerMapping) {
		String foundUrl = "";

		String[] controllerValues = controllerMapping != null ? controllerMapping.value() : new String[0];
		String[] methodValues = methodMapping.value();

		if (methodValues.length == 0 && controllerValues.length == 0) {
			throw new IncorrectRequestMapping("No request url found!");
		}

		if (controllerValues.length > 0) {
			foundUrl += controllerValues[0];
			if (controllerValues.length > 1) {
				LOG.warn("Found more than one controller URL mapping. Using first specified: {}", foundUrl);
			}
		}

		if (methodValues.length > 0) {
			foundUrl += methodValues[0];
			if (methodValues.length > 1) {
				LOG.warn("Found more than one URL mapping. Using first specified: {}", foundUrl);
			}
		}
		return foundUrl;
	}

	private HttpMethod extractRequestMethod(RequestMapping requestMapping, RequestMapping controllerRequestMapping) {
		RequestMethod[] requestMethods = requestMapping.method();
		if (requestMethods == null || requestMethods.length == 0) {
			if (controllerRequestMapping == null ||
					controllerRequestMapping.method() == null ||
					controllerRequestMapping.method().length == 0) {
				LOG.warn("No request mapping requestMethods found");
				throw new IncorrectRequestMapping("No requestMethods specified!");
			} else {
				requestMethods = controllerRequestMapping.method();
			}
		} else if (requestMethods.length > 1) {
			LOG.warn("More than one request method found. Using first specified");
		}
		return HttpMethod.valueOf(requestMethods[0].name());
	}

}
