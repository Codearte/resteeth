package eu.codearte.resteeth.handlers;

import eu.codearte.resteeth.core.RestInvocation;
import org.springframework.core.Ordered;

/**
 * @author Tomasz Nurkiewicz
 */
public interface RestInvocationHandler extends Ordered {

	Object proceed(RestInvocation invocation);

	/**
	 * Higher priority (smaller value) will cause this handler to be executed earlier in the chain.
	 * Low priority (bigger value) will push handler to the end.
	 * Handler with lowest priority must handle request.
	 *
	 * @return Value controlling at what stage this handler will be called.
	 * @see {@link Ordered}
	 */
	@Override
	int getOrder();
}
