package eu.codearte.resteeth.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

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
		if (AopUtils.isToStringMethod(invocation.getMethod())) {
			return "Proxy to " + restTemplateInvoker;
		}

		return restTemplateInvoker.invokeRest(invocation);
	}

}
