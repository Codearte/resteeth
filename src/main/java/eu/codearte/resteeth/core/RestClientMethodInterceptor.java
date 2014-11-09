package eu.codearte.resteeth.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Jakub Kubrynski
 */
class RestClientMethodInterceptor implements MethodInterceptor {

	private final RestTemplateInvoker restTemplateInvoker;

	RestClientMethodInterceptor(RestTemplateInvoker restTemplateInvoker) {
		this.restTemplateInvoker = restTemplateInvoker;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return restTemplateInvoker.invokeRest(invocation);
	}

}
