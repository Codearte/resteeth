package eu.codearte.resteeth.handlers;

import eu.codearte.resteeth.core.RestInvocation;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;

/**
 * @author Tomasz Nurkiewicz
 */
public class UserAgentHandler implements RestInvocationHandler {

	public final String userAgent;

	public UserAgentHandler() {
		this("Resteeth");
	}

	public UserAgentHandler(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public Object proceed(RestInvocation invocation) {
		invocation.getMetadata().getHttpHeaders().add(HttpHeaders.USER_AGENT, userAgent);
		return invocation.proceed();
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 1000;
	}
}
