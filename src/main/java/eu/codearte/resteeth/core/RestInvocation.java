package eu.codearte.resteeth.core;

import eu.codearte.resteeth.handlers.RestInvocationHandler;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Tomasz Nurkiewicz
 */
public class RestInvocation {

	private final MethodMetadata metadata;
	private final List<RestInvocationHandler> handlers;
	private final Object[] arguments;
	private final Method method;

	public RestInvocation(Method method, Object[] arguments, MethodMetadata metadata, List<RestInvocationHandler> handlers) {
		this.method = method;
		this.arguments = arguments;
		this.metadata = metadata;
		this.handlers = handlers;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public MethodMetadata getMetadata() {
		return metadata;
	}

	public Object proceed() {
		return currentHandler().proceed(nextHandlers());
	}

	private RestInvocationHandler currentHandler() {
		return handlers.get(0);
	}

	private RestInvocation nextHandlers() {
		final List<RestInvocationHandler> withoutCurrent = handlers.subList(1, handlers.size());
		return new RestInvocation(method, arguments, metadata, withoutCurrent);
	}
}
