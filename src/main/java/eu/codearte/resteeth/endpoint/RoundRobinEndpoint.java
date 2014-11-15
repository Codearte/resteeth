package eu.codearte.resteeth.endpoint;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jakub Kubrynski
 */
class RoundRobinEndpoint implements EndpointProvider {

	private final String[] endpointUrls;
	private final AtomicInteger counter = new AtomicInteger();

	RoundRobinEndpoint(String[] endpointUrls) {
		this.endpointUrls = endpointUrls;
	}

	@Override
	public String getEndpoint() {
		return endpointUrls[Math.abs(counter.getAndIncrement()) % endpointUrls.length];
	}

	@Override
	public String toString() {
		return Arrays.toString(endpointUrls);
	}
}
