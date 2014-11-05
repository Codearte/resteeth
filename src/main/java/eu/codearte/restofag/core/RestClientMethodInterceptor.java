package eu.codearte.restofag.core;

import eu.codearte.restofag.endpoint.EndpointProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Jakub Kubrynski
 */
public class RestClientMethodInterceptor implements MethodInterceptor {

	private final RestTemplateInvoker restTemplateInvoker;
	private final EndpointProvider endpointProvider;

	public RestClientMethodInterceptor(RestTemplateInvoker restTemplateInvoker, EndpointProvider endpointProvider) {
		this.restTemplateInvoker = restTemplateInvoker;
		this.endpointProvider = endpointProvider;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return restTemplateInvoker.invokeRest(invocation, endpointProvider);
	}

}
