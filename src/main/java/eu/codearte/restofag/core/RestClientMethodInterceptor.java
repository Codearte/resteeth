package eu.codearte.restofag.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Jakub Kubrynski
 */
public class RestClientMethodInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		throw new UnsupportedOperationException();
	}
}
