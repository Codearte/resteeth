package eu.codearte.resteeth.core;

import eu.codearte.resteeth.handlers.RestInvocationHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author Tomasz Nurkiewicz
 */
class RestInvocationInterceptor implements MethodInterceptor {
	private final Map<Method, MethodMetadata> methodMetadataMap;
	private final List<RestInvocationHandler> handlers;

	public RestInvocationInterceptor(Map<Method, MethodMetadata> methodMetadataMap, List<RestInvocationHandler> handlers) {
		this.methodMetadataMap = methodMetadataMap;
		this.handlers = handlers;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (AopUtils.isToStringMethod(invocation.getMethod())) {
			return "Proxy to " + handlers;
		}
		final MethodMetadata metadata = methodMetadataMap.get(invocation.getMethod());
		final RestInvocation restInvocation = new RestInvocation(invocation.getMethod(), invocation.getArguments(), metadata, handlers);
		return restInvocation.proceed();
	}
}
