package eu.codearte.restofag.core;

import eu.codearte.restofag.endpoint.EndpointProvider;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jakub Kubrynski
 */
public class BeanProxyCreator {

	private final RestTemplateInvoker restTemplateInvoker;

	public BeanProxyCreator(RestTemplate restTemplate) {
		restTemplateInvoker = new RestTemplateInvoker(restTemplate);
	}

	public Object createProxyBean(Class<?> beanClass, EndpointProvider endpointProvider) {
		ProxyFactory proxyFactory = new ProxyFactory();

		proxyFactory.addInterface(beanClass);

		proxyFactory.addAdvice(new RestClientMethodInterceptor(restTemplateInvoker, endpointProvider));

		return proxyFactory.getProxy();
	}
}
