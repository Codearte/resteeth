package eu.codearte.resteeth.core;

import eu.codearte.resteeth.endpoint.EndpointProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

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

	private final MetadataExtractor metadataExtractor = new MetadataExtractor();

	public BeanProxyCreator(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Object createProxyBean(Class<?> beanClass, EndpointProvider endpointProvider) {
		LOG.info("Creating Resteeth bean for interface {}", beanClass.getCanonicalName());
		ProxyFactory proxyFactory = new ProxyFactory();

		Map<Method, MethodMetadata> methodMetadataMap = extractInterfaceInformation(beanClass);

		proxyFactory.addInterface(beanClass);

		RestTemplateInvoker restTemplateInvoker = new RestTemplateInvoker(restTemplate, endpointProvider, methodMetadataMap);

		proxyFactory.addAdvice(new RestClientMethodInterceptor(restTemplateInvoker));

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
