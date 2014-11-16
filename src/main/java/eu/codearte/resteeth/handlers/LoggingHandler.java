package eu.codearte.resteeth.handlers;

import eu.codearte.resteeth.annotation.LogScope;
import eu.codearte.resteeth.core.RestInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tomasz Nurkiewicz
 */
public class LoggingHandler implements RestInvocationHandler {

	private static final Logger log = LoggerFactory.getLogger(LoggingHandler.class);

	@Override
	public Object proceed(RestInvocation invocation) {
		LogScope loggingScope = invocation.getMetadata().getMethodAnnotationMetadata().getResteethAnnotationMetadata().getLoggingScope();
		if (loggingScope.ordinal() >= LogScope.INVOCATION_ONLY.ordinal()) {
			log.debug("Invoked {}, calling {} {}, variables: {}",
					invocation.getMethod(),
					invocation.getMetadata().getRequestMethod(),
					invocation.getMetadata().getMethodUrl(),
					invocation.getMetadata().getUrlVariables());
		}
		final Object result = invocation.proceed();
		if (loggingScope == LogScope.FULL) {
			log.trace("Response: {}", result);
		}
		return result;
	}

	@Override
	public int getOrder() {
		return 100;
	}
}
