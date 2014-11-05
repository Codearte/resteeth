package eu.codearte.restofag.core;

import eu.codearte.restofag.endpoint.EndpointProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Kubrynski
 */
public class BeanProxyCreator {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final RestTemplate restTemplate;

	public BeanProxyCreator(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Object createProxyBean(Class<?> beanClass, EndpointProvider endpointProvider) {
		ProxyFactory proxyFactory = new ProxyFactory();

		Map<Method, MethodMetadata> methodMetadataMap = extractInterfaceInformation(beanClass);

		proxyFactory.addInterface(beanClass);

		RestTemplateInvoker restTemplateInvoker = new RestTemplateInvoker(restTemplate, endpointProvider, methodMetadataMap);

		proxyFactory.addAdvice(new RestClientMethodInterceptor(restTemplateInvoker));

		return proxyFactory.getProxy();
	}

	private Map<Method, MethodMetadata> extractInterfaceInformation(Class<?> beanClass) {
		Map<Method, MethodMetadata> methodMetadataMap = new HashMap<>();
		RequestMapping requestMapping = beanClass.getAnnotation(RequestMapping.class);
		String controllerUrl = extractUrl(requestMapping);
		for (Method method : beanClass.getMethods()) {
			methodMetadataMap.put(method, extractMethodMetadata(method, controllerUrl));
		}
		return methodMetadataMap;
	}

	private MethodMetadata extractMethodMetadata(Method method, String controllerUrl) {
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

		String methodUrl = controllerUrl + extractUrl(requestMapping);

		RequestMethod requestMethod = extractRequestMethod(requestMapping);

		Class<?> returnType = method.getReturnType();

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

		return new MethodMetadata(methodUrl, requestMethod, returnType, requestBody, urlVariables);
	}

	private String extractUrl(RequestMapping requestMapping) {
		String foundUrl = "";
		if (requestMapping == null) {
			return foundUrl;
		}

		String[] urls = requestMapping.value();
		if (urls != null && urls.length > 0) {
			foundUrl = urls[0];
			if (urls.length > 1) {
				LOG.warn("Found more than one URL mapping. Using first specified: {}", foundUrl);
			}
		}
		return foundUrl;
	}

	private RequestMethod extractRequestMethod(RequestMapping requestMapping) {
		RequestMethod[] requestMethods = requestMapping.method();
		if (requestMethods == null || requestMethods.length == 0) {
			LOG.warn("No request mapping requestMethods found");
			throw new IllegalStateException("No request mapping specified!");
		} else if (requestMethods.length > 1) {
			LOG.warn("More than one request method found. Using first specified");
		}
		return requestMethods[0];
	}
}
