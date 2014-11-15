package eu.codearte.resteeth.core;

import eu.codearte.resteeth.endpoint.EndpointProvider;
import eu.codearte.resteeth.handlers.RestInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.OrderComparator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Kubrynski
 * @author Tomasz Nurkiewicz
 */
public class BeanProxyCreator {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final RestTemplate restTemplate;

	private final MetadataExtractor metadataExtractor = new MetadataExtractor();
	private final List<RestInvocationHandler> handlers;

	public BeanProxyCreator(RestTemplate restTemplate, Collection<RestInvocationHandler> handlers) {
		this.restTemplate = restTemplate;
		this.handlers = new ArrayList<>(handlers);
		OrderComparator.sort(this.handlers);
		LOG.debug("Custom handlers: {}", this.handlers);
	}

	public Object createProxyBean(Class<?> beanClass, EndpointProvider endpointProvider) {
		LOG.info("Creating Resteeth bean for interface {}", beanClass.getCanonicalName());
		final RestInvocationInterceptor interceptor = buildInvocationHandler(beanClass, endpointProvider);
		return buildProxy(beanClass, interceptor);
	}

	private RestInvocationInterceptor buildInvocationHandler(Class<?> beanClass, EndpointProvider endpointProvider) {
		final Map<Method, MethodMetadata> methodMetadataMap = extractInterfaceInformation(beanClass);
		final List<RestInvocationHandler> handlersList = prepareHandlersList(endpointProvider);
		return new RestInvocationInterceptor(methodMetadataMap, handlersList);
	}

	private List<RestInvocationHandler> prepareHandlersList(EndpointProvider endpointProvider) {
		final List<RestInvocationHandler> handlersList = new ArrayList<>(this.handlers);
		handlersList.add(new RestTemplateInvoker(restTemplate, endpointProvider));
		return handlersList;
	}

	private Object buildProxy(Class<?> beanClass, RestInvocationInterceptor invocation) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.addInterface(beanClass);
		proxyFactory.addAdvice(invocation);
		return proxyFactory.getProxy();
	}

	private Map<Method, MethodMetadata> extractInterfaceInformation(Class<?> beanClass) {
		Map<Method, MethodMetadata> methodMetadataMap = new HashMap<>();
		RequestMapping controllerRequestMapping = beanClass.getAnnotation(RequestMapping.class);
		for (Method method : beanClass.getMethods()) {
			methodMetadataMap.put(method, metadataExtractor.extractMethodMetadata(method, controllerRequestMapping));
		}
		return methodMetadataMap;
	}

}
