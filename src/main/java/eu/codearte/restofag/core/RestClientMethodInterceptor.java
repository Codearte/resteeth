package eu.codearte.restofag.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Jakub Kubrynski
 */
public class RestClientMethodInterceptor implements MethodInterceptor {

	private final RestTemplateInvoker restTemplateInvoker;

	public RestClientMethodInterceptor(RestTemplateInvoker restTemplateInvoker) {
		this.restTemplateInvoker = restTemplateInvoker;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return restTemplateInvoker.invokeRest(invocation);
	}

}
