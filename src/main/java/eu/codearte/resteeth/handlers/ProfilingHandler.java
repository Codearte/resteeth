package eu.codearte.resteeth.handlers;

import eu.codearte.resteeth.core.RestInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

/**
 * @author Tomasz Nurkiewicz
 */
public class ProfilingHandler implements RestInvocationHandler {

	private static final Logger log = LoggerFactory.getLogger(ProfilingHandler.class);

	@Override
	public Object proceed(RestInvocation invocation) {
		final long startTime = System.currentTimeMillis();
		final Object result = invocation.proceed();
		final long stopTime = System.currentTimeMillis();
		log.debug("Invocation of {} took {}ms", invocation.getMethod(), stopTime - startTime);
		return result;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 500;
	}
}
